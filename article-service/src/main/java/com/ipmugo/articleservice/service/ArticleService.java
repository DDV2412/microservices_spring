package com.ipmugo.articleservice.service;

import com.ipmugo.articleservice.repository.AuthorRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.Key;
import org.jbibtex.StringValue;
import com.ipmugo.articleservice.dto.*;
import com.ipmugo.articleservice.event.*;
import com.ipmugo.articleservice.model.*;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_dc.OaiDcType;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_marc.OaiMarc;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_marc.Varfield;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.ListRecordsType;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.OAIPMHtype;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.RecordType;
import com.ipmugo.articleservice.schema.purl.dc.elements.ElementType;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ipmugo.articleservice.repository.ArticleRepository;
import com.ipmugo.articleservice.utils.CustomException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Element;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final AuthorRepository authorRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private JournalService journalService;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private WebClient.Builder webClientWithOutBuilder;

    @Value("${scopus.api.key.secret}")
    private String apiKey;


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, ArticleEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, UpdateCounter> kafkaUpdate;
    /**
     * Save Article
     * */
    @Async
    public Article createArticle(ArticleRequest articleRequest) throws CustomException {
        try{

            Journal journal = journalService.getJournal(articleRequest.getJournalAbbreviation());

            if(journal == null){
                ResponseJournalService journalRequest = webClientBuilder.build().get()
                        .uri("http://journal-service/api/journal/"+ articleRequest.getJournalAbbreviation())
                        .retrieve()
                        .bodyToMono(ResponseJournalService.class)
                        .block();

                if(journalRequest == null){
                    throw new CustomException("Journal with id " + articleRequest.getJournalAbbreviation() + " not found", HttpStatus.NOT_FOUND);
                }

                Journal journalBuilder = Journal.builder()
                        .id(journalRequest.getData().getId())
                        .name(journalRequest.getData().getName())
                        .issn(journalRequest.getData().getIssn())
                        .e_issn(journalRequest.getData().getE_issn())
                        .journalSite(journalRequest.getData().getJournalSite())
                        .abbreviation(journalRequest.getData().getAbbreviation())
                        .publisher(journalRequest.getData().getPublisher())
                        .build();

                journal = journalService.saveJournal(journalBuilder);
            }

            Set<Author> authorSet = new HashSet<>();

            for(Author author: articleRequest.getAuthors()){
                authorSet.add(authorService.addAuthor(author));
            }

            Article article = Article.builder()
                    .journal(journal)
                    .ojsId(articleRequest.getOjsId())
                    .title(articleRequest.getTitle())
                    .pages(articleRequest.getPages())
                    .publishYear(articleRequest.getPublishYear())
                    .publishDate(articleRequest.getPublishDate())
                    .lastModifier(articleRequest.getLastModifier())
                    .doi(articleRequest.getDoi())
                    .volume(articleRequest.getVolume())
                    .issue(articleRequest.getIssue())
                    .copyright(articleRequest.getCopyright())
                    .abstractText(articleRequest.getAbstractText())
                    .fullText(articleRequest.getFullText())
                    .articlePdf(articleRequest.getArticlePdf())
                    .keywords(articleRequest.getKeywords())
                    .authors(authorSet)
                    .publishStatus(articleRequest.getPublishStatus())
                    .citationByCrossRef(articleRequest.getCitationByCrossRef())
                    .citationByScopus(articleRequest.getCitationByScopus())
                    .figures(articleRequest.getFigures())
                    .build();


            Article save = articleRepository.save(article);

            /**
             * Build Article For Kafka template
             * */

            JournalEvent journalEvent = JournalEvent.builder()
                    .id(save.getJournal().getId())
                    .name(save.getJournal().getName())
                    .issn(save.getJournal().getIssn())
                    .e_issn(save.getJournal().getE_issn())
                    .publisher(save.getJournal().getPublisher())
                    .abbreviation(save.getJournal().getAbbreviation())
                    .journalSite(save.getJournal().getJournalSite())
                    .scopusIndex(save.getJournal().isScopusIndex())
                    .build();


            List<KeywordEvent> keywordEvents = new ArrayList<>();

            if(save.getKeywords().size() > 0){
                for(Keyword keyword: save.getKeywords()){
                    KeywordEvent keywordEvent = KeywordEvent.builder()
                            .name(keyword.getName()).build();

                    keywordEvents.add(keywordEvent);
                }
            }


            HashSet<AuthorEvent> authorEvents = new HashSet<>();
            if(save.getAuthors().size() > 0){
                for(Author author: save.getAuthors()){
                    AuthorEvent authorEvent = AuthorEvent.builder()
                            .id(author.getId())
                            .firstName(author.getFirstName())
                            .lastName(author.getLastName())
                            .email(author.getEmail())
                            .affiliation(author.getAffiliation())
                            .orcid(author.getOrcid())
                    .build();

                    authorEvents.add(authorEvent);
                }
            }

            kafkaTemplate.send("article", ArticleEvent.builder()
                            .id(save.getId())
                            .journal(journalEvent)
                            .ojsId(save.getOjsId())
                            .title(save.getTitle())
                            .pages(save.getPages())
                            .publishYear(save.getPublishYear())
                            .lastModifier(save.getLastModifier())
                            .publishDate(save.getPublishDate())
                            .doi(save.getDoi())
                            .volume(save.getVolume())
                            .issue(save.getIssue())
                            .publishStatus(save.getPublishStatus())
                            .abstractText(save.getAbstractText())
                    .articlePdf(save.getArticlePdf())
                    .keywords(keywordEvents)
                    .authors(authorEvents)
                    .citationByScopus(save.getCitationByScopus())
                    .citationByCrossRef(save.getCitationByCrossRef())
                    .figures(save.getFigures())
                    .viewsCount(save.getViewsCount())
                    .downloadCount(save.getDownloadCount())
                    .build());

            return save;
        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update Article
     * */
    @Async
    public Article updateArticle(String id, ArticleRequest articleRequest) throws CustomException {
        try{
            Article article = this.getArticle(id);

            Journal journal = journalService.getJournal(articleRequest.getJournalAbbreviation());

            if(journal == null){
                ResponseJournalService journalRequest = webClientBuilder.build().get()
                        .uri("http://journal-service/api/journal/"+ articleRequest.getJournalAbbreviation())
                        .retrieve()
                        .bodyToMono(ResponseJournalService.class)
                        .block();

                if(journalRequest == null){
                    throw new CustomException("Journal with " + articleRequest.getJournalAbbreviation() + " not found", HttpStatus.NOT_FOUND);
                }

                Journal journalBuilder = Journal.builder()
                        .id(journalRequest.getData().getId())
                        .name(journalRequest.getData().getName())
                        .issn(journalRequest.getData().getIssn())
                        .e_issn(journalRequest.getData().getE_issn())
                        .journalSite(journalRequest.getData().getJournalSite())
                        .abbreviation(journalRequest.getData().getAbbreviation())
                        .publisher(journalRequest.getData().getPublisher())
                        .build();

                journal = journalService.saveJournal(journalBuilder);
            }

            Set<Author> authorSet = new HashSet<>();

            for(Author author: articleRequest.getAuthors()){
                authorSet.add(authorService.addAuthor(author));
            }

            article.setJournal(journal);
            article.setOjsId(articleRequest.getOjsId());
            article.setTitle(articleRequest.getTitle());
            article.setPages(articleRequest.getPages());
            article.setPublishYear(articleRequest.getPublishYear());
            article.setPublishDate(articleRequest.getPublishDate());
            article.setLastModifier(articleRequest.getLastModifier());
            article.setDoi(articleRequest.getDoi());
            article.setVolume(articleRequest.getVolume());
            article.setIssue(articleRequest.getIssue());
            article.setCopyright(articleRequest.getCopyright());
            article.setAbstractText(articleRequest.getAbstractText());
            article.setFullText(articleRequest.getFullText());
            article.setArticlePdf(articleRequest.getArticlePdf());
            article.setKeywords(articleRequest.getKeywords());
            article.setAuthors(authorSet);
            article.setFigures(articleRequest.getFigures());
            article.setPublishStatus(articleRequest.getPublishStatus());

            Article save = articleRepository.save(article);

            /**
             * Build Article For Kafka template
             * */

            JournalEvent journalEvent = JournalEvent.builder()
                    .id(save.getJournal().getId())
                    .name(save.getJournal().getName())
                    .issn(save.getJournal().getIssn())
                    .e_issn(save.getJournal().getE_issn())
                    .publisher(save.getJournal().getPublisher())
                    .abbreviation(save.getJournal().getAbbreviation())
                    .journalSite(save.getJournal().getJournalSite())
                    .scopusIndex(save.getJournal().isScopusIndex())
                    .build();


            List<KeywordEvent> keywordEvents = new ArrayList<>();

            if(save.getKeywords().size() > 0){
                for(Keyword keyword: save.getKeywords()){
                    KeywordEvent keywordEvent = KeywordEvent.builder()
                            .name(keyword.getName()).build();

                    keywordEvents.add(keywordEvent);
                }
            }


            HashSet<AuthorEvent> authorEvents = new HashSet<>();
            if(save.getAuthors().size() > 0){
                for(Author author: save.getAuthors()){
                    AuthorEvent authorEvent = AuthorEvent.builder()
                            .id(author.getId())
                            .firstName(author.getFirstName())
                            .lastName(author.getLastName())
                            .email(author.getEmail())
                            .affiliation(author.getAffiliation())
                            .orcid(author.getOrcid())
                            .build();

                    authorEvents.add(authorEvent);
                }
            }

            kafkaTemplate.send("article", ArticleEvent.builder()
                    .id(save.getId())
                    .journal(journalEvent)
                    .ojsId(save.getOjsId())
                    .title(save.getTitle())
                    .pages(save.getPages())
                    .publishYear(save.getPublishYear())
                    .lastModifier(save.getLastModifier())
                    .publishDate(save.getPublishDate())
                    .doi(save.getDoi())
                    .volume(save.getVolume())
                    .issue(save.getIssue())
                    .publishStatus(save.getPublishStatus())
                    .abstractText(save.getAbstractText())
                    .articlePdf(save.getArticlePdf())
                    .keywords(keywordEvents)
                    .authors(authorEvents)
                    .citationByScopus(save.getCitationByScopus())
                    .citationByCrossRef(save.getCitationByCrossRef())
                    .figures(save.getFigures())
                    .viewsCount(save.getViewsCount())
                    .downloadCount(save.getDownloadCount())
                    .build());

            return save;
        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get List Article
     * */
    public Page<Article> getAllArticle(Pageable pageable, String searchTerm)throws CustomException{
        try {
            if(searchTerm == null || StringUtils.isBlank(searchTerm)){
                return articleRepository.findAll(pageable);
            }
            return articleRepository.searchTerm(searchTerm, pageable);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Article by ID
     * */
    public Article getArticle(String doi) throws CustomException{
        try {
            Optional<Article> article = articleRepository.findByDoi(doi);

            if (article.isEmpty()) {
                throw new CustomException("Article with " + doi +" not found", HttpStatus.NOT_FOUND);
            }
            return article.get();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Delete Article By Id
     * */
    public void deleteArticle(String id)throws CustomException{
        try{

            articleRepository.deleteById(id);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
  }

    /**
     * Get Article By Current Issue Journal Id
     * */
    public Iterable<Article> getCurrentIssue(String id) throws CustomException{
        try {
            return articleRepository.findByJournalIdOrderByIssueDescPublishDateDesc(id);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Harvest Article With Journal id or ISSN
     * Multiple Method
     * Protocol AOI DC
     */
    @Async
    public void getOaiPmh(String abbreviation, String protocolType, String startDate, String untilDate) throws CustomException {
       try{

           Journal journal = journalService.getJournal(abbreviation);

           if(journal == null){
               ResponseJournalService journalRequest = webClientBuilder.build().get()
                       .uri("http://journal-service/api/journal/"+abbreviation)
                       .retrieve()
                       .bodyToMono(ResponseJournalService.class)
                       .block();

               if(journalRequest == null || journalRequest.getData() == null){
                   throw new CustomException("Journal with " + abbreviation + " not found", HttpStatus.NOT_FOUND);
               }

               Journal journalBuilder = Journal.builder()
                       .id(journalRequest.getData().getId())
                       .name(journalRequest.getData().getName())
                       .issn(journalRequest.getData().getIssn())
                       .e_issn(journalRequest.getData().getE_issn())
                       .journalSite(journalRequest.getData().getJournalSite())
                       .abbreviation(journalRequest.getData().getAbbreviation())
                       .publisher(journalRequest.getData().getPublisher())
                       .build();

               journal = journalService.saveJournal(journalBuilder);
           }


           WebClient webClient = WebClient.builder()
                   .baseUrl(journal.getJournalSite() + "/oai")
                   .clientConnector(
                           new ReactorClientHttpConnector(HttpClient.create().secure(
                                   sslContextSpec -> {
                                       try {
                                           sslContextSpec.sslContext(
                                                   SslContextBuilder.forClient()
                                                           .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                                           .build()
                                           );
                                       } catch (SSLException e) {
                                           throw new RuntimeException(e);
                                       }
                                   }
                           ))
                   )
                   .codecs(codecs->codecs
                           .defaultCodecs()
                           .maxInMemorySize(1024 * 1024 * 256))
                   .build();

           OAIPMHtype oaipmHtype = webClient.get()
                   .uri("?verb=ListRecords&metadataPrefix=" + protocolType +"&set="+ journal.getAbbreviation() + "&from="+ startDate +"&until=" + untilDate)
                   .retrieve()
                   .bodyToMono(OAIPMHtype.class)
                   .timeout(Duration.ofSeconds(1000)).block();

           if (oaipmHtype != null && oaipmHtype.getError().isEmpty()){
               ListRecordsType listRecordsType = oaipmHtype.getListRecords();

               if(listRecordsType != null && !listRecordsType.getRecord().isEmpty()){

                   if(protocolType.equals("oai_dc")){
                       this.getDcType(journal, listRecordsType.getRecord());
                   }

                   if(protocolType.equals("oai_marc")){
                       this.getMarcType(journal, listRecordsType.getRecord());
                   }

                   if(listRecordsType.getResumptionToken() != null && listRecordsType.getResumptionToken().getExpirationDate() != null){
                       this.resumptionToken(journal, protocolType, listRecordsType.getResumptionToken().getValue());
                   }

               }
           }

       }catch (Exception e){
           throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
       }
    }

    /**
     * Harvest Article With Journal id or ISSN
     * Next Page with resumptionToken
     * Protocol AOI DC
     */
    @Async
    private void resumptionToken(Journal journal, String protocolType, String token) throws CustomException {
        try{

            WebClient webClient = WebClient.builder()
                    .baseUrl(journal.getJournalSite() + "/oai")
                    .codecs(codecs->codecs
                            .defaultCodecs()
                            .maxInMemorySize(1024 * 1024 * 256))
                    .build();

            OAIPMHtype oaipmHtype = webClient.get()
                    .uri("?verb=ListRecords&resumptionToken=" + token)
                    .retrieve()
                    .bodyToMono(OAIPMHtype.class)
                    .timeout(Duration.ofSeconds(100)).block();

            if (oaipmHtype != null && oaipmHtype.getError().isEmpty()){
                ListRecordsType listRecordsType = oaipmHtype.getListRecords();

                if(listRecordsType != null && !listRecordsType.getRecord().isEmpty()){

                    if(protocolType.equals("oai_dc")){
                        this.getDcType(journal, listRecordsType.getRecord());
                    }

                    if(protocolType.equals("oai_marc")){
                        this.getMarcType(journal, listRecordsType.getRecord());
                    }

                    if(listRecordsType.getResumptionToken() != null && listRecordsType.getResumptionToken().getExpirationDate() != null){
                        this.resumptionToken(journal, protocolType, listRecordsType.getResumptionToken().getValue());
                    }

                }
            }

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Harvest Article With Journal id or ISSN
     * Parsing article with DC Protocol
     */
    @Async
    private void getDcType(Journal journal, List<RecordType> recordTypes) throws CustomException {
        try{
            for(RecordType recordType: recordTypes){
                if(recordType.getHeader().getStatus() != null && !recordType.getHeader().getStatus().value().isEmpty()){
                    continue;
                }

                JAXBContext jc = JAXBContext.newInstance(OaiDcType.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();

                if(recordType.getMetadata() == null){
                    continue;
                }

                Element element = (Element) recordType.getMetadata().getAny();

                JAXBElement<OaiDcType> oaiElement = unmarshaller.unmarshal(element, OaiDcType.class);

                /**
                 * Get Identifier OJS ID
                 * */
                String ojsId = recordType.getHeader().getIdentifier();

                if(!ojsId.isEmpty() && ojsId.split("/").length >= 1){
                    ojsId = ojsId.split("/")[1];
                }

                /**
                 * Get Last Modifier from OJS
                 * */
                String lastModifier = recordType.getHeader().getDatestamp();

                /**
                 * Get Article Title
                 * */
                String title = oaiElement.getValue().getTitle().getValue().getValue();

                /**
                 * Get Article Source
                 * Parse with array string
                 * */
                String source = oaiElement.getValue().getSource().get(0).getValue().getValue();

                String pages = null;

                String volume = null;

                String issue = null;

                if (!source.isEmpty()) {
                    String[] stringList = source.split(";");

                    if (stringList.length == 3) {
                        pages = stringList[2].trim();

                        Pattern pattern = Pattern.compile("Vol\\s*(\\d+),\\s*No\\s*(\\d+)");
                        Matcher matcher = pattern.matcher(stringList[1]);

                        if (matcher.find()) {
                            volume = matcher.group(1);
                            issue = matcher.group(2);
                        }
                    }
                }


                String publishDate = oaiElement.getValue().getDate().getValue().getValue();

                String publishYear = publishDate.split("-")[0];

                String doi = null;

                String copyright = null;


                /**
                 * Check Identifier size more than 1
                 * */
                if (oaiElement.getValue().getIdentifier() != null && !oaiElement.getValue().getIdentifier().isEmpty() && oaiElement.getValue().getIdentifier().size() > 0) {
                    doi = oaiElement.getValue().getIdentifier().get(oaiElement.getValue().getIdentifier().size() - 1).getValue().getValue();
                }

                /**
                 * Check Copy Rights size more than 1
                 * */
                if (oaiElement.getValue().getRights() != null && !oaiElement.getValue().getRights().isEmpty() && oaiElement.getValue().getRights().size() > 0) {
                    copyright = oaiElement.getValue().getRights().get(oaiElement.getValue().getRights().size() - 1).getValue().getValue();
                }

                String abstractText = null;

                /**
                 * Check Abstrack include DOI
                 * */
                if (oaiElement.getValue().getDescription() != null && !oaiElement.getValue().getDescription().getValue().getValue().isEmpty() && oaiElement.getValue().getDescription().getValue().getValue().split("DOI:").length >= 2) {
                    abstractText = oaiElement.getValue().getDescription().getValue().getValue().split("DOI:")[0];
                    String[] doiArray = oaiElement.getValue().getDescription().getValue().getValue().split("DOI:")[1].split("doi.org/");
                    if (doiArray.length >= 2) {
                        doi = doiArray[1];
                    } else if (doiArray.length == 1) {
                        doi = doiArray[0];
                    }
                }else{
                    abstractText = oaiElement.getValue().getDescription().getValue().getValue();
                }


                String articlePdf = null;

                if(oaiElement.getValue().getRelation() != null){
                    articlePdf = oaiElement.getValue().getRelation().get(0).getValue().getValue().replaceAll("/view/", "/download/");
                }

                String keyword = null;

                if(oaiElement.getValue().getSubject() != null){
                    keyword = oaiElement.getValue().getSubject().getValue().getValue();
                }

                List<Keyword> keywordList = new ArrayList<>();

                if(keyword != null && !keyword.isEmpty()){
                    keyword = keyword.replaceAll("; ", ";").replaceAll(", ", ";").replaceAll(",", ";");

                    String[] keywords = keyword.split(";");

                    for (String value : keywords) {
                        Keyword keyword1 = Keyword.builder()
                                .name(value)
                                .build();

                        keywordList.add(keyword1);
                    }
                }

                List<JAXBElement<ElementType>> creators = oaiElement.getValue().getCreator();

                Set<Author> authors = new HashSet<>();

                if(!creators.isEmpty()){
                    for(JAXBElement<ElementType> creator : creators){
                        String value = creator.getValue().getValue();
                        if(!value.isEmpty()){
                            String firstName;
                            String lastName = value.split(", ")[0];
                            String affiliation = null;
                            if(value.split(", ")[1].split("; ").length > 1){
                                affiliation = value.split(", ")[1].split("; ")[1];
                                firstName = value.split(", ")[1].split("; ")[0];
                            }else{
                                firstName = value.split(", ")[1];
                            }

                            Author saveAuthor = Author.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .affiliation(affiliation)
                                    .build();

                            authors.add(authorService.addAuthor(saveAuthor));

                        }
                    }
                }

                /**
                 * Build Article
                 * */
                String publishStatus = "publish";

                if(doi!= null && !doi.contains("%")){

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentDate = new Date();

                    if(dateFormat.parse(publishDate).after(currentDate)){
                        publishStatus = "early access";
                    }

                    ArticleRequest articleBuild = ArticleRequest.builder()
                            .journalAbbreviation(journal.getAbbreviation())
                            .ojsId(ojsId)
                            .lastModifier(lastModifier)
                            .title(title)
                            .pages(pages)
                            .publishDate(publishDate)
                            .publishYear(publishYear)
                            .doi(doi)
                            .volume(volume)
                            .issue(issue)
                            .copyright(copyright)
                            .abstractText(abstractText)
                            .articlePdf(articlePdf)
                            .keywords(keywordList)
                            .publishStatus(publishStatus)
                            .authors(authors)
                            .build();

                    Optional<Article> articleCheck = articleRepository.findByDoi(doi);

                    if(articleCheck.isEmpty()){
                       this.createArticle(articleBuild);

                    }else {
                        if(!Objects.equals(articleCheck.get().getLastModifier(), lastModifier)){

                            this.updateArticle(articleCheck.get().getId(), articleBuild);

                        }
                    }
                }

            }
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Harvest Article With Journal id or ISSN
     * Parsing article with Marc Protocol
     */
    @Async
    private void getMarcType(Journal journal,  List<RecordType> recordTypes) throws CustomException {
        try{
            for(RecordType recordType: recordTypes){
                if(recordType.getHeader().getStatus() != null && !recordType.getHeader().getStatus().value().isEmpty()){
                    continue;
                }

                JAXBContext jc = JAXBContext.newInstance(OaiMarc.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();

                if(recordType.getMetadata() == null){
                    continue;
                }

                Element element = (Element) recordType.getMetadata().getAny();

                JAXBElement<OaiMarc> oaiElement = unmarshaller.unmarshal(element, OaiMarc.class);

                /**
                 * Get Identifier OJS ID
                 * */
                String ojsId = recordType.getHeader().getIdentifier();

                if(!ojsId.isEmpty() && ojsId.split("/").length >= 1){
                    ojsId = ojsId.split("/")[1];
                }

                /**
                 * Get Last Modifier from OJS
                 * */
                String lastModifier = recordType.getHeader().getDatestamp();


                List<Keyword> keywordList = new ArrayList<>();

                String title = null;

                String abstractText = null;

                String doi = null;

                String publishDate = null;

                String publishYear = null;

                String volume = null;

                String issue = null;

                Set<Author> authors = new HashSet<>();

                String publishStatus = "publish";

                /**
                 * Parsing varfield
                 * */
                if(!oaiElement.getValue().getVarfield().isEmpty()){
                    for(Varfield varfield: oaiElement.getValue().getVarfield()) {
                        /**
                         * Get Article Title
                         * ID 245
                         * Label a
                         * */
                        if(Objects.equals(varfield.getId(), "245") && Objects.equals(varfield.getI1(), "0") && Objects.equals(varfield.getI2(), "0") && Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                            title = varfield.getSubfield().get(0).getValue();
                        }

                        /**
                         * Get abstract
                         * ID 520
                         * Label a
                         * */
                        if(Objects.equals(varfield.getId(), "520") && Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                            if (varfield.getSubfield().get(0).getValue() != null && !varfield.getSubfield().get(0).getValue().isEmpty() && varfield.getSubfield().get(0).getValue().split("DOI:").length >= 2) {
                                abstractText = varfield.getSubfield().get(0).getValue().split("DOI:")[0];
                                String[] doiArray = varfield.getSubfield().get(0).getValue().split("DOI:")[1].split("doi.org/");
                                if (doiArray.length >= 2) {
                                    doi = doiArray[1];
                                } else if (doiArray.length == 1) {
                                    doi = doiArray[0];
                                }
                            }else{
                                abstractText = varfield.getSubfield().get(0).getValue();
                            }
                        }

                        /**
                         * Get Publish Date
                         * ID 260
                         * Label c
                         * */
                        if(Objects.equals(varfield.getId(), "260") && Objects.equals(varfield.getSubfield().get(0).getLabel(), "c")){
                            publishDate = varfield.getSubfield().get(0).getValue();

                            if(publishDate != null && !publishDate.isEmpty()){
                                publishYear = publishDate.split("-")[0];
                            }
                        }

                        /**
                         * Get Volume and Issue
                         * ID 786
                         * ind1 0
                         * Label n
                         * */
                        if(Objects.equals(varfield.getId(), "786") && Objects.equals(varfield.getI1(), "0") && Objects.equals(varfield.getSubfield().get(0).getLabel(), "n")){
                            String[] stringList = varfield.getSubfield().get(0).getValue().split(";");

                            /**
                             * Get Data Volume From StringList After Split
                             * */
                            if(stringList.length == 2){
                                Pattern pattern = Pattern.compile("Vol\\\\s+(\\\\d+), No\\\\s+(\\\\d+)");
                                Matcher matcher = pattern.matcher(stringList[1]);

                                if (matcher.find()) {
                                    volume = matcher.group(1);
                                    issue = matcher.group(2);
                                }
                            }
                        }

                        /**
                         * Get KeywordEvent
                         * ID 653
                         * Label a
                         * */
                        if(Objects.equals(varfield.getId(), "653") && Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                            String keyword = varfield.getSubfield().get(0).getValue();

                            if(keyword != null && !keyword.isEmpty()){
                                keyword = keyword.replaceAll("; ", ";").replaceAll(", ", ";").replaceAll(",", ";");

                                String[] keywords = keyword.split(";");

                                for (String value : keywords) {
                                    Keyword keyword1 = Keyword.builder().name(value).build();


                                    keywordList.add(keyword1);
                                }
                            }
                        }

                        /**
                         * Get AuthorEvent
                         * ID 720
                         * Id1 1
                         * Label a
                         * */
                        if(Objects.equals(varfield.getId(), "720") && Objects.equals(varfield.getI1(), "1")){
                            if(varfield.getSubfield() != null){
                                Author author = new Author();
                                if(varfield.getSubfield().size() == 3){
                                    if(Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                                        author.setFirstName(varfield.getSubfield().get(0).getValue().split(", ")[1]);
                                        author.setLastName(varfield.getSubfield().get(0).getValue().split(", ")[0]);
                                    }

                                    if(Objects.equals(varfield.getSubfield().get(1).getLabel(), "u")){
                                        author.setAffiliation(varfield.getSubfield().get(1).getValue());
                                    }

                                    if(Objects.equals(varfield.getSubfield().get(2).getLabel(), "0")){
                                        author.setOrcid(varfield.getSubfield().get(2).getValue());
                                    }
                                }else if(varfield.getSubfield().size() == 2){
                                    if(Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                                        author.setFirstName(varfield.getSubfield().get(0).getValue().split(", ")[1]);
                                        author.setLastName(varfield.getSubfield().get(0).getValue().split(", ")[0]);
                                    }

                                    if(Objects.equals(varfield.getSubfield().get(1).getLabel(), "u")){
                                        author.setAffiliation(varfield.getSubfield().get(1).getValue());
                                    }
                                }else{
                                    if(Objects.equals(varfield.getSubfield().get(0).getLabel(), "a")){
                                        author.setFirstName(varfield.getSubfield().get(0).getValue().split(", ")[1]);
                                        author.setLastName(varfield.getSubfield().get(0).getValue().split(", ")[0]);
                                    }
                                }

                                authors.add(authorService.addAuthor(author));
                            }
                        }

                    }
                }

                /**
                 * Build Article
                 * */

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date currentDate = new Date();

                if(dateFormat.parse(publishDate).after(currentDate)){
                    publishStatus = "early access";
                }

                ArticleRequest articleBuild = ArticleRequest.builder()
                        .journalAbbreviation(journal.getAbbreviation())
                        .ojsId(ojsId)
                        .lastModifier(lastModifier)
                        .title(title)
                        .publishDate(publishDate)
                        .publishYear(publishYear)
                        .volume(volume)
                        .issue(issue)
                        .abstractText(abstractText)
                        .keywords(keywordList)
                        .authors(authors)
                        .publishStatus(publishStatus)
                        .doi(doi)
                        .build();

                Optional<Article> articleCheck = articleRepository.findByTitleAndOjsId(title, ojsId);

                if(articleCheck.isEmpty()){
                  this.createArticle(articleBuild);

                }else {
                    if(!Objects.equals(articleCheck.get().getLastModifier(), lastModifier)){

                        this.updateArticle(articleCheck.get().getId(), articleBuild);
                    }
                }
            }
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }



    public void citationScopus(String doi) throws CustomException{
        try {
            CitationScopus citationScopus = webClientWithOutBuilder.build().get()
                    .uri("https://api.elsevier.com/content/search/scopus?query=DOI(" + doi + ")")
                    .header("X-ELS-APIKey", apiKey)
                    .retrieve()
                    .bodyToMono(CitationScopus.class)
                    .block();

            Optional<Article> article = articleRepository.findByDoi(doi);

            if(article.isEmpty()){
                throw new CustomException("Article with doi " + doi + " not found", HttpStatus.NOT_FOUND);
            }

            if(citationScopus == null){
                article.get().setCitationByScopus(0);

                articleRepository.save(article.get());

                throw new CustomException("Article with doi " + doi + " Citation not found", HttpStatus.NOT_FOUND);
            }

            SearchResults searchResults = citationScopus.getSearchResults();

            if(searchResults.getEntry().isEmpty()){
                throw new CustomException("Article with doi " + doi + " Citation not found", HttpStatus.NOT_FOUND);
            }

            article.get().setCitationByScopus(Integer.valueOf(searchResults.getEntry().get(0).getCitedbyCount()));

             articleRepository.save(article.get());


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void citationCrossRef(String doi) throws CustomException{
        try {
            CitationCrossRef citationCrossRef = webClientWithOutBuilder.build().get()
                    .uri("https://api.crossref.org/works/"+doi)
                    .retrieve()
                    .bodyToMono(CitationCrossRef.class)
                    .block();

            Optional<Article> article = articleRepository.findByDoi(doi);

            if(article.isEmpty()){
                throw new CustomException("Article with doi " + doi + " not found", HttpStatus.NOT_FOUND);
            }

            if(citationCrossRef == null){
                article.get().setCitationByCrossRef(0);

                articleRepository.save(article.get());

                throw new CustomException("Article with doi " + doi + " Citation not found", HttpStatus.NOT_FOUND);
            }


            article.get().setCitationByCrossRef(citationCrossRef.getMessage().getReferencesCount());



           articleRepository.save(article.get());




        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Featured articles by citation
     * */
    public Iterable<Article> featuredArticles()throws CustomException{
        try {
            return articleRepository.findTop3ByOrderByCitationByScopusDescCitationByCrossRefDesc();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Statistic
     * */
    public HashMap<String, Long> statistic()throws CustomException{
        try {
            HashMap<String, Long> statistic = new HashMap<>();

            statistic.put("articleCount", articleRepository.count());

            statistic.put("authorCount", authorRepository.count());

            return statistic;
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update Set Counter
     */
    public void setCounterUpdate(SetCounterEvent setCounterEvent) throws CustomException {
        try{
            Article article = this.getArticle(setCounterEvent.getDoi());

            if(setCounterEvent.getStatus().equals(Status.Download)){
                article.setDownloadCount(setCounterEvent.getCount());
            }

            if(setCounterEvent.getStatus().equals(Status.View)){
                article.setDownloadCount(setCounterEvent.getCount());
            }


            Article save = articleRepository.save(article);

            /**
             * Build Article For Kafka template
             * */


            kafkaUpdate.send("setUpdate", UpdateCounter.builder()
                    .articleId(save.getId())
                    .citationByScopus(save.getCitationByScopus())
                    .citationByCrossRef(save.getCitationByCrossRef())
                    .viewsCount(save.getViewsCount())
                    .downloadCount(save.getDownloadCount())
                    .build());

        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     *
     * Citation BibText
     */
    public String citationBibText(String id) throws CustomException{
        try{
            Optional<Article> article = articleRepository.findById(id);

            if (article.isEmpty()) {
                throw new CustomException("Article with " + id +" not found", HttpStatus.NOT_FOUND);
            }

            BibTeXDatabase database = new BibTeXDatabase();
            BibTeXEntry entry = new BibTeXEntry(BibTeXEntry.TYPE_ARTICLE,
                    new Key(article.get().getAuthors().iterator().next().getFirstName() + "@"
                            + article.get().getPublishYear()));
            entry.addField(BibTeXEntry.KEY_TITLE, new StringValue(article.get().getTitle(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_JOURNAL,
                    new StringValue(article.get().getJournal().getName(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_YEAR,
                    new StringValue(article.get().getPublishYear(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_VOLUME, new StringValue(article.get().getVolume(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_DOI, new StringValue(article.get().getDoi(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_PAGES, new StringValue(article.get().getPages(), StringValue.Style.BRACED));
            entry.addField(BibTeXEntry.KEY_NUMBER, new StringValue(article.get().getIssue(), StringValue.Style.BRACED));

            StringBuilder keyAuthorBuilder = new StringBuilder();
            for (int i = 0; i < article.get().getAuthors().size(); i++) {
                Author author = article.get().getAuthors().iterator().next();
                keyAuthorBuilder.append(author.getFirstName()).append(" ").append(author.getLastName());
                if (i < article.get().getAuthors().size() - 1) {
                    keyAuthorBuilder.append(" and ");
                }
            }

            entry.addField(BibTeXEntry.KEY_AUTHOR,
                    new StringValue(keyAuthorBuilder.toString(), StringValue.Style.BRACED));
            database.addObject(entry);

            BibTeXFormatter formatter = new BibTeXFormatter();
            StringWriter stringWriter = new StringWriter();
            formatter.format(database, stringWriter);
            return stringWriter.toString();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     *
     * GetFull Text Article
     */
    @Async
    public void fullText(String id) throws CustomException{
        try{
            Article article = this.getArticle(id);

        if(article.getArticlePdf() != null && !article.getArticlePdf().contains("downloadSuppFile") && !article.getArticlePdf().contains("info") && article.getFullText() == null){
            URL url = new URL(article.getArticlePdf());

            InputStream inputStream = url.openStream();

            PDDocument document = Loader.loadPDF(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            String[] lines = text.split("\n");
            StringBuilder paragraph = new StringBuilder();
            String startElement = "INTRODUCTION";
            String endElement = "BIOGRAPHIES OF AUTHORS";
            boolean foundStart = false;
            boolean foundEnd = false;

            for (String line : lines) {
                if (line.contains(startElement)) {
                    foundStart = true;
                } else if (line.contains(endElement)) {
                    foundEnd = true;
                    break;
                } else if (foundStart) {
                    paragraph.append(line).append("\n");
                }
            }
            article.setFullText(paragraph.toString());

            articleRepository.save(article);
        }
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    public  List<String> multipleCitationBibText(int count) throws CustomException{
        try{
            Pageable pageable = PageRequest.of(0, count);

            Iterable<Article> articles = articleRepository.findAll();

            List<String> export = new ArrayList<>();

            for(Article article: articles){
                BibTeXDatabase database = new BibTeXDatabase();
                BibTeXEntry entry = new BibTeXEntry(BibTeXEntry.TYPE_ARTICLE,
                        new Key(article.getAuthors().iterator().next().getFirstName() + "@"
                                + article.getPublishYear()));
                entry.addField(BibTeXEntry.KEY_TITLE, new StringValue(article.getTitle(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_JOURNAL,
                        new StringValue(article.getJournal().getName(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_YEAR,
                        new StringValue(article.getPublishYear(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_VOLUME, new StringValue(article.getVolume(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_DOI, new StringValue(article.getDoi(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_PAGES, new StringValue(article.getPages(), StringValue.Style.BRACED));
                entry.addField(BibTeXEntry.KEY_NUMBER, new StringValue(article.getIssue(), StringValue.Style.BRACED));

                StringBuilder keyAuthorBuilder = new StringBuilder();
                for (int i = 0; i < article.getAuthors().size(); i++) {
                    Author author = article.getAuthors().iterator().next();
                    keyAuthorBuilder.append(author.getFirstName()).append(" ").append(author.getLastName());
                    if (i < article.getAuthors().size() - 1) {
                        keyAuthorBuilder.append(" and ");
                    }
                }

                entry.addField(BibTeXEntry.KEY_AUTHOR,
                        new StringValue(keyAuthorBuilder.toString(), StringValue.Style.BRACED));
                database.addObject(entry);

                BibTeXFormatter formatter = new BibTeXFormatter();
                StringWriter stringWriter = new StringWriter();
                formatter.format(database, stringWriter);
                export.add(stringWriter.toString());
            }

            return export;


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

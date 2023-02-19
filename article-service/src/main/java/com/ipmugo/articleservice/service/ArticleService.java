package com.ipmugo.articleservice.service;

import com.ipmugo.articleservice.dto.*;
import com.ipmugo.articleservice.event.*;
import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.model.Keyword;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_dc.OaiDcType;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_marc.OaiMarc;
import com.ipmugo.articleservice.schema.openarchives.oai.oai_marc.Varfield;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.ListRecordsType;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.OAIPMHtype;
import com.ipmugo.articleservice.schema.openarchives.oai.pmh.RecordType;
import com.ipmugo.articleservice.schema.purl.dc.elements.ElementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ipmugo.articleservice.model.Article;
import com.ipmugo.articleservice.repository.ArticleRepository;
import com.ipmugo.articleservice.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

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
    private KafkaTemplate<String, ArticleEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, ArticleAssignEvent> articleAssignEventKafkaTemplate;
    /**
     * Save Article
     * */
    public Article createArticle(ArticleRequest articleRequest) throws CustomException {
        try{

            Journal journal = journalService.getJournal(articleRequest.getJournalId());

            if(journal == null){
                ResponseJournalService journalRequest = webClientBuilder.build().get()
                        .uri("http://journal-service/api/journal/"+ articleRequest.getJournalId())
                        .retrieve()
                        .bodyToMono(ResponseJournalService.class)
                        .block();

                if(journalRequest == null){
                    throw new CustomException("Journal with id " + articleRequest.getJournalId() + " not found", HttpStatus.NOT_FOUND);
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

            this.kafkaSend(save);

            return save;
        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update Article
     * */
    public Article updateArticle(String id, ArticleRequest articleRequest) throws CustomException {
        try{
            Article article = this.getArticle(id);

            Journal journal = journalService.getJournal(articleRequest.getJournalId());

            if(journal == null){
                ResponseJournalService journalRequest = webClientBuilder.build().get()
                        .uri("http://journal-service/api/journal/"+ articleRequest.getJournalId())
                        .retrieve()
                        .bodyToMono(ResponseJournalService.class)
                        .block();

                if(journalRequest == null){
                    throw new CustomException("Journal with id " + articleRequest.getJournalId() + " not found", HttpStatus.NOT_FOUND);
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

            this.kafkaSend(save);

            articleAssignEventKafkaTemplate.send("articleAssign", ArticleAssignEvent.builder()
                            .id(save.getId())
                            .journal(save.getJournal().getName())
                            .title(save.getTitle())
                    .publishDate(save.getPublishDate())
                    .volume(save.getVolume())
                    .issue(save.getIssue())
                    .doi(save.getDoi())
                    .articlePdf(save.getArticlePdf())
                    .citationByScopus(save.getCitationByScopus())
                    .citationByCrossRef(save.getCitationByCrossRef())
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
            if(searchTerm == null || searchTerm.isEmpty()){
                return articleRepository.findAll(pageable);
            }

            return articleRepository.searchTerm(pageable, searchTerm);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Article by ID
     * */
    public Article getArticle(String id) throws CustomException{
        try {
            Optional<Article> article = articleRepository.findById(id);

            if (article.isEmpty()) {
                throw new CustomException("Article with " + id +" not found", HttpStatus.NOT_FOUND);
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
            this.getArticle(id);

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
    public void getOaiPmh(UUID id, String protocolType, String startDate, String untilDate) throws CustomException {
       try{

           Journal journal = journalService.getJournal(id);

           if(journal == null){
               ResponseJournalService journalRequest = webClientBuilder.build().get()
                       .uri("http://journal-service/api/journal/"+id)
                       .retrieve()
                       .bodyToMono(ResponseJournalService.class)
                       .block();

               if(journalRequest == null || journalRequest.getData() == null){
                   throw new CustomException("Journal with id " + id + " not found", HttpStatus.NOT_FOUND);
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
                   .codecs(codecs->codecs
                           .defaultCodecs()
                           .maxInMemorySize(1024 * 1024 * 256))
                   .build();

           OAIPMHtype oaipmHtype = webClient.get()
                   .uri("?verb=ListRecords&metadataPrefix=" + protocolType +"&set="+ journal.getAbbreviation() + "&from="+ startDate +"&until=" + untilDate)
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
     * Next Page with resumptionToken
     * Protocol AOI DC
     */
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
                String source = oaiElement.getValue().getSource().getValue().getValue();

                String pages = null;

                String volume = null;

                String issue = null;

                if(!source.isEmpty()){
                    String[] stringList = source.split(";");

                    /**
                     * Get Data Volume From StringList After Split
                     * */
                    if(stringList.length == 3){
                        pages = stringList[2];

                        Pattern pattern = Pattern.compile("Vol\\\\s+(\\\\d+), No\\\\s+(\\\\d+)");
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
                    articlePdf = oaiElement.getValue().getRelation().getValue().getValue();
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
                            .journalId(journal.getId())
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
                        .journalId(journal.getId())
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


    private void kafkaSend(Article article){
        com.ipmugo.articleservice.event.Journal journal = com.ipmugo.articleservice.event.Journal.builder()
                .id(article.getJournal().getId())
                .name(article.getJournal().getName())
                .issn(article.getJournal().getIssn())
                .e_issn(article.getJournal().getE_issn())
                .publisher(article.getJournal().getPublisher())
                .build();

        Set<AuthorEvent> authorEvents = new HashSet<>();

        for(Author author: article.getAuthors()){
           authorEvents.add(AuthorEvent.builder()
                   .id(author.getId())
                   .firstName(author.getFirstName())
                   .lastName(author.getLastName())
                   .affiliation(author.getAffiliation())
                   .build());
        }

        Set<KeywordEvent> keywordEvents = new HashSet<>();

        if(!article.getKeywords().isEmpty()){
            for(Keyword keyword : article.getKeywords()){
                KeywordEvent keywordEvent1 = KeywordEvent.builder()
                        .name(keyword.getName()).build();

                keywordEvents.add(keywordEvent1);
            }
        }

        kafkaTemplate.send("article", ArticleEvent.builder()
                .id(article.getId())
                        .journal(journal)
                        .title(article.getTitle())
                        .pages(article.getPages())
                        .publishYear(article.getPublishYear())
                        .lastModifier(article.getLastModifier())
                        .publishDate(article.getPublishDate())
                        .doi(article.getDoi())
                        .volume(article.getVolume())
                        .issue(article.getIssue())
                        .publishStatus(article.getPublishStatus())
                        .abstractText(article.getAbstractText())
                        .authorEvents(authorEvents)
                        .keywordEvents(keywordEvents)
                        .figures(article.getFigures())
                        .citationByScopus(article.getCitationByScopus())
                        .viewsCount(article.getViewsCount())
                        .downloadCount(article.getDownloadCount())
                        .citationByCrossRef(article.getCitationByCrossRef())
                .build());
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

            Article save = articleRepository.save(article.get());

            kafkaTemplate.send("articleScopusCitation", ArticleEvent.builder()
                    .id(save.getId())
                    .citationByScopus(save.getCitationByScopus())
                    .build());


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



            Article save = articleRepository.save(article.get());

            kafkaTemplate.send("articleCrossRefCitation", ArticleEvent.builder()
                    .id(save.getId())
                    .citationByCrossRef(save.getCitationByCrossRef())
                    .build());


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Featured articles by citation
     * */
    public Iterable<Article> featuredArticles()throws CustomException{
        try {
            return articleRepository.findTop5ByOrderByCitationByScopusDescCitationByCrossRefDesc();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void setCounter(SetCounter setCounter) throws CustomException {
        try{
            Article article = this.getArticle(setCounter.getArticleId());

            article.setViewsCount(setCounter.getViewsCount());
            article.setDownloadCount(setCounter.getDownloadCount());

           Article save = articleRepository.save(article);

           this.kafkaSend(save);
        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

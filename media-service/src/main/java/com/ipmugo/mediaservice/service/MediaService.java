package com.ipmugo.mediaservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ipmugo.mediaservice.model.Document;
import com.ipmugo.mediaservice.model.Image;
import com.ipmugo.mediaservice.repository.DocumentRepository;
import com.ipmugo.mediaservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ipmugo.mediaservice.dto.MediaRequest;
import com.ipmugo.mediaservice.repository.ImageRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    @Value("${spring.client.serverUrl.imageZone}")
    private String imageZone;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Save Single Image
     * */
    public String uploadImage(MediaRequest mediaRequest) throws CustomException {
        try{
            Optional<Image> file = imageRepository.findByFileName(mediaRequest.getFileName());

            if(file.isPresent()){
                throw new CustomException("Image with id" + mediaRequest.getFileName() + " is ready", HttpStatus.FOUND);
            }

            Image image = Image.builder()
                    .fileName(mediaRequest.getFileName())
                    .fileSize(mediaRequest.getFileSize())
                    .fileByte(mediaRequest.getFileByte())
                    .fileType(mediaRequest.getFileType())
                    .build();

            Image save = imageRepository.save(image);

            return imageZone + "/api/file/image-"+save.getId();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Save Single Document
     * */
    public String uploadDocument(MediaRequest mediaRequest) throws CustomException {
        try{
            Optional<Document> file = documentRepository.findByFileName(mediaRequest.getFileName());

            if(file.isPresent()){
                throw new CustomException("Document with id" + mediaRequest.getFileName() + " is ready", HttpStatus.FOUND);
            }

            Document document = Document.builder()
                    .fileName(mediaRequest.getFileName())
                    .fileSize(mediaRequest.getFileSize())
                    .fileByte(mediaRequest.getFileByte())
                    .fileType(mediaRequest.getFileType())
                    .build();

            Document save = documentRepository.save(document);

            return imageZone + "/api/file/view/document-"+save.getId();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Save Multiple Images
     * */
    public List<String> multipleUploadImage(List<MediaRequest> medias) throws CustomException {
        try{
            List<String> mediaList = new ArrayList<>();
            for (MediaRequest mediaRequest : medias) {
                Optional<Image> file = imageRepository.findByFileName(mediaRequest.getFileName());

                if(file.isEmpty()){
                    Image media = Image.builder()
                            .fileName(mediaRequest.getFileName())
                            .fileSize(mediaRequest.getFileSize())
                            .fileByte(mediaRequest.getFileByte())
                            .fileType(mediaRequest.getFileType())
                            .build();

                    Image save = imageRepository.save(media);

                   mediaList.add(imageZone + "/api/file/image-"+save.getId());
                }else{
                    throw new CustomException("Image with id" + mediaRequest.getFileName() + " is ready", HttpStatus.FOUND);
                }
            }
            return mediaList;
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Save Multiple Documents
     * */
    public List<String> multipleUploadDocument(List<MediaRequest> medias) throws CustomException {
        try{
            List<String> mediaList = new ArrayList<>();
            for (MediaRequest mediaRequest : medias) {
                Optional<Document> file = documentRepository.findByFileName(mediaRequest.getFileName());

                if(file.isEmpty()){
                    Document document = Document.builder()
                            .fileName(mediaRequest.getFileName())
                            .fileSize(mediaRequest.getFileSize())
                            .fileByte(mediaRequest.getFileByte())
                            .fileType(mediaRequest.getFileType())
                            .build();

                    Document save = documentRepository.save(document);


                    mediaList.add(imageZone + "/api/file/view/document-"+save.getId());
                }else{
                    throw new CustomException("Document with id" + mediaRequest.getFileName() + " is ready", HttpStatus.FOUND);
                }
            }
            return mediaList;
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Image By Id
     * */
    public Image getImage(UUID id) throws CustomException {
       try{
           Optional<Image> image = imageRepository.findById(id);

           if (image.isEmpty()) {
               throw new CustomException("Image with id" + id + " not found", HttpStatus.NOT_FOUND);
           }

           return image.get();
       } catch (Exception e){
           throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
       }

    }

    /**
     * Get Document By Id
     * */
    public Document getDocument(UUID id) throws CustomException {
        try{
            Optional<Document> document = documentRepository.findById(id);

            if (document.isEmpty()) {
                throw new CustomException("Document with id" + id + " not found", HttpStatus.NOT_FOUND);
            }

            return document.get();
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    /**
     * Get List Images
     * */
    public Page<Image> getAllImage(Pageable pageable) throws CustomException {
        try{
            return imageRepository.findAll(pageable);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get List Documents
     * */
    public Page<Document> getAllDocuments(Pageable pageable) throws CustomException {
        try{
            return documentRepository.findAll(pageable);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete Image By FileName
     * */
    public void deleteImage(UUID id) throws CustomException {
        try{
            this.getImage(id);

            imageRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    /**
     * Delete Document By FileName
     * */
    public void deleteDocument(UUID id) throws CustomException {
        try{
            this.getDocument(id);
            documentRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }
}

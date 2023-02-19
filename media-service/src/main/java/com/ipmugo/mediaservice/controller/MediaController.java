package com.ipmugo.mediaservice.controller;

import java.util.*;

import com.ipmugo.mediaservice.dto.ResponseData;
import com.ipmugo.mediaservice.model.Document;
import com.ipmugo.mediaservice.model.Image;
import com.ipmugo.mediaservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.ipmugo.mediaservice.dto.MediaRequest;
import com.ipmugo.mediaservice.service.MediaService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MediaController {

    @Autowired
    private MediaService mediaService;




    /**
     * Allow Content Type
     * Validation
     * */
    private Set<String> allowImageType () {
        return new HashSet<>(Arrays.asList("image/jpeg", "image/png"));
    }

    private Set<String> allowDocumentType () {
        return new HashSet<>(Arrays.asList("application/pdf","application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    /**
     * Save Single Image
     * */
    @PostMapping("/upload/image")
    public ResponseEntity<ResponseData<String>> uploadImage(@RequestParam("image") MultipartFile file) {
        MediaRequest mediaRequest = new MediaRequest();
        ResponseData<String> responseData = new ResponseData<>();
        try {

            // Validation if file is Empty
            if(file.isEmpty()){
                responseData.setStatus(false);
                responseData.getMessages().add("Please select a file to upload");

                return ResponseEntity.badRequest().body(responseData);
            }

            // Validation if content type not match with allow content type
            if (!this.allowImageType().contains(file.getContentType())) {
                responseData.setStatus(false);
                responseData.getMessages().add("File must be an image/jpg or image/png");
                return ResponseEntity.badRequest().body(responseData);
            }

            mediaRequest.setFileName(file.getOriginalFilename());
            mediaRequest.setFileSize(file.getSize());
            mediaRequest.setFileByte(file.getBytes());
            mediaRequest.setFileType(file.getContentType());

            responseData.setStatus(true);
            responseData.setData(mediaService.uploadImage(mediaRequest));

            return ResponseEntity.ok(responseData);

        } catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }  catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }
    }

    @PostMapping("/upload/document")
    public ResponseEntity<ResponseData<String>> uploadDocument(@RequestParam("document") MultipartFile file) {
        MediaRequest mediaRequest = new MediaRequest();
        ResponseData<String> responseData = new ResponseData<>();
        try {

            // Validation if file is Empty
            if(file.isEmpty()){
                responseData.setStatus(false);
                responseData.getMessages().add("Please select a file to upload");

                return ResponseEntity.badRequest().body(responseData);
            }

            // Validation if content type not match with allow content type
            if (!this.allowDocumentType().contains(file.getContentType())) {
                responseData.setStatus(false);
                responseData.getMessages().add("File must be an PDF or word");
                return ResponseEntity.badRequest().body(responseData);
            }

            mediaRequest.setFileName(file.getOriginalFilename());
            mediaRequest.setFileSize(file.getSize());
            mediaRequest.setFileByte(file.getBytes());
            mediaRequest.setFileType(file.getContentType());

            responseData.setStatus(true);
            responseData.setData(mediaService.uploadDocument(mediaRequest));

            return ResponseEntity.ok(responseData);

        } catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }  catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }
    }

    @PostMapping("/upload/multiple/image")
    public ResponseEntity<ResponseData<List<String>>> multipleUploadImage(@RequestParam("images") List<MultipartFile> files) {

        ResponseData<List<String>> responseData = new ResponseData<>();

        try {
            // Validation if files is Empty
            if(files.isEmpty()){
                responseData.setStatus(false);
                responseData.getMessages().add("Please select a files to upload");

                return ResponseEntity.badRequest().body(responseData);
            }


            // Add Array Media Request for save all
            List<MediaRequest> mediaRequests = new ArrayList<>();
            for (MultipartFile file : files) {
                MediaRequest mediaRequest = new MediaRequest();

                // Validation if content type not match with allow content type
                if (!this.allowImageType().contains(file.getContentType())) {
                    responseData.setStatus(false);
                    responseData.getMessages().add("File must be an image/jpg or image/png");
                    return ResponseEntity.badRequest().body(responseData);
                }

                mediaRequest.setFileName(file.getOriginalFilename());
                mediaRequest.setFileSize(file.getSize());
                mediaRequest.setFileByte(file.getBytes());
                mediaRequest.setFileType(file.getContentType());
                mediaRequests.add(mediaRequest);
            }

            responseData.setStatus(true);
            responseData.setData(mediaService.multipleUploadImage(mediaRequests));

            return ResponseEntity.ok(responseData);

        } catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.badRequest().body(responseData);

        }
    }

    @PostMapping("/upload/multiple/document")
    public ResponseEntity<ResponseData<List<String>>> multipleUploadDocument(@RequestParam("documents") List<MultipartFile> files) {

        ResponseData<List<String>> responseData = new ResponseData<>();

        try {
            // Validation if files is Empty
            if(files.isEmpty()){
                responseData.setStatus(false);
                responseData.getMessages().add("Please select a files to upload");

                return ResponseEntity.badRequest().body(responseData);
            }


            // Add Array Media Request for save all
            List<MediaRequest> mediaRequests = new ArrayList<>();
            for (MultipartFile file : files) {
                MediaRequest mediaRequest = new MediaRequest();

                // Validation if content type not match with allow content type
                if (!this.allowDocumentType().contains(file.getContentType())) {
                    responseData.setStatus(false);
                    responseData.getMessages().add("File must be an PDF or Word");
                    return ResponseEntity.badRequest().body(responseData);
                }

                mediaRequest.setFileName(file.getOriginalFilename());
                mediaRequest.setFileSize(file.getSize());
                mediaRequest.setFileByte(file.getBytes());
                mediaRequest.setFileType(file.getContentType());
                mediaRequests.add(mediaRequest);
            }

            responseData.setStatus(true);
            responseData.setData(mediaService.multipleUploadDocument(mediaRequests));

            return ResponseEntity.ok(responseData);

        } catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.badRequest().body(responseData);

        }
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        try{

            String fileId = id.substring(6);

            Image media = mediaService.getImage(UUID.fromString(fileId));
            return ResponseEntity.ok().contentType(MediaType.valueOf(media.getFileType())).body(media.getFileByte());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/file/view/{id}")
    public ResponseEntity<byte[]> getDocument(@PathVariable("id") String id) {
        try{

            String fileId = id.substring(6);
            Document media = mediaService.getDocument(UUID.fromString(fileId));
            return ResponseEntity.ok().contentType(MediaType.valueOf(media.getFileType())).body(media.getFileByte());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/upload/view/images")
    public ResponseEntity<ResponseData<Page<Image>>> getAllImage(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size) {
        ResponseData<Page<Image>> responseData = new ResponseData<>();

        try{
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
            responseData.setStatus(true);
            responseData.setData(mediaService.getAllImage(pageable));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }

    }

    @GetMapping("/upload/view/documents")
    public ResponseEntity<ResponseData<Page<Document>>> getAllDocuments(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size) {
        ResponseData<Page<Document>> responseData = new ResponseData<>();

        try{
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));

            responseData.setStatus(true);
            responseData.setData(mediaService.getAllDocuments(pageable));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }

    }

    @DeleteMapping("/upload/delete/image/{id}")
    public ResponseEntity<ResponseData<String>> deleteImage(@PathVariable("id") String id) {
        ResponseData<String> responseData = new ResponseData<>();

        try{
            String fileId = id.substring(6);

            mediaService.deleteImage(UUID.fromString(fileId));
            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Image with id " + id + " deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @DeleteMapping("/upload/delete/document/{id}")
    public ResponseEntity<ResponseData<String>> deleteDocument(@PathVariable("id") String id) {
        ResponseData<String> responseData = new ResponseData<>();

        try{
            String fileId = id.substring(6);

            mediaService.deleteDocument(UUID.fromString(fileId));
            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Document with id " + id + " deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }


}

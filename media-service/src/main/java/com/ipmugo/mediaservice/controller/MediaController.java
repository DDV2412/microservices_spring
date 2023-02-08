package com.ipmugo.mediaservice.controller;

import java.util.*;

import com.ipmugo.mediaservice.dto.ResponseData;
import com.ipmugo.mediaservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.ipmugo.mediaservice.dto.MediaRequest;
import com.ipmugo.mediaservice.model.Media;
import com.ipmugo.mediaservice.service.MediaService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Value("${spring.client.serverUrl.imageZone}")
    private String imageZone;

    /**
     * Allow Content Type
     * Validation
     * */
    private Set<String> allowContentType () {
        return new HashSet<>(Arrays.asList("image/jpeg", "image/png", "application/pdf","application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    /**
     * Save Single File
     * */
    @PostMapping("/upload/file")
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseData<Media>> uploadFile(@RequestParam("single") MultipartFile file) {
        MediaRequest mediaRequest = new MediaRequest();
        ResponseData<Media> responseData = new ResponseData<>();
        try {

            // Validation if file is Empty
            if(file.isEmpty()){
                responseData.setStatus(false);
                responseData.getMessages().add("Please select a file to upload");

                return ResponseEntity.badRequest().body(responseData);
            }

            // Validation if content type not match with allow content type
            if (!this.allowContentType().contains(file.getContentType())) {
                responseData.setStatus(false);
                responseData.getMessages().add("File must be an image, PDF or word");
                return ResponseEntity.badRequest().body(responseData);
            }

            mediaRequest.setFileName(file.getOriginalFilename());
            mediaRequest.setFileSize(file.getSize());
            mediaRequest.setFileByte(file.getBytes());
            mediaRequest.setFileType(file.getContentType());
            mediaRequest.setFilePath(imageZone + "/file/"+file.getOriginalFilename());

            responseData.setStatus(true);
            responseData.setData(mediaService.uploadFile(mediaRequest));

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

    @PostMapping("/upload/multiple")
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseData<List<Media>>> multipleUpload(@RequestParam("multiple") List<MultipartFile> files) {

        ResponseData<List<Media>> responseData = new ResponseData<>();

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
                if (!this.allowContentType().contains(file.getContentType())) {
                    responseData.setStatus(false);
                    responseData.getMessages().add("File must be an image, PDF or word");
                    return ResponseEntity.badRequest().body(responseData);
                }

                mediaRequest.setFileName(file.getOriginalFilename());
                mediaRequest.setFileSize(file.getSize());
                mediaRequest.setFileByte(file.getBytes());
                mediaRequest.setFileType(file.getContentType());
                mediaRequest.setFilePath(imageZone + "/file/"+file.getOriginalFilename());
                mediaRequests.add(mediaRequest);
            }

            responseData.setStatus(true);
            responseData.setData(mediaService.multipleUpload(mediaRequests));

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

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getMedia(@PathVariable("fileName") String fileName) {
        try{
            Media media = mediaService.getFile(fileName);
            return ResponseEntity.ok().contentType(MediaType.valueOf(media.getFileType())).body(media.getFileByte());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/upload/view/files")
    public ResponseEntity<ResponseData<List<Media>>> getAllMedia() {
        ResponseData<List<Media>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(mediaService.getAllMedia());

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }

    }

    @DeleteMapping("/upload/delete/{fileName}")
    public ResponseEntity<ResponseData<String>> deleteMedia(@PathVariable("fileName") String fileName) {
        ResponseData<String> responseData = new ResponseData<>();

        try{
            mediaService.deleteMedia(fileName);
            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("File with name " + fileName + " deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

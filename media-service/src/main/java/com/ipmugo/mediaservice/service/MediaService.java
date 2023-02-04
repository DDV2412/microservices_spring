package com.ipmugo.mediaservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ipmugo.mediaservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ipmugo.mediaservice.dto.MediaRequest;
import com.ipmugo.mediaservice.model.Media;
import com.ipmugo.mediaservice.repository.MediaRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    /**
     * Save Single File
     * */
    public Media uploadFile(MediaRequest mediaRequest) throws CustomException {
        try{
            Media media = Media.builder()
                    .fileName(mediaRequest.getFileName())
                    .fileSize(mediaRequest.getFileSize())
                    .fileByte(mediaRequest.getFileByte())
                    .fileType(mediaRequest.getFileType())
                    .filePath(mediaRequest.getFilePath())
                    .build();

            return mediaRepository.save(media);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Save Multiple File
     * */
    public List<Media> multipleUpload(List<MediaRequest> medias) throws CustomException {
        try{
            List<Media> mediaList = new ArrayList<>();
            for (MediaRequest mediaRequest : medias) {
                Media media = Media.builder()
                        .fileName(mediaRequest.getFileName())
                        .fileSize(mediaRequest.getFileSize())
                        .fileByte(mediaRequest.getFileByte())
                        .fileType(mediaRequest.getFileType())
                        .filePath(mediaRequest.getFilePath())
                        .build();
                mediaList.add(media);
            }
            return mediaRepository.saveAll(mediaList);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get File By FileName
     * */
    public Media getFile(String fileName) throws CustomException {
       try{
           Optional<Media> media = mediaRepository.findByFileName(fileName);

           if (media.isEmpty()) {
               throw new CustomException("File with " + fileName + " not found", HttpStatus.NOT_FOUND);
           }

           return media.get();
       } catch (Exception e){
           throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
       }

    }

    /**
     * Get List Files
     * */
    public List<Media> getAllMedia() throws CustomException {
        try{
            return mediaRepository.findAll();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete File By FileName
     * */
    public void deleteMedia(String fileName) throws CustomException {
        try{
            Media media = this.getFile(fileName);

            mediaRepository.deleteById(media.getId());
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }
}

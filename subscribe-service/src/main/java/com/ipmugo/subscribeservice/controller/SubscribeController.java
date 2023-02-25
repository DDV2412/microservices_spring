package com.ipmugo.subscribeservice.controller;


import com.ipmugo.subscribeservice.dto.ResponseData;
import com.ipmugo.subscribeservice.model.Subscribe;
import com.ipmugo.subscribeservice.service.SubscribeService;
import com.ipmugo.subscribeservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    @GetMapping
    public ResponseEntity<ResponseData<Page<Subscribe>>> getAllSubscribe(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size){
        ResponseData<Page<Subscribe>> responseData = new ResponseData<>();
        try{
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
            responseData.setStatus(true);
            responseData.setData(subscribeService.getAllSubscribe(pageable));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteSubscribe(@PathVariable("id") UUID id){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            subscribeService.deleteSubscribe(id);
            responseData.setStatus(true);
            responseData.getMessages().add("Delete subscription successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseData<Subscribe>> subscribe(@RequestBody Subscribe subscribe){
        ResponseData<Subscribe> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.getMessages().add("Subscription successfully");
            responseData.setData(subscribeService.subscribe(subscribe));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

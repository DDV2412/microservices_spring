package com.ipmugo.setcounter.controller;

import com.ipmugo.setcounter.dto.ResponseData;
import com.ipmugo.setcounter.model.Status;
import com.ipmugo.setcounter.service.SetCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/set-counter")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SetCounterController {

    @Autowired
    private SetCounterService setCounterService;

    @PostMapping("/{unique}/{doi}/{status}")
    public ResponseEntity<ResponseData<String>> setCounter(@PathVariable("unique") String unique, @PathVariable("doi") String doi, @PathVariable("status") Status status){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            setCounterService.setCounter(unique+"/"+doi, status);

            responseData.getMessages().add("Set counter add successfully");
            responseData.setStatus(true);

            return ResponseEntity.ok(responseData);
        }catch (Exception e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }
    }

    @GetMapping("/{status}")
    public ResponseEntity<ResponseData<Long>> getSetCounter(@PathVariable("status") Status status){
        ResponseData<Long> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(setCounterService.getCounter(status));

            return ResponseEntity.ok(responseData);
        }catch (Exception e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }
    }



}

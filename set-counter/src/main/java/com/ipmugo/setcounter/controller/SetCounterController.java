package com.ipmugo.setcounter.controller;

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

    @PostMapping("/{articleId}/{status}")
    public ResponseEntity<String> setCounter(@PathVariable("articleId") String articleId, @PathVariable("status") Status status){
        setCounterService.setCounter(articleId, status);

        return ResponseEntity.ok("Set counter add successfully");
    }

    @GetMapping
    public ResponseEntity<Long> getSetCounter(@PathVariable("status") Status status){
        return ResponseEntity.ok(setCounterService.getCounter(status));
    }



}

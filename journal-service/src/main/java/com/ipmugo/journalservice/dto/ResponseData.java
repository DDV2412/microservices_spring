package com.ipmugo.journalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseData <T> {
    private boolean status;

    private List<String> messages = new ArrayList<>();

    private T data;
}

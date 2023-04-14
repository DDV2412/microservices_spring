package com.ipmugo.articleservice.event;

import com.ipmugo.articleservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetCounterEvent {

    private String doi;
    private Status status;
    private Long count;
}

package com.ipmugo.searchservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Journal {

    private String id;

    private String name;

    private String issn;

    private String e_issn;

    private String publisher;


}

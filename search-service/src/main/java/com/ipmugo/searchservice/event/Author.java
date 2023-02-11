package com.ipmugo.searchservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {


    private String id;


    private String firstName;


    private  String lastName;


    private String affiliation;


}

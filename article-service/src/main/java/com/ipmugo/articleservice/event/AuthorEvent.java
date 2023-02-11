package com.ipmugo.articleservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorEvent {


    private String id;


    private String firstName;


    private  String lastName;


    private String affiliation;


}

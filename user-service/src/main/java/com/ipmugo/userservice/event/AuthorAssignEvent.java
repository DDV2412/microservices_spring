package com.ipmugo.userservice.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorAssignEvent {

    private String id;


    private String firstName;


    private  String lastName;


    private String affiliation;


    private String orcid;


    private String scopusId;


    private String googleScholar;

}

package com.ipmugo.authorservice.dto;

import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignRequest {

    private User user;

    private List<Article> article;
}

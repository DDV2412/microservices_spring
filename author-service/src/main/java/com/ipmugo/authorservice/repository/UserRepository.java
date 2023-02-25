package com.ipmugo.authorservice.repository;


import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u JOIN u.articles a ORDER BY u.hIndex DESC, ((a.citationByScopus + a.citationByCrossRef)/2 + (a.viewsCount + a.downloadCount)/2) DESC")
    List<User> findTop4ByOrderByHIndexAndArticleValuesDescWithLimit();


}
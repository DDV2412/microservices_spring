package com.ipmugo.articleservice.service;

import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.repository.AuthorRepository;
import com.ipmugo.articleservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;


    /**
     * Save Author
     * */
    public Author addAuthor(Author author) throws CustomException {
        try{
            return authorRepository.save(author);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Author by FirstName and LastName
     * */
    public Author getAuthor(String firstName, String lastName) throws CustomException {
        try{
            Optional<Author> author1 = authorRepository.findAllByFirstNameAndLastName(firstName, lastName);
            return author1.orElse(null);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
    /**
     * Update Data Author
     * */
    public Author updateAuthor(String firstName, String lastName, Author author) throws CustomException {
        try{
            Author authorUpdate = this.getAuthor(firstName, lastName);

            authorUpdate.setFirstName(author.getFirstName());
            authorUpdate.setLastName(author.getLastName());
            authorUpdate.setEmail(author.getEmail());
            authorUpdate.setAffiliation(author.getAffiliation());
            authorUpdate.setBiography(author.getBiography());
            authorUpdate.setOrcid(author.getOrcid());
            authorUpdate.setGoogleScholar(author.getGoogleScholar());
            authorUpdate.setScopusId(author.getScopusId());
            return authorRepository.save(authorUpdate);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete Author
     * */
    public void deleteAuthor(String id) throws CustomException {
        try{
            authorRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

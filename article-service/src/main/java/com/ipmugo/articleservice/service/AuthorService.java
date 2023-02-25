package com.ipmugo.articleservice.service;

import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.repository.AuthorRepository;
import com.ipmugo.articleservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
     * Save AuthorEvent
     * */
    public Author addAuthor(Author author) throws CustomException {
        try{
           if(author.getId() != null){
               Optional<Author> author1 = authorRepository.findById(author.getId());

               if(author1.isPresent()){
                   author1.get().setFirstName(author.getFirstName());
                   author1.get().setLastName(author.getLastName());
                   author1.get().setEmail(author.getEmail());
                   author1.get().setAffiliation(author.getAffiliation());
                   author1.get().setOrcid(author.getOrcid());



                   return authorRepository.save(author1.get());
               }

           }
            return authorRepository.save(author);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * AuthorEvent by FirstName and LastName
     * */
    public Iterable<Author> getAuthor(Pageable pageable, String firstName, String lastName) throws CustomException {
        try{
            return authorRepository.searchAuthor(pageable, firstName, lastName);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
    /**
     * Update Data AuthorEvent
     * */
    public Author updateAuthor(String  id, Author author) throws CustomException {
        try{
            Optional<Author> authorUpdate = authorRepository.findById(id);

            if(authorUpdate.isEmpty()){
                throw new CustomException("AuthorEvent with id "+ id + " not found", HttpStatus.BAD_GATEWAY);
            }
            authorUpdate.get().setFirstName(author.getFirstName());
            authorUpdate.get().setLastName(author.getLastName());
            authorUpdate.get().setEmail(author.getEmail());
            authorUpdate.get().setAffiliation(author.getAffiliation());
            authorUpdate.get().setOrcid(author.getOrcid());
            return authorRepository.save(authorUpdate.get());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete AuthorEvent
     * */
    public void deleteAuthor(String id) throws CustomException {
        try{
            authorRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

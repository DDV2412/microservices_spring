package com.ipmugo.userservice.utils;

import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
public class Automate {

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 0 1 * *", zone = "GMT+7")
    public void asyncScholar(){
        Pageable pageable = PageRequest.of(0, 25);


        do{
            Page<User> users = userService.getAllUser(pageable, null);

            if(users.getContent().size() == 0){
                break;
            }

            for(User user: users.getContent()){
                if(user.getGoogleScholar() == null){
                    continue;
                }
                try{
                    userService.asyncScholar(user.getId());
                }catch (Exception e){
                    continue;
                }
            }
        }while (true);
    }
}

package com.ipmugo.setcounter.utils;

import com.ipmugo.setcounter.model.SetCounter;
import com.ipmugo.setcounter.model.Status;
import com.ipmugo.setcounter.repository.SetCounterRepository;
import com.ipmugo.setcounter.service.SetCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Async
public class Automate {

    @Autowired
    private SetCounterService setCounterService;

    @Autowired
    private KafkaTemplate<String, com.ipmugo.setcounter.event.SetCounter> kafkaTemplate;

    @Autowired
    private SetCounterRepository setCounterRepository;
    @Scheduled(cron = "0 0 1 * * *", zone = "GMT+7")
    public void setCounter(){
        setCounterService.weekCounterData(Status.Download);

        setCounterService.weekCounterData(Status.View);
    }

    @Scheduled(cron = "0 1 * * * *", zone = "GMT+7")
    public void pushCounter(){
        Pageable pageable = PageRequest.of(0, 25);

        do{
            Page<SetCounter> setCounters = setCounterRepository.findAll(pageable);

            if(setCounters.getContent().isEmpty()){
                break;
            }

            for(SetCounter setCounter: setCounters){
                Long viewCount = setCounterRepository.countByArticleIdAndStatus(setCounter.getArticleId(), Status.View);
                Long downloadCount = setCounterRepository.countByArticleIdAndStatus(setCounter.getArticleId(), Status.Download);

                kafkaTemplate.send("setCounter", com.ipmugo.setcounter.event.SetCounter.builder()
                        .articleId(setCounter.getArticleId())
                                .downloadCount(Math.toIntExact(downloadCount))
                                .viewsCount(Math.toIntExact(viewCount))
                        .build());
            }
        }while (true);
    }
}

package com.ipmugo.setcounter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(value = "reportPercentage")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekCounter {

    @Field
    private String weekYear;

    @Field
    private Status status;

    @Field
    private int count;

    @Field
    private int lastWeekCount;

    @Field
    private double percentageIncrease;

    @CreatedDate
    private LocalDateTime createdAt;
}

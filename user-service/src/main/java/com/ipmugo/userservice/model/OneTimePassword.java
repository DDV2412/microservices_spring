package com.ipmugo.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(value = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OneTimePassword {

    @Id
    private String id;

    @Field
    private String email;

    @Field
    @Indexed(unique = true)
    private Integer otp;

    private LocalDateTime expiredAt;

}

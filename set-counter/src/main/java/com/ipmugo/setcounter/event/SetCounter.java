package com.ipmugo.setcounter.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetCounter {

    private String articleId;

    private int viewsCount;

    private  int downloadCount;
}

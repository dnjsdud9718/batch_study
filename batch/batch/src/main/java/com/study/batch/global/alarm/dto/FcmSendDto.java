package com.study.batch.global.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 모바일에서 전달받은 객체
 *
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmSendDto {
    private String token;

    private String title;

    private String body;

}
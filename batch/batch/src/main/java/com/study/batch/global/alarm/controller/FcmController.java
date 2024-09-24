package com.study.batch.global.alarm.controller;

import com.study.batch.global.alarm.dto.FcmSendDto;
import com.study.batch.global.alarm.service.FcmService;
import com.study.batch.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * FCM 관리하는 Controller
 *
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<CommonResponse> pushMessage(@RequestBody FcmSendDto fcmSendDto) throws IOException {
        log.info("[+] 푸시 메시지를 전송합니다. ");
        return ResponseEntity.ok(fcmService.sendMessageTo(fcmSendDto));
    }
}

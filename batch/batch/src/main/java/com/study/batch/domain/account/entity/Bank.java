package com.study.batch.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bank {
    BOK("한국은행", "001"),
    KDB("산업은행", "002"),
    IBK("기업은행", "003"),

    KB("국민은행", "004"),
    NH("농협은행", "011"),
    WR("우리은행", "020"),
    SC("SC제일은행", "023"),
    CT("시티은행", "027"),
    DGB("대구은행", "032"),
    KJ("광주은행", "034"),
    JB("제주은행", "035"),
    BNK("경남은행", "039"),
    MG("새마을금고", "045"),
    KEB("하나은행", "081"),
    SH("신한은행", "088"),
    KAKAO("카카오뱅크","090"),
    SSAFY("싸피은행", "999");
    // 농협, 국민, 하나, 부산, 산업, 제일, 우리

    private final String bankName;
    private final String bankCode;
}

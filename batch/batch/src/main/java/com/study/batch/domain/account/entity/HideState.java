package com.study.batch.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HideState {
    HIDE_ALL("전체숨김"),
    HIDE_ASSET("자산숨김"),
    HIDE_LIST("목록숨김"),
    NONE("숨김없음");
    private final String state;
}
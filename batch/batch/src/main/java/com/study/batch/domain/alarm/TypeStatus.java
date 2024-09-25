package com.study.batch.domain.alarm;

public enum TypeStatus {
    GAME_RESULT, // 내기 결과
    GAME_REQUEST, // 내기 신청
    SETTLEMENT_REQUEST, // 정산 요청
    FIXED_EXPENSES, //고정 치출 알림
    SEED_SEND, // 종잣돈 이체 성공 여부
    SEED_FINISH // 종잣돈 종료
}

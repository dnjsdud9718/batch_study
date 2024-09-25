package com.study.batch.domain.alarm;

import com.study.batch.domain.user.entity.User;
import com.study.batch.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "alarms")
@Getter
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Alarm extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;
    private Long typeId;

    @Enumerated(EnumType.STRING)
    private TypeStatus type;



    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    public static Alarm of(Long userId, String title, String content, TypeStatus type, Long typeId) {
        return Alarm.builder()
                .user(User.builder().id(userId).build())
                .title(title)
                .content(content)
                .typeId(typeId)
                .type(type)
                .status(AlarmStatus.NONREAD).build();
    }

    public void updateAlarmStatusRead() {
        if (AlarmStatus.NONREAD.equals(status)) {
            return;
        }
        status = AlarmStatus.READ;
    }
}

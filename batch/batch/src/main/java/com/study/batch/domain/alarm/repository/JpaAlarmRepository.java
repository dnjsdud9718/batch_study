package com.study.batch.domain.alarm.repository;

import com.study.batch.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAlarmRepository extends JpaRepository<Alarm, Long> {
}

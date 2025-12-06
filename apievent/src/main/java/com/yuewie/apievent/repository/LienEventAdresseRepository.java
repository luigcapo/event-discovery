package com.yuewie.apievent.repository;

import com.yuewie.apievent.entity.LienEventAdresse;
import com.yuewie.apievent.entity.LienEventAdresseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LienEventAdresseRepository extends JpaRepository<LienEventAdresse, LienEventAdresseId> {
    List<LienEventAdresse> findByEventId(Long eventId);
}

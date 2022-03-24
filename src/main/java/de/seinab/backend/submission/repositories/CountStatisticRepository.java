package de.seinab.backend.submission.repositories;

import de.seinab.backend.submission.entities.CountStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountStatisticRepository extends JpaRepository<CountStatistic, Long> {
    List<CountStatistic> getAllByFormId(Long formId);
}

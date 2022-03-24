package de.seinab.finance.repositories;

import de.seinab.finance.entities.FormFeeTurnover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormFeeTurnoverRepository extends JpaRepository<FormFeeTurnover, Long> {
    FormFeeTurnover getBySubmissionId(Long submissionId);
}

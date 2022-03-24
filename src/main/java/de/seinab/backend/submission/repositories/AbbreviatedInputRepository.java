package de.seinab.backend.submission.repositories;

import de.seinab.backend.submission.entities.AbbreviatedInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbbreviatedInputRepository extends JpaRepository<AbbreviatedInput, Long> {
    List<AbbreviatedInput> getAllByFormId(Long formId);
}

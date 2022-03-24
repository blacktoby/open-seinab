package de.seinab.form.repositories;

import de.seinab.form.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> getAllByFormId(Long formId);
    Submission getSubmissionById(Long submissionId);
    Form getFormIdById(Long submissionId);

    interface Form {
        Id getId();
    }

    interface Id {
        Long getId();
    }
}

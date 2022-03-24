package de.seinab.form.repositories;

import de.seinab.form.entities.ConfirmationInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmationInputRepository extends JpaRepository<ConfirmationInput, Long> {
    List<ConfirmationInput> getByFormId(Long formId);
}

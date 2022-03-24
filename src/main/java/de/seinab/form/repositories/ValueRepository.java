package de.seinab.form.repositories;

import de.seinab.form.entities.Form;
import de.seinab.form.entities.InputValue;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ValueRepository extends JpaRepository<InputValue, Long> {
    List<InputValue> getValuesByForm(Form form);
    int countByInputIdAndData(Long input_id, @NotNull String data);
}

package de.seinab.form.repositories;

import de.seinab.EventGroup;
import de.seinab.form.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> getAllByEventGroup(EventGroup eventGroup);
    Form getFormByNameAndEventGroupName(@NotNull String name, @NotNull String eventGroupName);
    Form getFormByNameAndEventGroupId(@NotNull String name, @NotNull Long eventGroupId);
    Form getFormByName(String name);
    Form getFormById(Long id);
    Id getFormIdByNameAndEventGroupName(String name, @NotNull String eventGroupName);

    interface Id {
        Long getId();
    }
}

package de.seinab.form.repositories;

import de.seinab.EventGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;

public interface EventGroupRepository extends JpaRepository<EventGroup, Long> {
    EventGroup getEventGroupByName(@NotNull String name);
    Id getIdByName(@NotNull String name);

    interface Id {
        Long getId();
    }

}

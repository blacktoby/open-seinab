package de.seinab.finance.repositories;

import de.seinab.finance.entities.ReferenceSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceSettingsRepository extends JpaRepository<ReferenceSettings, Long> {
    ReferenceSettings getByFormId(Long id);
}

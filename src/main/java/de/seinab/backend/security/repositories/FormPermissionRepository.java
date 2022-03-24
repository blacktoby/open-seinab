package de.seinab.backend.security.repositories;

import de.seinab.backend.security.entities.FormPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormPermissionRepository extends JpaRepository<FormPermission, Long> {
    FormPermission getFormPermissionByUserIdAndFormId(Long userId, Long formId);
}

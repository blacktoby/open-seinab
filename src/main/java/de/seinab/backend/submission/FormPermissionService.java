package de.seinab.backend.submission;

import de.seinab.backend.security.entities.FormPermission;
import de.seinab.backend.security.repositories.FormPermissionRepository;
import de.seinab.form.repositories.FormRepository;
import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class FormPermissionService {
    @Autowired
    private FormPermissionRepository formPermissionRepository;
    @Autowired
    private FormRepository formRepository;


    public FormPermission getFormPermission(User user, String eventGroupName, String formName) throws AccessDeniedException {
        FormPermission formPermission = user.getFormPermissionList().stream()
                .filter(fp -> StringUtils.equals(fp.getForm().getName(), formName) &&
                        StringUtils.equals(fp.getEventGroup().getName(), eventGroupName))
                .findFirst().orElse(null);
        if(formPermission == null) {
            throw new AccessDeniedException("User has no rights for Form with name: " + formName);
        }
        return formPermission;
    }

}

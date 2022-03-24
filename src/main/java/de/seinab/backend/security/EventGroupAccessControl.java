package de.seinab.backend.security;

import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EventGroupAccessControl {

    public boolean checkEventGroup(Authentication authentication, String eventGroupName) {
        boolean hasAccess = false;
        if(authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            hasAccess =
                    user.getEventGroupList().stream()
                            .anyMatch(e -> StringUtils.equals(e.getName(), eventGroupName));
        }
        return hasAccess;
    }
}

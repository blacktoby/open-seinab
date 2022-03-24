package de.seinab.backend.security;

import de.seinab.backend.security.entities.FormPermission;
import de.seinab.backend.submission.FormPermissionService;
import de.seinab.backend.submission.entities.InputFilter;
import de.seinab.form.entities.InputValue;
import de.seinab.form.entities.Submission;
import de.seinab.form.repositories.SubmissionRepository;
import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FormPermissionAccessControl {

    @Autowired
    private FormPermissionService formPermissionService;
    @Autowired
    private SubmissionRepository submissionRepository;

    public boolean checkFormWritePermission(Authentication authentication, String formName, String eventGroupName) {
        if (!(authentication.getPrincipal() instanceof User)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        return checkFormWritePermission(formPermission);
    }

    public boolean checkFormWritePermission(FormPermission formPermission) {
        if (formPermission == null) {
            return false;
        }

        return formPermission.isWritePermitted();
    }

    public boolean checkSubmissionWritePermission(FormPermission formPermission, Long submissionId) {
        Submission submission = submissionRepository.getSubmissionById(submissionId);
        Long submissionFormId = submission.getForm().getId();
        Long formPermissionFormId = formPermission.getForm().getId();
        return submissionFormId.equals(formPermissionFormId) &&
                formPermission.isWritePermitted() &&
                inputFilterAllowsWriting(submission, formPermission.getInputFilterList());
    }

    private boolean inputFilterAllowsWriting(Submission submission, List<InputFilter> inputFilterList) {
        if(inputFilterList.isEmpty()) {
            return true;
        }
        Map<Long, String> inputIdDataMap =
                submission.getInputValueList().stream()
                        .collect(Collectors.toMap(iv -> iv.getInput().getId(), InputValue::getData));
        for(InputFilter inputFilter : inputFilterList) {
            Long filteredInputId = inputFilter.getInput().getId();
            if(inputIdDataMap.containsKey(filteredInputId) &&
                    !StringUtils.equals(inputFilter.getData(), inputIdDataMap.get(filteredInputId))) {
                return false;
            }
        }
        return true;
    }

    public boolean checkingFormfeePermission(Authentication authentication, String eventGroupName, String formName) {
        if (!(authentication.getPrincipal() instanceof User)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        return formPermission.isFormfeePermitted();
    }

    public boolean checkBankingPermission(Authentication authentication, String eventGroupName, String formName) {
        if (!(authentication.getPrincipal() instanceof User)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        return formPermission.isBankingPermitted();
    }
}

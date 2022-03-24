package de.seinab.backend.submission;

import de.seinab.backend.security.entities.FormPermission;
import de.seinab.backend.submission.entities.AbbreviatedInput;
import de.seinab.backend.submission.entities.InputFilter;
import de.seinab.backend.submission.models.AbbreviatedInputValue;
import de.seinab.backend.submission.models.BackendSubmission;
import de.seinab.backend.submission.repositories.AbbreviatedInputRepository;
import de.seinab.form.IllegalFormException;
import de.seinab.form.RequiredFieldMissingException;
import de.seinab.form.SubmissionService;
import de.seinab.form.entities.InputType;
import de.seinab.form.entities.InputValue;
import de.seinab.form.entities.Submission;
import de.seinab.form.repositories.SubmissionRepository;
import de.seinab.form.repositories.ValueRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BackendSubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ValueRepository valueRepository;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private AbbreviatedInputRepository abbreviatedInputRepository;

    public List<BackendSubmission> getSubmissions(FormPermission formPermission) {
        List<AbbreviatedInput> abbreviatedInputList = abbreviatedInputRepository.getAllByFormId(formPermission.getForm().getId());
        return getSubmissions(formPermission, abbreviatedInputList);
    }

    public List<BackendSubmission> getSubmissions(FormPermission formPermission, List<AbbreviatedInput> abbreviatedInputList) {
        List<BackendSubmission> backendSubmissionList = getBackendSubmissions(formPermission, abbreviatedInputList);
        List<InputFilter> inputFilterList = formPermission.getInputFilterList();
        if(inputFilterList.isEmpty()) {
            return backendSubmissionList;
        }
        return backendSubmissionList.stream()
                .filter(submission -> inputValuesMatchesAnyInputFilter(submission.getInputValueList(), inputFilterList))
                .collect(Collectors.toList());
    }

    List<BackendSubmission> getBackendSubmissions(FormPermission formPermission, List<AbbreviatedInput> abbreviatedInputList) {
        List<Submission> submissions = submissionRepository.getAllByFormId(formPermission.getForm().getId());
        return getBackendSubmissions(abbreviatedInputList, submissions);
    }

    List<BackendSubmission> getBackendSubmissions(List<AbbreviatedInput> abbreviatedInputList, List<Submission> submissions) {
        List<BackendSubmission> backendSubmissions = submissions.stream().map(BackendSubmission::new).collect(Collectors.toList());
        setAbbreviatedValues(backendSubmissions, abbreviatedInputList);
        return backendSubmissions;
    }

    private void setAbbreviatedValues(List<BackendSubmission> submissions, List<AbbreviatedInput> abbreviatedInputs) {
        submissions.forEach(submission -> setAbbreviatedValues(submission, abbreviatedInputs));
    }

    private void setAbbreviatedValues(BackendSubmission submission, List<AbbreviatedInput> abbreviatedInputs) {
        List<AbbreviatedInputValue> abbreviatedInputValues = abbreviatedInputs.stream()
                .map(abbreviatedInput -> createAbbreviatedInputValue(submission, abbreviatedInput))
                .collect(Collectors.toList());
        submission.setAbbreviatedInputValueList(abbreviatedInputValues);
    }

    private AbbreviatedInputValue createAbbreviatedInputValue(BackendSubmission backendSubmission, AbbreviatedInput abbreviatedInput) {
        Map<String, String> keyValueMap = abbreviatedInput.getKeyValueMap();
        String inputData = backendSubmission.getInputValueList().stream()
                .filter(inputValue -> inputValue.getInput().getId().equals(abbreviatedInput.getKeyInput().getId()))
                .map(InputValue::getData)
                .findFirst().orElse("");
        String abbreviatedValue = keyValueMap.getOrDefault(inputData, "");
        return new AbbreviatedInputValue(abbreviatedInput, abbreviatedValue);
    }

    @PreAuthorize("@formPermissionAccessControl.checkSubmissionWritePermission(#formPermission, #submissionId)")
    public Submission getSubmission(FormPermission formPermission, Long submissionId) {
        return submissionRepository.getSubmissionById(submissionId);
    }


    private boolean inputValuesMatchesAnyInputFilter(List<InputValue> inputValueList, List<InputFilter> inputFilterList) {
        Map<Long, String> inputIdFilterDataMap =
                inputFilterList.stream().collect(Collectors.toMap(inputFilter -> inputFilter.getInput().getId(), InputFilter::getData));
        return inputValueList.stream()
                .filter(inputValue ->
                        inputIdFilterDataMap.containsKey(inputValue.getInput().getId()))
                .anyMatch(inputValue ->
                        StringUtils.equals(inputIdFilterDataMap.get(inputValue.getInput().getId()), inputValue.getData()));
    }

    @PreAuthorize("@formPermissionAccessControl.checkFormWritePermission(#formPermission)")
    public Submission createSubmissionByFormData(FormPermission formPermission, Map<String, String> formData)
            throws IllegalFormException, RequiredFieldMissingException {
        formPermission.getInputFilterList().forEach(inputFilter -> formData.put(inputFilter.getInput().getName(), inputFilter.getData()));
        Submission submission = submissionService.createSubmissionByFormData(formPermission.getEventGroup().getName(), formPermission.getForm().getName(), formData);
        return submissionRepository.save(submission);
    }

    @PreAuthorize("@formPermissionAccessControl.checkSubmissionWritePermission(#formPermission, #formData.getOrDefault('id', 0))")
    public void updateSubmissionByFormData(FormPermission formPermission, Map<String, String> formData) {
        Long submissionId = Long.valueOf(formData.getOrDefault("id", "-1"));
        if(submissionId <= 0) {
            throw new IllegalArgumentException("Missing submissionId! Can't update Submission without it.");
        }

        if(formDataIncludesFilteredInput(formPermission.getInputFilterList(), formData)) {
            throw new AccessDeniedException("Illegal update on filtered Input");
        }

        Submission submission = submissionRepository.getSubmissionById(submissionId);
        updateSubmissionByFormData(formData, submission);
    }

    private void updateSubmissionByFormData(Map<String, String> formData, Submission submission) {
        for(String key : formData.keySet()) {
            InputValue inputValue = findInputValue(submission, key);
            if(inputValue == null) {
                continue;
            }
            String data = formData.get(key);
            updateInputValue(inputValue, data);
        }
    }

    private void updateInputValue(InputValue inputValue, String data) {
        if(inputValue.getInput().getType() == InputType.checkbox) {
            if(StringUtils.equals(data,"false")) {
                data = "false";
            } else {
                data = "true";
            }
        }
        inputValue.setData(data);
        inputValue.setDateEdited(new Date());
        valueRepository.save(inputValue);
    }

    private InputValue findInputValue(Submission submission, String key) {
        return submission.getInputValueList().stream()
                .filter(v -> StringUtils.equals(v.getInput().getName(), key))
                .findFirst().orElse(null);
    }

    private boolean formDataIncludesFilteredInput(List<InputFilter> inputFilters, Map<String, String> formData) {
        return formData.keySet().stream()
                .anyMatch(key ->
                        inputFilters.stream()
                                .anyMatch(inputFilter ->
                                        StringUtils.equals(inputFilter.getInput().getName(), key)));
    }

    @PreAuthorize("@formPermissionAccessControl.checkSubmissionWritePermission(#formPermission, #formData.getOrDefault('id', 0))")
    public void deleteSubmission(FormPermission formPermission, Map<String, String> formData) {
        Long submissionId = Long.valueOf(formData.getOrDefault("id", "-1"));
        if(submissionId <= 0) {
            throw new IllegalArgumentException("Missing submissionId! Can't update Submission without it.");
        }
        submissionRepository.deleteById(submissionId);
    }
}

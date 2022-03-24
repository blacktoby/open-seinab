package de.seinab.backend.formfee;

import de.seinab.backend.datatable.models.MissingFieldError;
import de.seinab.backend.datatable.resolver.DataTablesEditorRequest;
import de.seinab.backend.datatable.resolver.DataTablesEditorRequestParam;
import de.seinab.backend.security.entities.FormPermission;
import de.seinab.backend.submission.BackendSubmissionController;
import de.seinab.backend.submission.BackendSubmissionService;
import de.seinab.backend.submission.FormPermissionService;
import de.seinab.backend.submission.models.BackendSubmission;
import de.seinab.finance.FormFeeTurnoverService;
import de.seinab.finance.entities.FormFeeTurnover;
import de.seinab.finance.entities.ReferenceSettings;
import de.seinab.finance.repositories.FormFeeTurnoverRepository;
import de.seinab.finance.repositories.ReferenceSettingsRepository;
import de.seinab.form.IllegalFormException;
import de.seinab.form.entities.Input;
import de.seinab.form.entities.InputValue;
import de.seinab.form.entities.Submission;
import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FormFeeController {

    @Autowired
    private BackendSubmissionService backendSubmissionService;
    @Autowired
    private FormPermissionService formPermissionService;
    @Autowired
    private ReferenceSettingsRepository referenceSettingsRepository;
    @Autowired
    private FormFeeTurnoverService turnoverService;
    @Autowired
    private FormFeeTurnoverRepository turnoverRepository;

    @RequestMapping("/backend/{eventGroupName}/{formName}/formfee")
    public String getFormFeePage(@AuthenticationPrincipal User user, @PathVariable String eventGroupName,
                                 @PathVariable String formName,
                                 Model model) {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        ReferenceSettings referenceSettings = referenceSettingsRepository.getByFormId(formPermission.getForm().getId());

        List<BackendSubmission> submissions = backendSubmissionService.getSubmissions(formPermission);
        removeNonReferenceValues(submissions, referenceSettings);

        model.addAttribute("submissions", submissions);
        model.addAttribute("formPermission", formPermission);
        model.addAttribute("inputList", getReferenceInputs(formPermission, referenceSettings));
        return "backend/formfee";
    }


    private void removeNonReferenceValues(List<BackendSubmission> backendSubmissions, ReferenceSettings referenceSettings) {
        List<Long> referenceInputIds = referenceSettings.getReferenceInputs()
                .stream()
                .map(ri -> ri.getInput().getId())
                .collect(Collectors.toList());


        backendSubmissions.forEach(backendSubmission ->  {
            List<InputValue> valueList = backendSubmission.getInputValueList();
            valueList = valueList.stream()
                    .filter(iv -> referenceInputIds.stream().anyMatch(id -> id.equals(iv.getInput().getId())))
                    .collect(Collectors.toList());
            backendSubmission.setInputValueList(valueList);
        });
    }

    private List<Input> getReferenceInputs(FormPermission formPermission, ReferenceSettings referenceSettings) {
        List<Input> filteredInputList = formPermission.getFilteredInputList();
        Set<Long> referenceInputIds = referenceSettings.getReferenceInputs()
                .stream()
                .map(i -> i.getInput().getId())
                .collect(Collectors.toSet());
        return filteredInputList.stream().filter(i -> referenceInputIds.contains(i.getId())).collect(Collectors.toList());
    }

    @RequestMapping("/backend/api/{eventGroupName}/{formName}/formfee")
    @ResponseBody
    public Object getFormFeeValues(@AuthenticationPrincipal User user,
                                   @PathVariable String eventGroupName,
                                   @PathVariable String formName) {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        ReferenceSettings referenceSettings = referenceSettingsRepository.getByFormId(formPermission.getForm().getId());

        List<BackendSubmission> submissions = backendSubmissionService.getSubmissions(formPermission);
        removeNonReferenceValues(submissions, referenceSettings);

        return buildDatatablesResponseObject(submissions);
    }


    @PreAuthorize("@formPermissionAccessControl.checkFormWritePermission(authentication, #formName, #eventGroupName)")
    @RequestMapping("/backend/api/{eventGroupName}/{formName}/formfee/datatable")
    @ResponseBody
    public Object formDataTableAction(@AuthenticationPrincipal User user,
                                      @PathVariable String eventGroupName, @PathVariable String formName,
                                      @DataTablesEditorRequestParam DataTablesEditorRequest dataTableRequest) throws IllegalFormException {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        List<Map<String, String>> parameterList = dataTableRequest.getParameterList();
        if ("edit".equals(dataTableRequest.getAction())) {
            return updateFormFeeTurnover(formPermission, parameterList);
        }
        return null;
    }

    private Object updateFormFeeTurnover(FormPermission formPermission, List<Map<String, String>> parameterList) {
        Map<Long, Submission> submissionMap =
                parameterList.stream()
                        .map(m -> backendSubmissionService.getSubmission(formPermission, Long.valueOf(m.get("id"))))
                        .collect(Collectors.toMap(Submission::getId, s -> s));

        for(Map<String, String> formData : parameterList) {
            List<String> missingFields = new ArrayList<>();
            Long submissionId = checkParseLongField(formData.get("id"), missingFields, "id");
            if(submissionId == -1) {
                missingFields.add("id");
            }

            Long value = checkParseLongField(formData.get("value"), missingFields, "value");

            Long dateTime = checkParseLongField(formData.get("date"), missingFields, "date");
            Date date = new Date(dateTime);

            if(!missingFields.isEmpty()) {
                return new MissingFieldError(missingFields);
            }

            FormFeeTurnover formFeeTurnover = turnoverRepository.getBySubmissionId(submissionId);
            if(formFeeTurnover == null) {
                formFeeTurnover = new FormFeeTurnover();

                Submission submission = submissionMap.get(submissionId);
                formFeeTurnover.setSubmission(submission);
                submission.setFormFeeTurnover(formFeeTurnover);
            }
            formFeeTurnover.setValue(value);
            formFeeTurnover.setDate(date);

            turnoverRepository.save(formFeeTurnover);
        }
        return buildDatatablesResponseObject(new ArrayList<>(submissionMap.values()));
    }

    private Long checkParseLongField(String longString, List<String> missingFields, String fieldString) {
        long value = -1L;
        if(StringUtils.isEmpty(longString) || !StringUtils.isNumeric(longString)) {
            missingFields.add(fieldString);
        } else {
            value = Long.parseLong(longString);
        }
        return value;
    }


    private Map<String, Object> buildDatatablesResponseObject(List<? extends Submission> submissions) {
        return Collections.singletonMap("data", buildFeeSubmissionMapList(submissions));
    }

    private List<Object> buildFeeSubmissionMapList(List<? extends Submission> submissions) {
        return submissions.stream().map(this::buildFeeSubmissionDataMap).collect(Collectors.toList());
    }


    private Map<String, Object> buildFeeSubmissionDataMap(Submission submission) {
        Map<String, Object> dataMap = new LinkedHashMap<>();

        submission.getInputValueList().forEach(iv ->
                dataMap.put(iv.getInput().getName(), BackendSubmissionController.getData(iv)));
        dataMap.put("submissionId", submission.getId());

        FormFeeTurnover turnover = submission.getFormFeeTurnover();
        if(turnover == null) {
            dataMap.put("date", "");
            dataMap.put("value", "");
        } else {
            dataMap.put("date", turnover.getDate().getTime());
            dataMap.put("value", turnover.getValue());
        }

        return dataMap;
    }




}

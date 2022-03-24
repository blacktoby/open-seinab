package de.seinab.backend.submission;

import de.seinab.backend.datatable.models.MissingFieldError;
import de.seinab.backend.datatable.resolver.DataTablesEditorRequest;
import de.seinab.backend.datatable.resolver.DataTablesEditorRequestParam;
import de.seinab.backend.security.entities.FormPermission;
import de.seinab.backend.submission.entities.AbbreviatedInput;
import de.seinab.backend.submission.models.AbbreviatedInputValue;
import de.seinab.backend.submission.models.BackendSubmission;
import de.seinab.backend.submission.models.DatatableData;
import de.seinab.backend.submission.repositories.AbbreviatedInputRepository;
import de.seinab.finance.repositories.ReferenceSettingsRepository;
import de.seinab.form.FormService;
import de.seinab.form.IllegalFormException;
import de.seinab.form.RequiredFieldMissingException;
import de.seinab.form.entities.*;
import de.seinab.pdf.ConsentFactory;
import de.seinab.pdf.ConsentFillFactory;
import de.seinab.user.models.User;
import de.seinab.utils.DownloadUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BackendSubmissionController {
    @Autowired
    private BackendSubmissionService backendSubmissionService;
    @Autowired
    private FormPermissionService formPermissionService;
    @Autowired
    private StatisticService formCountStatisticService;
    @Autowired
    private AbbreviatedInputRepository abbreviatedInputRepository;
    @Autowired
    private ReferenceSettingsRepository referenceSettingsRepository;
    @Autowired
    private FormService formService;

    @RequestMapping("/backend/{eventGroupName}/{formName}/submissions")
    public String backendFormSubmissions(@AuthenticationPrincipal User user, @PathVariable String eventGroupName, @PathVariable String formName,
                                         Model model) {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        Form form = formPermission.getForm();
        formService.sortForm(form);
        List<AbbreviatedInput> abbreviatedInputs = abbreviatedInputRepository.getAllByFormId(form.getId());
        List<BackendSubmission> submissions = backendSubmissionService.getSubmissions(formPermission, abbreviatedInputs);
        model.addAttribute("formPermission", formPermission);
        model.addAttribute("form", form);
        model.addAttribute("submissions", submissions);
        model.addAttribute("abbreviatedInputList", abbreviatedInputs);
        model.addAttribute("countStatistics", formCountStatisticService.getCountStatistics(form.getId(), submissions));
        return "backend/submissions";
    }

    @RequestMapping("/backend/{eventGroupName}/{formName}/{submissionId}/consent")
    public ResponseEntity<InputStreamResource> downloadConsent(@AuthenticationPrincipal User user,
                                                               @PathVariable String eventGroupName,
                                                               @PathVariable String formName,
                                                               @PathVariable Long submissionId)
            throws IOException, ConsentFillFactory.UnknownFieldException {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        Submission submission = backendSubmissionService.getSubmission(formPermission, submissionId);
        String reference = formService.buildReference(submission);


        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        formService.buildConsent(outStream, submission);

        HttpHeaders headers = DownloadUtils.getHttpHeaders("Einverständniserklärung.pdf");
        return DownloadUtils.getResponseEntity(new ByteArrayInputStream(outStream.toByteArray()), headers);
    }

    @PreAuthorize("@formPermissionAccessControl.checkFormWritePermission(authentication, #formName, #eventGroupName)")
    @RequestMapping("/backend/api/{eventGroupName}/{formName}/datatable")
    @ResponseBody
    public Object formDataTableAction(@AuthenticationPrincipal User user,
                                      @PathVariable String eventGroupName, @PathVariable String formName,
                                      @DataTablesEditorRequestParam DataTablesEditorRequest dataTableRequest)
            throws IllegalFormException {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        List<Map<String, String>> parameterList = dataTableRequest.getParameterList();
        switch (dataTableRequest.getAction()) {
            case "edit":
                return updateSubmission(formPermission, parameterList);
            case "create":
                return createSubmission(formPermission, parameterList);
            case "remove":
                return deleteSubmission(formPermission, parameterList);
        }
        return null;
    }

    private Object updateSubmission(FormPermission formPermission, List<Map<String, String>> parameterList) {
        parameterList.forEach(formData -> backendSubmissionService.updateSubmissionByFormData(formPermission, formData));
        List<Submission> submissions =
                parameterList.stream()
                        .map(m -> backendSubmissionService.getSubmission(formPermission, Long.valueOf(m.get("id"))))
                                .collect(Collectors.toList());
        return buildDatatablesResponseObjectBySubmissions(submissions);
    }

    private Object createSubmission(FormPermission formPermission, List<Map<String, String>> parameterList)
            throws IllegalFormException {
        List<Submission> submissionList = new ArrayList<>();
        for (Map<String, String> formData : parameterList) {
            try {
                Submission submission = backendSubmissionService.createSubmissionByFormData(formPermission, formData);
                submissionList.add(submission);
            } catch (RequiredFieldMissingException e) {
                return new MissingFieldError(e.getMissingInputNames());
            }
        }
        return buildDatatablesResponseObjectBySubmissions(submissionList);
    }

    private Object deleteSubmission(FormPermission formPermission, List<Map<String, String>> parameterList) {
        parameterList.forEach(formData -> backendSubmissionService.deleteSubmission(formPermission, formData));
        return "{}";
    }

    @RequestMapping("/backend/api/{eventGroupName}/{formName}/submissions")
    @ResponseBody
    public Object formSubmissionData(@AuthenticationPrincipal User user,
                                     @PathVariable String eventGroupName, @PathVariable String formName,
                                     Model model) {
        FormPermission formPermission = formPermissionService.getFormPermission(user, eventGroupName, formName);
        List<AbbreviatedInput> abbreviatedInputs = abbreviatedInputRepository.getAllByFormId(formPermission.getForm().getId());
        List<BackendSubmission> submissions = backendSubmissionService.getSubmissions(formPermission, abbreviatedInputs);

        return buildDatatablesResponseObject(submissions);
    }

    private Map<String, Object> buildDatatablesResponseObjectBySubmissions(List<Submission> submissions) {
        if(submissions.isEmpty()) {
            return new HashMap<>();
        }
        List<AbbreviatedInput> abbreviatedInputs = abbreviatedInputRepository.getAllByFormId(submissions.get(0).getForm().getId());
        return this.buildDatatablesResponseObject(backendSubmissionService.getBackendSubmissions(abbreviatedInputs, submissions));
    }

    private Map<String, Object> buildDatatablesResponseObject(List<BackendSubmission> submissions) {
        return Collections.singletonMap("data", buildSubmissionMapList(submissions));
    }

    private List<LinkedHashMap<String, Object>> buildSubmissionMapList(List<BackendSubmission> submissions) {
        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
        for (BackendSubmission submission : submissions) {
            LinkedHashMap<String, Object> submissionMap = buildSubmissionMap(submission);
            mapList.add(submissionMap);
        }
        return mapList;
    }

    private LinkedHashMap<String, Object> buildSubmissionMap(BackendSubmission submission) {
        LinkedHashMap<String, Object> submissionMap = new LinkedHashMap<>();
        submissionMap.put("submissionId", submission.getId());
        for (InputValue inputValue : submission.getInputValueList()) {
            DatatableData data = getData(inputValue);
            submissionMap.put(inputValue.getInput().getName(), data);
        }
        insertAbbreviatedInputs(submission, submissionMap);
        return submissionMap;
    }

    private void insertAbbreviatedInputs(BackendSubmission submission, LinkedHashMap<String, Object> submissionMap) {
        for(AbbreviatedInputValue abbreviatedInputValue : submission.getAbbreviatedInputValueList()) {
            submissionMap.put(abbreviatedInputValue.getAbbreviatedInput().getName(), new DatatableData(abbreviatedInputValue.getValue(), ""));
        }
    }

    public static DatatableData getData(InputValue inputValue) {

        String data = inputValue.getData();
        final String finalData = data;
        switch (inputValue.getInput().getType()) {
            case select:
                InputOption inputOption = ((SelectInput) inputValue.getInput())
                        .getInputOptionByValue(finalData);
                data = inputOption != null ? inputOption.getText() : "";
                break;
            case radio:
                data = ((RadioInput) inputValue.getInput())
                        .getInputOptions().stream()
                        .filter(o -> StringUtils.equals(o.getValue(), finalData))
                        .map(InputOption::getText).findFirst().orElse("");
                break;
        }
        return new DatatableData(data, inputValue.getData());
    }

}

package de.seinab.form;

import de.seinab.form.entities.Form;
import de.seinab.form.entities.InputValue;
import de.seinab.form.entities.Submission;
import de.seinab.form.repositories.ConfirmationInputRepository;
import de.seinab.form.repositories.SubmissionRepository;
import de.seinab.pdf.ConsentFillFactory;
import de.seinab.utils.DownloadUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import static de.seinab.utils.DownloadUtils.getFileType;
import static de.seinab.utils.DownloadUtils.getHttpHeaders;

@Controller
public class FormController {
    private static final Logger log = LoggerFactory.getLogger(FormController.class);

    @Value("${de.seinab.form.data.directory}")
    private String formDataDirectory;

    @Autowired
    private FormService formService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ConfirmationInputRepository confirmationInputRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    @RequestMapping("/form/{eventGroup}/{name}")
    public String form(@PathVariable String eventGroup, @PathVariable String name, Model model) {
        Form form = formService.getFormSorted(eventGroup, name);
        model.addAttribute("form", form);
        model.addAttribute("confirmationInputs", confirmationInputRepository.getByFormId(form.getId()));
        return "form/form";
    }

    @RequestMapping("/form/{eventGroup}/{name}/save")
    public String saveForm(@PathVariable String eventGroup,
                           @PathVariable String name,
                           @RequestBody MultiValueMap<String, String> formData, Model model)
            throws IllegalFormException, RequiredFieldMissingException, IOException, IllegalSubmissionException, ConsentFillFactory.UnknownFieldException {
        Submission submission = submissionService.createSubmissionByFormData(eventGroup, name, formData);
        submissionService.checkIfSubmissionIsLegal(submission);
        sendSuccessMail(submission);
        submissionRepository.save(submission);
        model.addAttribute("form", submission.getForm());
        return "redirect:/form/" + eventGroup + "/" + name + "/success";
    }

    private void sendSuccessMail(Submission submission) throws IOException, ConsentFillFactory.UnknownFieldException {
        InputValue emailValue =
                submission.getInputValueList().stream().filter(inputValue -> StringUtils.equals(inputValue.getInput().getHtmlId(), "email"))
                        .findFirst().orElse(null);
        if (emailValue == null) {
            log.debug("Submission of form with id {} contains no E-Mail!", submission.getForm().getId());
            return;
        }

        formService.sendSuccessMail(submission, emailValue.getData());
    }

    @RequestMapping("/form/{eventGroup}/{name}/success")
    public String formSuccess(@PathVariable String eventGroup, @PathVariable String name, Model model) {
        model.addAttribute("form", formService.getForm(eventGroup, name));
        return "form/form_success";
    }

    @RequestMapping("/form/{eventGroup}/{name}/login")
    public String formLogin(@PathVariable String eventGroup, @PathVariable String name, Model model) {
        model.addAttribute("form", formService.getForm(eventGroup, name));
        return "form/form_login";
    }

    @RequestMapping("/form/{eventGroup}/{name}/logo")
    public ResponseEntity<InputStreamResource> formLogo(@PathVariable String eventGroup, @PathVariable String name) throws IOException {
        File logoFile = getLogoFile(eventGroup, name);
        if (logoFile == null) {
            throw new IOException("logofile not found! formName " + name + " and eventGroupName " + eventGroup);
        }
        String path = logoFile.getAbsolutePath();
        String fileType = getFileType(path);
        HttpHeaders headers = getHttpHeaders(FilenameUtils.getName(path), fileType);
        return DownloadUtils.getResponseEntity(logoFile, headers);
    }

    private File getLogoFile(String eventGroupName, String formName) {
        File formDir = FileUtils.getFile(formDataDirectory, eventGroupName, formName);
        if (formDir == null || !formDir.exists()) {
            return null;
        }
        FileFilter logoFileFilter = new WildcardFileFilter("logo.*");
        File[] logoFiles = formDir.listFiles(logoFileFilter);
        if (logoFiles == null) {
            return null;
        }
        return Arrays.stream(logoFiles).findFirst().orElse(null);
    }



}

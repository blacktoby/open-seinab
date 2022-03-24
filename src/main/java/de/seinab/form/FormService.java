package de.seinab.form;


import de.seinab.backend.security.repositories.FormPermissionRepository;
import de.seinab.finance.ReferenceGenerator;
import de.seinab.finance.entities.ReferenceSettings;
import de.seinab.finance.repositories.ReferenceSettingsRepository;
import de.seinab.form.entities.Form;
import de.seinab.form.entities.Input;
import de.seinab.form.entities.InputOption;
import de.seinab.form.entities.Submission;
import de.seinab.form.repositories.EventGroupRepository;
import de.seinab.form.repositories.FormRepository;
import de.seinab.form.repositories.ValueRepository;
import de.seinab.mail.EmailAttachment;
import de.seinab.mail.EmailData;
import de.seinab.mail.MailService;
import de.seinab.pdf.ConsentFactory;
import de.seinab.pdf.ConsentFillFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class FormService {
    private static final Logger log = LoggerFactory.getLogger(FormService.class);
    @Value("${de.seinab.url}")
    private String seinabUrl;
    @Value("${de.seinab.form.data.directory}")
    private String formdataPath;

    @Autowired
    private Configuration configuration;
    @Autowired
    private MailService mailService;
    @Autowired
    private FormPermissionRepository formPermissionRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private EventGroupRepository eventGroupRepository;
    @Autowired
    private ReferenceSettingsRepository referenceSettingsRepository;
    @Autowired
    private ValueRepository valueRepository;

    public String findKey(Input input, Set<String> keySet) {
        return keySet.stream().filter(k -> StringUtils.equals(input.getName(), k)).findFirst().orElse(null);
    }

    /**
     * Fetches Form from Database sorted by InputGroups and Inputs
     *
     * @return Form
     */
    public Form getFormSorted(String eventGroupName, String name) {
        Form form = getForm(eventGroupName, name);
        limitInputOptions(form);
        sortForm(form);
        return form;
    }

    public void sortForm(Form form) {
        List<Input> inputList = form.getInputList();
        inputList = InputUtils.sortInputListByPositonAndGroup(inputList);
        form.setInputList(inputList);
    }

    public Form getForm(String eventGroupName, String formName) {
        Long eventGroupdId = eventGroupRepository.getIdByName(eventGroupName).getId();
        if (eventGroupdId == null) {
            return null;
        }
        return formRepository.getFormByNameAndEventGroupId(formName, eventGroupdId);
    }

    public void limitInputOptions(Form form) {
        form.getInputList().stream()
                .filter(i -> i instanceof ContainsInputOptions)
                .map(i -> (ContainsInputOptions) i)
                .forEach(this::disableLimitedOptions);
    }

    private void disableLimitedOptions(ContainsInputOptions input) {
        input.getInputOptions().stream()
                .filter(io -> inputOptionExceedsLimit(input.getId(), io))
                .forEach(io -> io.setDisabled(true));
    }

    public boolean inputOptionExceedsLimit(long inputId, InputOption inputOption) {
        int inputValueCount = valueRepository.countByInputIdAndData(inputId, inputOption.getValue());
        int limit = inputOption.getLimitCount();
        return limit > 0 && limit <= inputValueCount;
    }

    public void sendSuccessMail(Submission submission, String recipientEmail) throws IOException, ConsentFillFactory.UnknownFieldException {
        Form form = submission.getForm();
        HashMap<String, Object> data = new HashMap<>();
        data.put("logoLink", seinabUrl + "/form/" + form.getEventGroup().getName() + "/" + form.getName() + "/logo");
        data.put("formname", form.getDisplayName());
        data.put("message", form.getEmailMessage());
        String emailHtml = getEmailHtml(data);

        EmailData emailData = new EmailData();
        emailData.setRecipientEmail(recipientEmail);
        emailData.setHtmlMessage(emailHtml);
        emailData.setSenderName(form.getDisplayName());
        emailData.setSubject(form.getDisplayName() + " - Anmeldung");

        if(submission.getForm().getConsent() == null) {
            mailService.sendEmail(emailData);
            return;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        buildConsent(outStream, submission);

        EmailAttachment emailAttachment = new EmailAttachment();
        emailAttachment.setName("Einverständniserklärung.pdf");
        emailAttachment.setDescription("Einverständniserklärung");
        emailAttachment.setDataSource(new ByteArrayDataSource(outStream.toByteArray(), "application/pdf"));

        mailService.sendEmail(emailData, emailAttachment);
    }

    public void buildConsent(ByteArrayOutputStream outputStream, Submission submission)
            throws ConsentFillFactory.UnknownFieldException,
            IOException {
        String reference = buildReference(submission);
        String formPdfFile = submission.getForm().getConsent().getFormPdfFile();
        if(StringUtils.isNoneEmpty(formPdfFile)) {
            Form form = submission.getForm();
            File formDir = FileUtils.getFile(formdataPath, form.getEventGroup().getName(), form.getName());
            if (formDir == null || !formDir.exists()) {
                return;
            }
            new ConsentFillFactory(reference, new File(formDir, formPdfFile)).build(submission).saveWithStream(outputStream);
        } else {
            new ConsentFactory(reference).build(submission).saveWithStream(outputStream);
        }
    }

    public String buildReference(Submission submission) {
        ReferenceSettings referenceSettings = referenceSettingsRepository.getByFormId(submission.getForm().getId());
        if (referenceSettings == null) {
            return submission.getForm().getDisplayName();
        }
        return new ReferenceGenerator(referenceSettings).generateReference(submission);
    }

    private String getEmailHtml(HashMap<String, Object> data) throws IOException {
        try {
            Template template = getMailTemplate();
            if (template == null) {
                throw new IOException();
            }
            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException | IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }

    private Template getMailTemplate() throws IOException {
        return configuration.getTemplate("email/basicEmail.ftlh");
    }
}

package de.seinab.finance;

import de.seinab.finance.entities.FormFeeTurnover;
import de.seinab.finance.entities.ReferenceSettings;
import de.seinab.finance.repositories.FormFeeTurnoverRepository;
import de.seinab.finance.repositories.ReferenceSettingsRepository;
import de.seinab.form.entities.Form;
import de.seinab.form.entities.Submission;
import de.seinab.form.repositories.FormRepository;
import de.seinab.form.repositories.SubmissionRepository;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormFeeTurnoverService {

    @Autowired
    private ReferenceSettingsRepository referenceSettingsRepository;
    @Autowired
    private FormFeeTurnoverRepository turnoverRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    public void doFormFeeTurnoverMatching(String eventGroup, String formName, List<GVRKUms.UmsLine> umsLines) {
        Form form = formRepository.getFormByNameAndEventGroupName(formName, eventGroup);
        List<Submission> submissions = submissionRepository.getAllByFormId(form.getId());
        List<FormFeeTurnover> formFeeTurnovers = matchTurnoverListToSubmissionList(form, submissions, umsLines);
        turnoverRepository.saveAll(formFeeTurnovers);
    }

    public List<FormFeeTurnover> matchTurnoverListToSubmissionList(Form form, List<Submission> submissionList,
                                                                   List<GVRKUms.UmsLine> umsLines) {
        ReferenceSettings referenceSettings = referenceSettingsRepository.getByFormId(form.getId());
        ReferenceGenerator referenceGenerator = new ReferenceGenerator(referenceSettings);
        return umsLines.stream()
                .map(umsLine -> matchTurnoverToSubmission(submissionList, umsLine, referenceGenerator))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FormFeeTurnover matchTurnoverToSubmission(List<Submission> submissionList,
                                                      GVRKUms.UmsLine umsLine,
                                                      ReferenceGenerator referenceGenerator) {
        List<String> usageLines = umsLine.usage;
        String usage = StringUtils.join(usageLines, " ");
        if(StringUtils.isEmpty(usage)) {
            return null;
        }

        return submissionList.stream()
                .filter(submission -> usageMatchesSubmission(submission, usage, referenceGenerator))
                .map(submission -> createFormFeeTurnover(submission, umsLine))
                .findFirst().orElse(null);
    }

    private boolean usageMatchesSubmission(Submission submission, String usage, ReferenceGenerator referenceGenerator) {
        String reference = referenceGenerator.generateReference(submission);

        usage = StringUtils.remove(usage, " ");
        usage = StringUtils.lowerCase(usage);
        usage = usage.replace("ü", "ue")
                .replace("ö", "oe")
                .replace("ä", "ae")
                .replace("ß", "ss");

        String finalUsage = usage;
        List<String> referenceValues = referenceGenerator.getValueStrings(submission);


        boolean alternativeValuesMatch =
                checkIfAlternativeValuesMatch(finalUsage, referenceValues, referenceGenerator.getSettings().getEventName());

        referenceValues.add(referenceGenerator.getSettings().getEventName());
        boolean allValuesMatch = referenceValues.stream().allMatch(value -> StringUtils.contains(finalUsage, value));


        boolean matches = StringUtils.contains(usage, reference) || allValuesMatch || alternativeValuesMatch;
        if(matches) {
            System.out.println("Usage: " +usage + " matches values: " +StringUtils.join(referenceValues, ", "));
        }
        return matches;
    }

    private boolean checkIfAlternativeValuesMatch(String finalUsage, List<String> referenceValues, String eventName) {
        boolean alternativeValuesMatch = false;
        boolean createAlternateValues = referenceValues.stream().anyMatch(value ->
                value.contains("ae") || value.contains("ue") || value.contains("oe") || value.contains("ss"));
        if(createAlternateValues) {
            List<String> alternativeValues = referenceValues.stream().map(value -> {
                if (value.contains("ae") || value.contains("ue") || value.contains("oe") || value.contains("ss")) {
                    return value.replace("ue", "u")
                            .replace("oe", "o")
                            .replace("ae", "a")
                            .replace("ss", "s");
                } else {
                    return value;
                }
            })
            .collect(Collectors.toList());
            alternativeValues.add(eventName);
            alternativeValuesMatch = alternativeValues.stream().allMatch(value -> StringUtils.contains(finalUsage, value));
        }
        return alternativeValuesMatch;
    }

    private FormFeeTurnover createFormFeeTurnover(Submission submission, GVRKUms.UmsLine umsLine) {
        FormFeeTurnover turnover = submission.getFormFeeTurnover();
        if(submission.getFormFeeTurnover() == null) {
            turnover = new FormFeeTurnover();
        }
        turnover.setValue(umsLine.value.getLongValue());
        turnover.setDate(umsLine.bdate);
        turnover.setSubmission(submission);
        return turnover;
    }

}

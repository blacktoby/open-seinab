package de.seinab.form;

import de.seinab.form.entities.*;
import de.seinab.form.repositories.SubmissionRepository;
import de.seinab.form.repositories.ValueRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionService {

    @Autowired
    private FormService formService;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ValueRepository valueRepository;

    public Submission createSubmissionByFormData(String eventGroupName, String formName, MultiValueMap<String, String> formData)
            throws IllegalFormException, RequiredFieldMissingException {
        return createSubmissionByFormData(eventGroupName, formName, formData.toSingleValueMap());
    }

    public Submission createSubmissionByFormData(String eventGroupName, String formName, Map<String, String> formData)
            throws IllegalFormException, RequiredFieldMissingException {
        Form form = formService.getForm(eventGroupName, formName);
        if (form == null) {
            throw new IllegalFormException("Tried to create Submission for Form with name which is not existing in the database: " + formName);
        }

        Submission submission = new Submission();
        submission.setForm(form);
        submission.setDateSubmitted(new Date());

        List<InputValue> valueList = new ArrayList<>();
        List<String> missingInputNames = new ArrayList<>();
        for(Input input : form.getInputList()) {
            String key = formService.findKey(input, formData.keySet());
            String data = "";
            if(key != null) {
                data = formData.get(key);
            }
            if(input.getType() == InputType.checkbox) {
                data = key == null || StringUtils.equals(formData.get(key), "false") ? "false" : "true";
            }
            if(input.isRequired() && (StringUtils.isEmpty(data) || StringUtils.equals(data, "false"))) {
                missingInputNames.add(input.getName());
            }

            InputValue value = new InputValue();
            value.setData(data);
            value.setForm(form);
            value.setInput(input);
            value.setSubmission(submission);
            valueList.add(value);
        }

        if(!missingInputNames.isEmpty()) {
            throw new RequiredFieldMissingException(missingInputNames);
        }

        submission.setInputValueList(valueList);
        return submission;
    }

    public void checkIfSubmissionIsLegal(Submission submission) throws IllegalSubmissionException {
        boolean isExceedingOptionLimit = submission.getInputValueList().stream()
                .anyMatch(this::exceedsOptionLimit);

        if(isExceedingOptionLimit) {
            throw new IllegalSubmissionException();
        }
    }

    private boolean exceedsOptionLimit(InputValue inputValue) {
        Input input = inputValue.getInput();
        if(!(inputValue.getInput() instanceof ContainsInputOptions)) {
            return false;
        }

        return ((ContainsInputOptions) input)
                .getInputOptions().stream()
                .filter(io -> StringUtils.equals(io.getValue(), inputValue.getData()))
                .anyMatch(io -> formService.inputOptionExceedsLimit(input.getId(), io));
    }
}

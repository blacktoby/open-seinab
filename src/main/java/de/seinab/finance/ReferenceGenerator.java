package de.seinab.finance;

import de.seinab.finance.entities.ReferenceSettings;
import de.seinab.finance.models.ReferenceInputValue;
import de.seinab.form.InputUtils;
import de.seinab.form.entities.InputValue;
import de.seinab.form.entities.Submission;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReferenceGenerator {
    private ReferenceSettings settings;

    public ReferenceGenerator(ReferenceSettings settings) {
        this.settings = settings;
    }

    public String generateReference(Submission submission) {
        List<String> valueStrings = getValueStrings(submission);

        String eventName = settings.getEventName();
        if(eventName == null) {
            eventName = "";
        }
        return eventName + settings.getSeperator() + StringUtils.join(valueStrings, settings.getSeperator());
    }


    public List<String> getValueStrings(Submission submission) {
        List<InputValue> referenceValues = getReferenceValues(submission);
        return referenceValues.stream().map(InputUtils::beautifyInputValueData)
                .filter(Objects::nonNull)
                .map(StringUtils::lowerCase)
                .map(s -> s.replace("ü", "ue")
                        .replace("ö", "oe")
                        .replace("ä", "ae")
                        .replace("ß", "ss")
                )
                .map(s -> RegExUtils.removeAll(s, "[^a-z0-9]"))
                .collect(Collectors.toList());
    }

    private List<InputValue> getReferenceValues(Submission submission) {
        return submission.getInputValueList().stream()
                .map(this::mapToReferenceInputValue)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(riv -> riv.getReferenceInput().getReferencePosition()))
                .map(ReferenceInputValue::getInputValue)
                .collect(Collectors.toList());
    }

    private ReferenceInputValue mapToReferenceInputValue(InputValue inputValue) {
        return settings.getReferenceInputs().stream()
                .filter(r -> inputValue.getInput().getId().equals(r.getInput().getId()))
                .map(r -> new ReferenceInputValue(inputValue, r))
                .findFirst().orElse(null);
    }

    public ReferenceSettings getSettings() {
        return settings;
    }

    public void setSettings(ReferenceSettings settings) {
        this.settings = settings;
    }
}

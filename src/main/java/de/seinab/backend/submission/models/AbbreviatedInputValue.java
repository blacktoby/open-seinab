package de.seinab.backend.submission.models;

import de.seinab.backend.submission.entities.AbbreviatedInput;

public class AbbreviatedInputValue {
    private AbbreviatedInput abbreviatedInput;
    private String value;

    public AbbreviatedInputValue(AbbreviatedInput abbreviatedInput, String value) {
        this.abbreviatedInput = abbreviatedInput;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AbbreviatedInput getAbbreviatedInput() {
        return abbreviatedInput;
    }

    public void setAbbreviatedInput(AbbreviatedInput abbreviatedInput) {
        this.abbreviatedInput = abbreviatedInput;
    }
}

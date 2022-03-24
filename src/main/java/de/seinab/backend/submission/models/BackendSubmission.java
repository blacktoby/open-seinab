package de.seinab.backend.submission.models;

import de.seinab.form.entities.Submission;

import java.util.List;

public class BackendSubmission extends Submission {
    public BackendSubmission(Submission submission) {
        super(submission);
    }

    private List<AbbreviatedInputValue> abbreviatedInputValueList;

    public List<AbbreviatedInputValue> getAbbreviatedInputValueList() {
        return abbreviatedInputValueList;
    }

    public void setAbbreviatedInputValueList(List<AbbreviatedInputValue> abbreviatedInputValueList) {
        this.abbreviatedInputValueList = abbreviatedInputValueList;
    }
}

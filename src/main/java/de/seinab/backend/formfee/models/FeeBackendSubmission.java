package de.seinab.backend.formfee.models;

import de.seinab.backend.submission.models.BackendSubmission;
import de.seinab.finance.entities.FormFeeTurnover;
import de.seinab.form.entities.Submission;

public class FeeBackendSubmission extends BackendSubmission {

    public FeeBackendSubmission(Submission submission, FormFeeTurnover formFeeTurnover) {
        super(submission);
    }

    private FormFeeTurnover formFeeTurnover;

    @Override
    public FormFeeTurnover getFormFeeTurnover() {
        return formFeeTurnover;
    }

    @Override
    public void setFormFeeTurnover(FormFeeTurnover formFeeTurnover) {
        this.formFeeTurnover = formFeeTurnover;
    }
}

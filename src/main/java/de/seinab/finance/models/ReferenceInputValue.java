package de.seinab.finance.models;

import de.seinab.finance.entities.ReferenceInput;
import de.seinab.form.entities.InputValue;

public class ReferenceInputValue {
    private InputValue inputValue;
    private ReferenceInput referenceInput;

    public ReferenceInputValue(InputValue inputValue, ReferenceInput referenceInput) {
        this.inputValue = inputValue;
        this.referenceInput = referenceInput;
    }

    public InputValue getInputValue() {
        return inputValue;
    }

    public void setInputValue(InputValue inputValue) {
        this.inputValue = inputValue;
    }

    public ReferenceInput getReferenceInput() {
        return referenceInput;
    }

    public void setReferenceInput(ReferenceInput referenceInput) {
        this.referenceInput = referenceInput;
    }
}

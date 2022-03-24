package de.seinab.form;

import java.util.List;

public class RequiredFieldMissingException extends Exception{
    private List<String> missingInputNames;

    public RequiredFieldMissingException(String message) {
        super(message);
    }

    public RequiredFieldMissingException(List<String> missingInputNames) {
        super("" + missingInputNames.size() +" Inputs are missing!");
        this.missingInputNames = missingInputNames;
    }

    public List<String> getMissingInputNames() {
        return missingInputNames;
    }

    public void setMissingInputNames(List<String> missingInputNames) {
        this.missingInputNames = missingInputNames;
    }
}

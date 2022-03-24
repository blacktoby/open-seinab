package de.seinab.backend.datatable.models;

import java.util.List;
import java.util.stream.Collectors;

public class MissingFieldError {
    private List<MissingFieldNameStatus> fieldErrors;

    public MissingFieldError(List<String> missingInputNames) {
        this.fieldErrors = missingInputNames.stream().map(MissingFieldNameStatus::new).collect(Collectors.toList());
    }

    public List<MissingFieldNameStatus> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<MissingFieldNameStatus> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}

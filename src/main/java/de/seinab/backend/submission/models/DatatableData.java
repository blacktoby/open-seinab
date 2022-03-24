package de.seinab.backend.submission.models;

public class DatatableData {
    private Object text;
    private String value;

    public DatatableData(Object text, String value) {
        this.text = text;
        this.value = value;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

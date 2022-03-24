package de.seinab.backend.datatable.models;

public class MissingFieldNameStatus {
    private String name;
    private String status = "Dieses Feld wird benötigt";

    public MissingFieldNameStatus() {}

    public MissingFieldNameStatus(String name) {
        this.name = name;
    }

    public MissingFieldNameStatus(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

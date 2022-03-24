package de.seinab.form.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "input_value")
@PrimaryKeyJoinColumn(name="valueId")
public class InputValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "formId")
    @NotNull
    private Form form;

    @ManyToOne
    @JoinColumn(name = "inputId")
    @NotNull
    private Input input;

    @ManyToOne
    @JoinColumn(name = "submissionId")
    @NotNull
    private Submission submission;

    @NotNull
    private String data;

    @NotNull
    private Date dateInserted = new Date();
    private Date dateEdited;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getDateInserted() {
        return dateInserted;
    }

    public void setDateInserted(Date dateInserted) {
        this.dateInserted = dateInserted;
    }

    @Column
    @NotNull
    public Date currentDateEdited() {
        return new Date();
    }

    public Date getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }
}

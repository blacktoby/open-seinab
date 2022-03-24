package de.seinab.form.entities;

import de.seinab.finance.entities.FormFeeTurnover;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Submission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "formId")
    private Form form;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "submission")
    private List<InputValue> inputValueList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne(mappedBy = "submission")
    private FormFeeTurnover formFeeTurnover;

    public Submission() {}

    public Submission(Submission submission) {
        this.id = submission.getId();
        this.form = submission.getForm();
        this.inputValueList = submission.getInputValueList();
        this.dateSubmitted = submission.getDateSubmitted();
        this.formFeeTurnover = submission.getFormFeeTurnover();
    }

    private Date dateSubmitted;

    public Long getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<InputValue> getInputValueList() {
        return inputValueList;
    }

    public void setInputValueList(List<InputValue> inputValueList) {
        this.inputValueList = inputValueList;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public FormFeeTurnover getFormFeeTurnover() {
        return formFeeTurnover;
    }

    public void setFormFeeTurnover(FormFeeTurnover formFeeTurnover) {
        this.formFeeTurnover = formFeeTurnover;
    }
}

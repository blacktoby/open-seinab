package de.seinab.pdf.entities;

import de.seinab.form.entities.Form;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Consent implements Serializable {
    @Id
    @OneToOne(targetEntity = Form.class)
    @JoinColumn(unique = true)
    @NotNull
    private Form form;

    private String text;

    private String formPdfFile;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFormPdfFile() {
        return formPdfFile;
    }

    public void setFormPdfFile(String formPdfFile) {
        this.formPdfFile = formPdfFile;
    }
}

package de.seinab.finance.entities;

import de.seinab.form.entities.Form;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Entity
public class FormFee implements Serializable {
    @Id
    @OneToOne(targetEntity = Form.class)
    @JoinColumn(unique = true)
    @NotNull
    private Form form;

    private long fee;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public long getFee() {
        return fee;
    }

    public String getFeeString() {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        return new DecimalFormat("#.##", formatSymbols).format(fee / 100.);
    }

    public void setFee(long fee) {
        this.fee = fee;
    }
}

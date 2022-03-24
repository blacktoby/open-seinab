package de.seinab.form.entities;

import javax.persistence.Entity;

@Entity
public class EmailInput extends Input{

    public EmailInput() {
        super.setName("E-Mail");
        super.setHtmlId("templates/email");
        super.setType(InputType.email);
        super.setRequired(true);
    }

    @Override
    public void setRequired(boolean required) {
        throw new IllegalAccessError("Setting required is not allowed!");
    }

    @Override
    public void setName(String name) {
        throw new IllegalAccessError("Setting name is not allowed!");
    }

    @Override
    public void setHtmlId(String htmlId) {
        throw new IllegalAccessError("Setting htmlId is not allowed!");
    }

    @Override
    public void setType(InputType type) {
        throw new IllegalAccessError("Setting type is not allowed!");
    }
}

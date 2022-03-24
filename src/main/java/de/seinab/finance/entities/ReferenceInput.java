package de.seinab.finance.entities;

import de.seinab.form.entities.Input;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class ReferenceInput implements Serializable {
    @Id
    @OneToOne(targetEntity = Input.class)
    @JoinColumn(name = "input_id")
    private Input input;

    @NotNull
    private int referencePosition;

    @NotNull
    @ManyToOne(targetEntity = ReferenceSettings.class)
    private ReferenceSettings referenceSettings;

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public ReferenceSettings getReferenceSettings() {
        return referenceSettings;
    }

    public void setReferenceSettings(ReferenceSettings referenceSettings) {
        this.referenceSettings = referenceSettings;
    }

    public int getReferencePosition() {
        return referencePosition;
    }

    public void setReferencePosition(int referencePosition) {
        this.referencePosition = referencePosition;
    }
}

package de.seinab.backend.submission.entities;

import de.seinab.form.entities.Input;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class CountStatisticAbbreviated extends CountStatistic{
    @NotNull
    @OneToOne(targetEntity = AbbreviatedInput.class)
    @JoinColumn(name = "key_abbreviated_input_id")
    private AbbreviatedInput abbreviatedKeyInput;

    @Override
    public Input getKeyInput() {
        throw new IllegalAccessError();
    }

    @Override
    public void setKeyInput(Input keyInput) {
        throw new IllegalAccessError();
    }

    public AbbreviatedInput getAbbreviatedKeyInput() {
        return abbreviatedKeyInput;
    }

    public void setAbbreviatedKeyInput(AbbreviatedInput keyInput) {
        this.abbreviatedKeyInput = keyInput;
    }
}

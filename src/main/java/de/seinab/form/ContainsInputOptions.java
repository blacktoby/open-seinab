package de.seinab.form;

import de.seinab.form.entities.InputOption;

import java.util.List;

public interface ContainsInputOptions {
    Long getId();
    List<InputOption>  getInputOptions();
}

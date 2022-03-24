package de.seinab.form.entities;

import de.seinab.form.ContainsInputOptions;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.List;

@Entity
public class RadioInput extends Input implements ContainsInputOptions {
    private String title;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = InputOption.class)
    @JoinColumn(name = "input_id")
    @OrderBy("position")
    private List<InputOption> inputOptions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<InputOption> getInputOptions() {
        return inputOptions;
    }

    public void setInputOptions(List<InputOption> inputOptions) {
        this.inputOptions = inputOptions;
    }
}

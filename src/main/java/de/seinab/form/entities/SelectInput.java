package de.seinab.form.entities;

import de.seinab.form.ContainsInputOptions;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Entity
public class SelectInput extends Input implements ContainsInputOptions {
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = InputOption.class)
    @JoinColumn(name = "input_id")
    @OrderBy("position")
    private List<InputOption> inputOptions;

    private String defaultText;

    @OneToOne(targetEntity = Input.class)
    private Input optionsKeyInput;

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    @Override
    public List<InputOption> getInputOptions() {
        return inputOptions;
    }

    public void setInputOptions(List<InputOption> inputOptions) {
        this.inputOptions = inputOptions;
    }

    public Input getOptionsKeyInput() {
        return optionsKeyInput;
    }

    public void setOptionsKeyInput(Input optionsKeyInput) {
        this.optionsKeyInput = optionsKeyInput;
    }

    public MultiValueMap<String, InputOption> getInputOptionsByKey() {
        MultiValueMap<String, InputOption> optionsKey = new LinkedMultiValueMap<>();
        inputOptions.forEach(o -> optionsKey.add(o.getInputKey(), o));
        optionsKey.values().forEach(optionList -> optionList.sort(Comparator.comparingInt(InputOption::getPosition)));
        return optionsKey;
    }

    public InputOption getInputOptionByValue(String optionValue) {
        return inputOptions.stream().filter(o -> StringUtils.equals(o.getValue(), optionValue))
                .findFirst().orElse(null);
    }
}

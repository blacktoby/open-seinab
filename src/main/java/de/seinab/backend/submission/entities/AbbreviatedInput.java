package de.seinab.backend.submission.entities;

import de.seinab.form.entities.Form;
import de.seinab.form.entities.Input;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class AbbreviatedInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne(targetEntity = Form.class)
    @JoinColumn(name = "form_id")
    private Form form;

    @NotNull
    @ManyToOne(targetEntity = Input.class)
    @JoinColumn(name = "input_id")
    private Input keyInput;

    @NotNull
    @OneToMany(targetEntity = AbbreviatedKeyValue.class)
    @JoinColumn(name = "abbreviated_input_id")
    private List<AbbreviatedKeyValue> abbreviatedKeyValueList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Input getKeyInput() {
        return keyInput;
    }

    public void setKeyInput(Input keyInput) {
        this.keyInput = keyInput;
    }

    public List<AbbreviatedKeyValue> getAbbreviatedKeyValueList() {
        return abbreviatedKeyValueList;
    }

    public void setAbbreviatedKeyValueList(List<AbbreviatedKeyValue> abbreviatedKeyValueList) {
        this.abbreviatedKeyValueList = abbreviatedKeyValueList;
    }

    public Map<String, String> getKeyValueMap() {
        return abbreviatedKeyValueList.stream()
                .collect(Collectors.toMap(AbbreviatedKeyValue::getKey, AbbreviatedKeyValue::getValue));
    }
}

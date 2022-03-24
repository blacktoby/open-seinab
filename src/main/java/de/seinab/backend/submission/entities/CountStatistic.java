package de.seinab.backend.submission.entities;

import de.seinab.form.entities.Form;
import de.seinab.form.entities.Input;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class CountStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    @ManyToOne(targetEntity = Form.class)
    @JoinColumn(name = "form_id")
    private Form form;

    @OneToOne(targetEntity = Input.class)
    @JoinColumn(name = "key_input_id")
    private Input keyInput;

    private String keyData;

    @Transient
    private long count;

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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getKeyData() {
        return keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }

    public Input getKeyInput() {
        return keyInput;
    }

    public void setKeyInput(Input keyInput) {
        this.keyInput = keyInput;
    }
}

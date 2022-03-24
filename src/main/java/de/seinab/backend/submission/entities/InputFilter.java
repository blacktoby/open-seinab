package de.seinab.backend.submission.entities;

import de.seinab.backend.security.entities.FormPermission;
import de.seinab.form.entities.Input;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class InputFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(targetEntity = FormPermission.class)
    @JoinColumn(name = "form_permission_id")
    private FormPermission formPermission;

    @NotNull
    @OneToOne
    @JoinColumn(name = "input_id")
    private Input input;

    @NotNull
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

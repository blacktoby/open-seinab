package de.seinab.form.entities;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"form_id", "htmlId"})
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Input {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Form.class)
    @JoinColumn(name = "form_id")
    @UniqueElements
    @NotNull
    private Form form;

    @NotNull
    private String htmlId;

    @NotNull
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private InputType type;

    private String defaultValue;

    @ManyToOne(targetEntity = InputGroup.class)
    @JoinColumn(name = "input_group")
    private InputGroup inputGroup;

    @NotNull
    private int position;

    @Column(columnDefinition = "boolean default false")
    private boolean required = false;

    public Long getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public void setHtmlId(String htmlId) {
        this.htmlId = htmlId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getInputGroupPosition() {
        return inputGroup == null ? position : inputGroup.getPosition();
    }

    public InputGroup getInputGroup() {
        return inputGroup;
    }

    public void setInputGroup(InputGroup inputGroup) {
        this.inputGroup = inputGroup;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}

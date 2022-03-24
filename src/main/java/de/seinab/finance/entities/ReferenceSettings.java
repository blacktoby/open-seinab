package de.seinab.finance.entities;

import de.seinab.form.entities.Form;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
public class ReferenceSettings implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = Form.class)
    @JoinColumn(name = "form_id", unique = true)
    @NotNull
    private Form form;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ReferenceInput.class)
    @JoinColumn(name = "reference_settings_id")
    @OrderBy("referencePosition")
    @NotNull
    private List<ReferenceInput> referenceInputs;

    private String eventName;

    @NotNull
    private String seperator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<ReferenceInput> getReferenceInputs() {
        return referenceInputs;
    }

    public void setReferenceInputs(List<ReferenceInput> referenceInputs) {
        this.referenceInputs = referenceInputs;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

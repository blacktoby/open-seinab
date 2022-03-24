package de.seinab.form.entities;

import de.seinab.EventGroup;
import de.seinab.finance.entities.FormFee;
import de.seinab.pdf.entities.Consent;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "event_group_id"})
})
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = EventGroup.class, optional = false)
    @JoinColumn(name = "event_group_id")
    @NotNull
    private EventGroup eventGroup;

    @NotNull
    private String displayName;
    @NotNull
    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "form")
    private Consent consent;

    @OneToOne(mappedBy = "form")
    private FormFee formFee;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "form_id")
    @OrderBy("position")
    private List<Input> inputList;

    private String password;

    @Column(columnDefinition = "text")
    private String emailMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventGroup getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public Consent getConsent() {
        return consent;
    }

    public FormFee getFormFee() {
        return formFee;
    }

    public void setFormFee(FormFee formFee) {
        this.formFee = formFee;
    }

    public List<Input> getInputList() {
        return inputList;
    }

    public void setInputList(List<Input> inputList) {
        this.inputList = inputList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }
}

package de.seinab.backend.security.entities;

import de.seinab.EventGroup;
import de.seinab.backend.submission.entities.InputFilter;
import de.seinab.form.entities.Form;
import de.seinab.form.entities.Input;
import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class FormPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(targetEntity = de.seinab.form.entities.Form.class)
    @JoinColumn(name = "form_id")
    private Form form;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = InputFilter.class)
    @JoinColumn(name = "form_permission_id")
    private List<InputFilter> inputFilterList;

    @NotNull
    private boolean writePermitted;

    @NotNull
    private boolean formfeePermitted;

    @NotNull
    private boolean bankingPermitted;

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

    public List<Input> getFilteredInputList() {
        Set<Long> filteredInputIdSet = inputFilterList.stream()
                .map(inputFilter -> inputFilter.getInput().getId())
                .collect(Collectors.toSet());
        return form.getInputList().stream().filter(i -> !filteredInputIdSet.contains(i.getId())).collect(Collectors.toList());
    }

    public String getFilteredInputData(String inputName) {
        return inputFilterList.stream().filter(inputFilter -> StringUtils.equals(inputName, inputFilter.getInput().getName()))
                .map(InputFilter::getData).findFirst().orElse("");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isWritePermitted() {
        return writePermitted;
    }

    public void setWritePermitted(boolean writePermitted) {
        this.writePermitted = writePermitted;
    }

    public EventGroup getEventGroup() {
        if(form == null) {
            return null;
        }
        return form.getEventGroup();
    }

    public List<InputFilter> getInputFilterList() {
        return inputFilterList;
    }

    public void setInputFilterList(List<InputFilter> inputFilterList) {
        this.inputFilterList = inputFilterList;
    }

    public boolean isFormfeePermitted() {
        return formfeePermitted;
    }

    public void setFormfeePermitted(boolean formfeePermitted) {
        this.formfeePermitted = formfeePermitted;
    }

    public boolean isBankingPermitted() {
        return bankingPermitted;
    }

    public void setBankingPermitted(boolean bankingPermitted) {
        this.bankingPermitted = bankingPermitted;
    }
}

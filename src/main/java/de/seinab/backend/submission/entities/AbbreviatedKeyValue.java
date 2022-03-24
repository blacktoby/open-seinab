package de.seinab.backend.submission.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"abbreviated_input_id", "value_key"})
})
public class AbbreviatedKeyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(targetEntity = AbbreviatedInput.class)
    @JoinColumn(name = "abbreviated_input_id")
    private AbbreviatedInput abbreviatedInput;

    @NotNull
    @Column(name = "value_key")
    private String key;
    @NotNull
    private String value;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AbbreviatedInput getAbbreviatedInput() {
        return abbreviatedInput;
    }

    public void setAbbreviatedInput(AbbreviatedInput abbreviatedInput) {
        this.abbreviatedInput = abbreviatedInput;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

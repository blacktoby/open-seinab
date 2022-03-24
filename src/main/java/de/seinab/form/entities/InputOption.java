package de.seinab.form.entities;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class InputOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String value;
    private int position;
    private Integer limitCount;

    @Transient
    private boolean disabled;

    private String inputKey;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getLimitCount() {
        if(limitCount == null) {
            return 0;
        }
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    @NotNull
    public String getValue() {
        if(value != null) {
            return value;
        }
        String value = StringUtils.remove(text, " ");
        value = StringUtils.lowerCase(value);
        return value;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }
}

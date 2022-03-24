package de.seinab.backend.banking.models;

public class BankingCredentials {
    public String userid;
    public String pin;

    public BankingCredentials(String userid, String pin) {
        this.userid = userid;
        this.pin = pin;
    }
}

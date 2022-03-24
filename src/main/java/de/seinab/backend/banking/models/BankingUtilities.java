package de.seinab.backend.banking.models;

import java.util.concurrent.CompletableFuture;

public class BankingUtilities {
    private BankingStatus bankingStatus = BankingStatus.IDLE;
    private CompletableFuture<String> currentFuture;
    private Thread jobThread;

    public BankingStatus getBankingStatus() {
        return bankingStatus;
    }

    public void setBankingStatus(BankingStatus bankingStatus) {
        this.bankingStatus = bankingStatus;
    }

    public CompletableFuture<String> getCurrentFuture() {
        return currentFuture;
    }

    public void setCurrentFuture(CompletableFuture<String> currentFuture) {
        this.currentFuture = currentFuture;
    }

    public Thread getJobThread() {
        return jobThread;
    }

    public void setJobThread(Thread jobThread) {
        this.jobThread = jobThread;
    }
}

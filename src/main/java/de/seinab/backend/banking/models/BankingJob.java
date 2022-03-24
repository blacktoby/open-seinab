package de.seinab.backend.banking.models;

public class BankingJob {
    public String userName;
    public String eventGroup;
    public String jobDescription;

    public BankingJob(String userName, String eventGroup) {
        this.userName = userName;
        this.eventGroup = eventGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankingJob that = (BankingJob) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        return eventGroup != null ? eventGroup.equals(that.eventGroup) : that.eventGroup == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (eventGroup != null ? eventGroup.hashCode() : 0);
        return result;
    }
}

package net.exposedrecords.web.domain;

import java.util.Date;

/**
 * 
 * @author drone
 */
public class Subscription {
    private String email;
    private String confirmationCode;
    private Date creationDate;
    private Date confirmationDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public boolean isConfirmed() {
        return confirmationDate != null;
    }
}

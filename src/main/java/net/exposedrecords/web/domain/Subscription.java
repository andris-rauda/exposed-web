package net.exposedrecords.web.domain;

import java.util.Date;

/**
 * 
 * @author drone
 */
public class Subscription {
    private String email;
    private String verificationCode;
    private Date creationDate;
    private Date verificationDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public boolean isConfirmed() {
        return verificationDate != null;
    }
}

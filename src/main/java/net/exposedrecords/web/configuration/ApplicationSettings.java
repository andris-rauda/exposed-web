package net.exposedrecords.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationSettings {
    private String domain;
    private String googleAnalyticsToken;
    private String mailerPassword;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGoogleAnalyticsToken() {
        return googleAnalyticsToken;
    }

    public void setGoogleAnalyticsToken(String googleAnalyticsToken) {
        this.googleAnalyticsToken = googleAnalyticsToken;
    }

    public String getMailerPassword() {
        return mailerPassword;
    }

    public void setMailerPassword(String mailerPassword) {
        this.mailerPassword = mailerPassword;
    }
}

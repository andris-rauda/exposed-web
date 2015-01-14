package net.exposedrecords.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationSettings {
    private String name;
    private String googleAnalyticsToken;
    private String mailerPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

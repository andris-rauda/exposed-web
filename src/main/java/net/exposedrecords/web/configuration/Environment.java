package net.exposedrecords.web.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Component
public class Environment {

    private static final Log log = LogFactory.getLog(Environment.class);

    private String name;
    private Persistence persistence;

    public Environment() {
        initName();
        initPersistence();
    }

    protected void initName() {
        // check custom system property
        String environmentName = System.getProperty("application.environment");

        if (StringUtils.isEmpty(environmentName)) {
            environmentName = initNameForAppEngine();
        }
        this.name = environmentName;
        if (log.isInfoEnabled()) {
            log.info("Determined application environment.name: " + name);
        }
    }

    protected String initNameForAppEngine() {
        try {
            // check appengine application version
            Class<?> clazz = Class
                    .forName("com.google.appengine.api.utils.SystemProperty");

            Field applicationVersionField = ReflectionUtils.findField(clazz,
                    "applicationVersion");
            if (!ReflectionUtils.isPublicStaticFinal(applicationVersionField)) {
                return null;
            }
            Object applicationVersionProperty = applicationVersionField
                    .get(null);

            Method getMethod = ReflectionUtils.findMethod(
                    applicationVersionProperty.getClass(), "get");

            String applicationVersion = (String) ReflectionUtils.invokeMethod(
                    getMethod, applicationVersionProperty);

            if (applicationVersion == null) {
                return null;
            }
            String[] applicationVersionSplit = applicationVersion.split("\\.");
            if (applicationVersionSplit.length != 2) {
                if (log.isWarnEnabled()) {
                    log.warn("Invalid appengine applicationVersion property: "
                            + applicationVersion);
                }
                return null;
            }

            // this appears to be AppEngine
            persistence = Persistence.AppEngine;

            return applicationVersionSplit[0];
        } catch (ClassNotFoundException e) {
            // AppEngine is not supported, ignore
            log.info("AppEngine is not supported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to extract appengine applciationVersion", e);
            }
        } catch (IllegalAccessException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to extract appengine applciationVersion", e);
            }
        }
        return null;
    }

    protected void initPersistence() {
        // check custom system property
        String persistenceProperty = System
                .getProperty("application.persistence");

        if (StringUtils.isEmpty(persistenceProperty)) {
            persistence = Persistence.InMemory;
        } else {
            persistence = Persistence.valueOf(persistenceProperty);
        }
        if (log.isInfoEnabled()) {
            log.info("Determined application persistence: " + persistence);
        }
    }

    public String getName() {
        return name;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public static enum Persistence {
        InMemory, MongoDB, AppEngine;
    }
}

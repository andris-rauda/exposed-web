package net.exposedrecords.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.appengine.api.utils.SystemProperty;

/**
 * Handles all requests for the application index page.
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory
            .getLogger(IndexController.class);

    // TODO use more dynamic way to validate existing pages (check messages?)
    private static final String NORMAL_ERROR_PAGE = "miss";

    private static final List<String> MENU_ITEMS;
    static {
        MENU_ITEMS = new ArrayList<String>(4);
        MENU_ITEMS.add("home");
        MENU_ITEMS.add("demandVinyl");
        MENU_ITEMS.add("contact");
    }

    private String googleAnalyticsToken;

    /**
     * Exception thrown when invalid URL has been detected.
     */
    public class PageNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PageNotFoundException(String message) {
            super(message);
        }
    }

    @PostConstruct
    public void setupGoogleAnalytics() {
        // check custom system property
        String environment = System.getProperty("application.environment");

        if (StringUtils.isEmpty(environment)) {

            // check appengine application version
            String applicationVersion = SystemProperty.applicationVersion.get();

            if (applicationVersion != null) {
                String[] applicationVersionSplit = applicationVersion
                        .split("\\.");
                assert applicationVersionSplit.length == 2 : "Invalid appengine applicationVersion property";

                environment = applicationVersionSplit[0];
            }
        }

        if ("production".equals(environment)) {
            googleAnalyticsToken = "UA-56087455-1";
        } else if (environment != null && environment.startsWith("test")) {
            googleAnalyticsToken = "UA-56087455-2";
        }
    }

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "forward:home";
    }

    /**
     * Simply selects the home view to render by returning its name.
     * 
     * @throws Exception
     */
    @RequestMapping(value = { "/{page}" }, method = RequestMethod.GET)
    public String page(@PathVariable String page, Model model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!MENU_ITEMS.contains(page) && !NORMAL_ERROR_PAGE.equals(page)) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid page: " + page);
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (logger.isInfoEnabled()) {
            logger.info("page: " + page);
        }

        model.addAttribute("menuItems", MENU_ITEMS);
        model.addAttribute("templateName", page);

        if (googleAnalyticsToken != null) {
            model.addAttribute("googleAnalyticsAccount", googleAnalyticsToken);
        }

        // fetch email from cookies
        if ("demandVinyl".equals(page)) {
            for (Cookie cookie : request.getCookies()) {
                if (SubscriptionController.COOKIE_EMAIL
                        .equals(cookie.getName())) {
                    model.addAttribute("email", cookie.getValue());
                }
                if (SubscriptionController.COOKIE_EMAIL_VERIFIED.equals(cookie
                        .getName())) {
                    model.addAttribute("emailVerified", cookie.getValue());
                }
            }
        }

        return "index";
    }

    /**
     * Simple keep-alive request for external monitor.
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }

    /**
     * Support robots.
     */
    @RequestMapping(value = "/robots", method = RequestMethod.GET)
    public String robots() {
        return "robots";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    @ExceptionHandler(Exception.class)
    public String error(Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(e.getMessage(), e);
        }
        return "error";
    }

}

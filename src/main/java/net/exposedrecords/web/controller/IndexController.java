package net.exposedrecords.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import net.exposedrecords.web.configuration.ApplicationSettings;
import net.exposedrecords.web.domain.Subscription;
import net.exposedrecords.web.service.SubscriptionService;

/**
 * Handles all requests for the application index page.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory
            .getLogger(IndexController.class);

    private static final java.util.logging.Logger jlog = java.util.logging.Logger
            .getLogger(IndexController.class.getName());

    // TODO use more dynamic way to validate existing pages (check messages?)
    private static final String NORMAL_ERROR_PAGE = "miss";

    private static final List<String> MENU_ITEMS;
    static {
        MENU_ITEMS = new ArrayList<String>(4);
        MENU_ITEMS.add("home");
        MENU_ITEMS.add("releases");
        MENU_ITEMS.add("artists");
        MENU_ITEMS.add("contact");
    }

    private String googleAnalyticsToken;
    private SubscriptionService subscriptionService;

    /**
     * Exception thrown when invalid URL has been detected.
     */
    public class PageNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PageNotFoundException(String message) {
            super(message);
        }
    }

    @Resource
    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.googleAnalyticsToken = applicationSettings
                .getGoogleAnalyticsToken();
    }

    @Resource
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping("/")
    String index(Model model) {
        return "index";
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
        if ("releases".equals(page)) {
            Cookie subscriptionIdCookie = WebUtils.getCookie(request,
                    SubscriptionController.COOKIE_SUBSCRIPTION_ID);
            if (subscriptionIdCookie != null) {
                String subscriptionId = subscriptionIdCookie.getValue();

                try {
                    Subscription subscription = subscriptionService
                            .get(subscriptionId);
                    model.addAttribute("subscription", subscription);
                } catch (DataAccessResourceFailureException e) {
                    Subscription subscriptionStub = new Subscription();
                    subscriptionStub.setId(subscriptionId);
                    model.addAttribute("subscription", subscriptionStub);
                    model.addAttribute("exception", e);
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
    @RequestMapping(value = "/robots.txt", method = RequestMethod.GET)
    public String robots() {
        return "robots";
    }

    @RequestMapping(value = "/error", method = { RequestMethod.GET,
            RequestMethod.POST })
    @ExceptionHandler(Exception.class)
    public String error(Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(e.getMessage(), e);
        }
        jlog.log(Level.SEVERE, "Unexpected exception", e);
        return "error";
    }
}

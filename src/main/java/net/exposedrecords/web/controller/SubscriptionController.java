package net.exposedrecords.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.exposedrecords.web.domain.SubscriptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles all requests regarding email subscription.
 */
@Controller
public class SubscriptionController {

    private static final Logger logger = LoggerFactory
            .getLogger(SubscriptionController.class);

    public static final String COOKIE_EMAIL = "email";
    public static final String COOKIE_EMAIL_VERIFIED = "emailVerified";

    private SubscriptionService subscriptionService;

    @Resource
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Add email to storage, send verification, remember the fact in cookie.
     */
    @RequestMapping(value = { "/subscribe" }, method = RequestMethod.POST)
    public String subscribe(@RequestParam("email") String email,
            HttpServletResponse response) {

        if (logger.isInfoEnabled()) {
            logger.info("subscribe: " + email);
        }

        subscriptionService.add(email);

        Cookie cookie = new Cookie(COOKIE_EMAIL, email);
        response.addCookie(cookie);

        return "redirect:demandVinyl";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/verify" }, method = RequestMethod.GET)
    public String verify(@RequestParam("email") String email,
            @RequestParam("code") String confirmationCode,
            HttpServletResponse response) {

        if (logger.isInfoEnabled()) {
            logger.info("verify: " + email);
        }

        if (subscriptionService.verify(email, confirmationCode)) {
            Cookie cookie = new Cookie(COOKIE_EMAIL_VERIFIED,
                    Boolean.TRUE.toString());
            response.addCookie(cookie);
        }

        return "redirect:demandVinyl";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/unsubscribe" }, method = RequestMethod.GET)
    public String unsubscribe(@RequestParam("email") String email,
            @RequestParam("code") String confirmationCode,
            HttpServletRequest request, HttpServletResponse response) {

        if (logger.isInfoEnabled()) {
            logger.info("unsubscribe: " + email + ", code: " + confirmationCode);
        }

        if (subscriptionService.reset(email, confirmationCode)) {
            resetCookies(request, response);
        }

        return "redirect:demandVinyl";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/unsubscribe" }, method = RequestMethod.POST)
    public String unsubscribe(@RequestParam("email") String email,
            HttpServletRequest request, HttpServletResponse response) {

        if (logger.isInfoEnabled()) {
            logger.info("unsubscribe: " + email);
        }

        subscriptionService.reset(email);

        resetCookies(request, response);

        return "redirect:demandVinyl";
    }

    protected void resetCookies(HttpServletRequest request,
            HttpServletResponse response) {
        for (Cookie cookie : request.getCookies()) {
            if (SubscriptionController.COOKIE_EMAIL.equals(cookie.getName())
                    || SubscriptionController.COOKIE_EMAIL_VERIFIED
                            .equals(cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                response.addCookie(cookie);
            }
        }
    }
}

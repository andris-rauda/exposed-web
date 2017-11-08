package net.exposedrecords.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.exposedrecords.web.service.SubscriptionService;

/**
 * Handles all requests regarding email subscription.
 */
@Controller
public class SubscriptionController {

    private static final Logger logger = LoggerFactory
            .getLogger(SubscriptionController.class);

    public static final String COOKIE_SUBSCRIPTION_ID = "sId";

    private static final int COOKIE_MAX_AGE = 5 * 365 * 24 * 60 * 60; // 5 years

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
            HttpServletResponse response, RedirectAttributes redirectAttributes) {

        if (logger.isInfoEnabled()) {
            logger.info("subscribe: " + email);
        }

        try {
            String id = subscriptionService.add(email);
    
            Cookie cookie = new Cookie(COOKIE_SUBSCRIPTION_ID, id);
            cookie.setMaxAge(COOKIE_MAX_AGE);
            response.addCookie(cookie);
    
            redirectAttributes.addAttribute("action", "verificationSent");
        } catch (DataAccessResourceFailureException e) {
            if (logger.isErrorEnabled()) {
                logger.error("subscription verification failed: " + e.getMessage());
            }
            redirectAttributes.addAttribute("action", "subscriptionFailed");
        }
        
        return "redirect:releases";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/verify" }, method = RequestMethod.GET)
    public String verify(@RequestParam("sId") String subscriptionId,
            @RequestParam("code") String verificationCode,
            HttpServletResponse response, RedirectAttributes redirectAttributes) {

        if (logger.isInfoEnabled()) {
            logger.info("verify: " + subscriptionId);
        }

        try {
            boolean success = subscriptionService.verify(subscriptionId, verificationCode);

            if (logger.isInfoEnabled()) {
                logger.info("verification: " + subscriptionId + ", success: " + success);
            }
    
            redirectAttributes.addAttribute("action", success ? "subscriptionVerified" : "subscriptionRejected");
        } catch (DataAccessResourceFailureException e) {
            if (logger.isErrorEnabled()) {
                logger.error("subscription verification failed: " + e.getMessage());
            }
            redirectAttributes.addAttribute("action", "subscriptionFailed");
        }

        return "redirect:releases";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/unsubscribe" }, method = RequestMethod.GET)
    public String unsubscribe(@RequestParam("sId") String subscriptionId,
            @RequestParam("code") String verificationCode,
            HttpServletRequest request, HttpServletResponse response) {

        if (logger.isInfoEnabled()) {
            logger.info("unsubscribe: " + subscriptionId + ", code: " + verificationCode);
        }

        if (subscriptionService.reset(subscriptionId, verificationCode)) {
            resetCookies(request, response);
        }

        return "redirect:releases";
    }

    /**
     * Remove email from storage, clean up cookie.
     */
    @RequestMapping(value = { "/unsubscribe" }, method = RequestMethod.POST)
    public String unsubscribe(@RequestParam("sId") String subscriptionId,
            HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        if (logger.isInfoEnabled()) {
            logger.info("unsubscribe: " + subscriptionId);
        }

        try {
            subscriptionService.reset(subscriptionId);
        } catch (DataAccessResourceFailureException e) {
            if (logger.isErrorEnabled()) {
                logger.error("subscription verification failed: " + e.getMessage());
            }
            redirectAttributes.addAttribute("action", "subscriptionFailed");
        }
        resetCookies(request, response);

        return "redirect:releases";
    }

    protected void resetCookies(HttpServletRequest request,
            HttpServletResponse response) {
        if (request.getCookies() == null) {
            return;
        }
        for (Cookie cookie : request.getCookies()) {
            if (SubscriptionController.COOKIE_SUBSCRIPTION_ID.equals(cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                response.addCookie(cookie);
            }
        }
    }
}

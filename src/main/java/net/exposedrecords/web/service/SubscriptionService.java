package net.exposedrecords.web.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.exposedrecords.web.domain.Subscription;
import net.exposedrecords.web.domain.SubscriptionRepository;

@Service
public class SubscriptionService {
    private static final Logger log = LoggerFactory
            .getLogger(SubscriptionService.class);

    private static final int CONFIRMATION_CODE_LENGTH = 16;
    private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private MailingService mailingService;
    private SubscriptionRepository subscriptionRepository;

    @Resource
    public void setMailingService(MailingService mailingService) {
        this.mailingService = mailingService;
    }

    @Resource
    public void setSubscriptionRepository(
            SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    protected String encode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return new String(result);
    }

    protected String generateConfirmationCode() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            byte[] bytes = new byte[CONFIRMATION_CODE_LENGTH];
            random.nextBytes(bytes);

            return encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate confirmation code",
                    e);
        }
    }

    /**
     * @param email
     * @return id
     */
    public String add(String email) {
        Subscription subscription = new Subscription();
        subscription.setCreationDate(new Date());
        subscription.setEmail(email);
        subscription.setVerificationCode(generateConfirmationCode());
        
        log.info("subscriptionRepository: " + subscriptionRepository);
        
        subscriptionRepository.save(subscription);

        String id = subscription.getId();
        String verificationCode = subscription.getVerificationCode();

        mailingService
                .send(email,
                        "Subscription verification from ExposedRecords.NET",
                        String.format(
                                "Open this link to verify: http://exposedrecords.net/verify?sId=%s&code=%s",
                                id, verificationCode));

        if (log.isInfoEnabled()) {
            log.info(String.format("Sent id: %s, code: %s to email: %s",
                    id, verificationCode, email));
        }
        
        return id;
    }

    public Subscription get(String subscriptionId) {
        return subscriptionRepository.findOne(subscriptionId);
    }

    public boolean verify(String id, String confirmationCode) {

        Subscription subscription = subscriptionRepository.findOne(id);

        if (subscription != null
                && subscription.getVerificationCode().equals(confirmationCode)) {
            subscription.setVerificationDate(new Date());
            subscriptionRepository.save(subscription);
            return true;
        }
        return false;
    }

    public void reset(String id) {
        subscriptionRepository.delete(id);
    }

    public boolean reset(String id, String confirmationCode) {
        Subscription subscription = subscriptionRepository.findOne(id);

        if (subscription == null) {
            return true;
        }

        if (subscription.getVerificationCode().equals(confirmationCode)) {
            reset(id);
            return true;
        } else {
            return false;
        }
    }
}

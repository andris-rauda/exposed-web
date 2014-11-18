package net.exposedrecords.web.domain;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private static final Logger logger = LoggerFactory
            .getLogger(SubscriptionService.class);

    private static final int CONFIRMATION_CODE_LENGTH = 16;
    private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private SubscriptionRepository subscriptionRepository;

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

    public void add(String email) {
        Subscription subscription = new Subscription();
        subscription.setCreationDate(new Date());
        subscription.setEmail(email);
        subscription.setConfirmationCode(generateConfirmationCode());

        subscriptionRepository.save(subscription);

        // FIXME send verification email

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Sent code: %s to email: %s",
                    subscription.getConfirmationCode(), email));
        }
    }

    public boolean verify(String email, String confirmationCode) {

        Subscription subscription = subscriptionRepository.findOne(email);

        if (subscription != null
                && subscription.getConfirmationCode().equals(confirmationCode)) {
            subscription.setConfirmationDate(new Date());
            subscriptionRepository.save(subscription);
            return true;
        }
        return false;
    }

    public void reset(String email) {
        subscriptionRepository.delete(email);
    }

    public boolean reset(String email, String confirmationCode) {
        Subscription subscription = subscriptionRepository.findOne(email);

        if (subscription == null) {
            return true;
        }

        if (subscription.getConfirmationCode().equals(confirmationCode)) {
            reset(email);
            return true;
        } else {
            return false;
        }
    }
}

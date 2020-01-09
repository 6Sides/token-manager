package dashflight.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.xml.bind.DatatypeConverter;

/**
 * Utilities for generating token fingerprints to strengthen security of JWTs
 */
public class FingerprintService {

    private static final int FINGERPRINT_LENGTH = 64;

    private SecureRandom secureRandom;
    private MessageDigest digest;


    public FingerprintService() {
        this.secureRandom = new SecureRandom();

        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a random fingerprint of length FINGERPRINT_LENGTH
     */
    public String generateRandomFingerprint() {
        byte[] randomBytes = new byte[FINGERPRINT_LENGTH];
        secureRandom.nextBytes(randomBytes);

        return DatatypeConverter.printHexBinary(randomBytes);
    }

    /**
     * Hashes a fingerprint with SHA-256
     */
    public String hashFingerprint(String fgp) {
        byte[] fingerprintDigest = digest.digest(fgp.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(fingerprintDigest);
    }

}

package dashflight.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;
import java.nio.ByteBuffer;

/**
 * Utility for hashing & verifying user passwords
 */
public class PasswordHasher {

    private static Hasher hasher = BCrypt.withDefaults();
    private static Verifyer verifier = BCrypt.verifyer();

    /**
     * Overwrites the byte array to remove its contents from memory.
     */
    private static void overwriteBytes(byte[] arr) {
        ByteBuffer.wrap(arr).put(new byte[arr.length]);
    }

    /**
     * Hashes the specified byte array and overwrites it.
     * @return The hashed password
     */
    public static byte[] hashPassword(byte[] password) {
        byte[] result = hasher.hash(12, password);
        overwriteBytes(password);
        return result;
    }

    /**
     * Checks if the password matches the password hash. Then overwrites password data.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean verifyPassword(byte[] password, byte[] passwordHash) {
        boolean result = verifier.verify(password, passwordHash).verified;
        overwriteBytes(password);
        return result;
    }

}

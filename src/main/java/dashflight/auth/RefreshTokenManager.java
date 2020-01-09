package dashflight.auth;

import dashflight.auth.cache.RefreshTokenCache;
import java.security.SecureRandom;
import javax.xml.bind.DatatypeConverter;

/**
 * Handles all logic regarding refresh tokens.
 *
 * Refresh tokens are relatively long-lived tokens used to
 * obtain JWT access tokens. Refresh tokens only interface with
 * our authentication servers, never the authorization servers.
 */
public class RefreshTokenManager {

    /**
     * Size of the refresh token. The token hash returned to the
     * user is `TOKEN_LENGTH * 2` bytes.
     */
    private static final int TOKEN_LENGTH = 64;

    /**
     * The amount of time (in seconds) that the token should remain valid.
     */
    private static final int TOKEN_TTL = 3600 * 24 * 7;

    private RefreshTokenCache redisCache;
    private SecureRandom secureRandom;

    public RefreshTokenManager() {
        this.secureRandom = new SecureRandom();
        this.redisCache = new RefreshTokenCache();
    }

    /**
     * Generates and persists a refresh token for the specified user.
     * Stores the token in the cache, and returns the hash of the token.
     *
     * @param userId The user to associate the refresh token with.
     * @return The refresh token to send back to the user.
     */
    public String obtainToken(String userId) {
        byte[] randBytes = new byte[TOKEN_LENGTH];
        this.secureRandom.nextBytes(randBytes);

        String token = DatatypeConverter.printHexBinary(randBytes);

        if (redisCache.setWithExpiry(token, TOKEN_TTL, userId)) {
            // If token was set, return it
            return token;
        }

        return null;
    }

    /**
     * Verifies if the provided refreshToken is valid.
     */
    public String verifyToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }

        return redisCache.get(refreshToken);
    }

    /**
     * Invalidates the specified token. If the token is not in the cache,
     * nothing happens.
     */
    public void invalidateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return;
        }

        redisCache.del(refreshToken);
    }

}

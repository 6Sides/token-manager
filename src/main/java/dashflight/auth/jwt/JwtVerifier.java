package dashflight.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dashflight.auth.cache.JwtCache;

/**
 * Handles verifying and decoding JWTs
 */
public class JwtVerifier extends JwtOperator {

    private JwtCache jwtCache = new JwtCache();

    /**
     * Verifies a JWT and decodes it if it is valid.
     *
     * How it works:
     *      1. token is deciphered.
     *      2. JWT is verified using the signing algorithm. Any time expiration checks are performed here.
     *      3. The hash of the provided fingerprint is matched against the user_fingerprint claim in the payload.
     *          If they match, the token is valid. Otherwise the JWT is rejected.
     */
    public DecodedJWT decodeJwtToken(String token, String fingerprint) {
        String fgpHash = fgpService.hashFingerprint(fingerprint);
        // String decipheredToken = tokenCipher.decipherToken(token);

        if (jwtCache.has(fgpHash)) {
            throw new JWTVerificationException("That token has been revoked");
        }

        JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA512(keyManager.getPublicKey(), null))
                .withIssuer(ISSUER)
                .acceptIssuedAt(10)
                .withClaim("user_fingerprint", fgpHash)
                .build();

        return jwtVerifier.verify(token);
    }
}

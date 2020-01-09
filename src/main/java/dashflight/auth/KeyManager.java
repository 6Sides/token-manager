package dashflight.auth;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface KeyManager {

    RSAPublicKey getPublicKey();

    RSAPrivateKey getPrivateKey();

}

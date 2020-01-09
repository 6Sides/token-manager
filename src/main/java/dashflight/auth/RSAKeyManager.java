package dashflight.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages RSA keys. In production this will interface with s3 to store/retrieve
 * new keys and simplify key rotation.
 */
public class RSAKeyManager implements KeyManager {

    private static final String KEY_ID = "JWT-signer";

    private transient RSAPublicKey publicKey;
    private transient RSAPrivateKey privateKey;

    private static KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public RSAKeyManager() {
        try {
            privateKey = this.jsonToPrivKey(new FileInputStream(new File(KEY_ID + "_priv.json")));

            if (privateKey != null) {
                // We have private key, find the matching public key

                String requestURL = "http://localhost:8080/auth/jwks";
                URL pubKeyRequest = new URL(requestURL);
                URLConnection connection = pubKeyRequest.openConnection();
                connection.setDoOutput(true);

                List<Map<String, String>> keys =
                        new ObjectMapper().readValue(pubKeyRequest.openStream(), new TypeReference<List<Map<String, String>>>(){});

                publicKey = jsonToPubKey(new ByteArrayInputStream(new ObjectMapper().writeValueAsString(keys.get(0)).getBytes()));
            } else {
                // We don't have a private key, generate a new pair

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(1024);
                KeyPair keyPair = keyPairGenerator.genKeyPair();

                publicKey = (RSAPublicKey) keyPair.getPublic();
                privateKey = (RSAPrivateKey) keyPair.getPrivate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.keyToJson(this.publicKey, "initial_key");
        this.keyToJson(this.privateKey, "initial_key");
    }

    static RSAPrivateKey jsonToPrivKey(InputStream input) {
        try {
            Map<String, String> data = new ObjectMapper().readValue(input, new TypeReference<HashMap<String, String>>(){});

            BigInteger modulus = new BigInteger(Base64.getDecoder().decode(data.get("n")));
            BigInteger exponent = new BigInteger(Base64.getDecoder().decode(data.get("e")));

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, exponent);
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    static RSAPublicKey jsonToPubKey(InputStream input) {
        try {
            Map<String, String> data = new ObjectMapper().readValue(input, new TypeReference<HashMap<String, String>>(){});

            BigInteger modulus = new BigInteger(Base64.getDecoder().decode(data.get("n")));
            BigInteger exponent = new BigInteger(Base64.getDecoder().decode(data.get("e")));

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void keyToJson(RSAKey key, String kid) {
        Map<String, String> data = new HashMap<>();

        data.put("kty", "RSA");
        data.put("alg", "RS512");
        data.put("use", "sig");
        data.put("kid", kid);
        data.put("n", new String(Base64.getEncoder().encode(key.getModulus().toByteArray())));

        String filename = KEY_ID;

        if (key instanceof RSAPublicKey) {
            filename += "_pub";
            RSAPublicKey pubKey = ((RSAPublicKey) key);

            data.put("e", new String(Base64.getEncoder().encode(pubKey.getPublicExponent().toByteArray())));

        } else if (key instanceof RSAPrivateKey) {
            filename += "_priv";
            RSAPrivateKey privKey = ((RSAPrivateKey) key);

            data.put("e", new String(Base64.getEncoder().encode(privKey.getPrivateExponent().toByteArray())));
        }

        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(filename + ".json"), data);
        } catch(IOException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }
}

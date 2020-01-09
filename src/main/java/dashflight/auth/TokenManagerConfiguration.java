package dashflight.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TokenManagerConfiguration {

    private static TokenManagerConfiguration instance;

    public static TokenManagerConfiguration getInstance() {
        return instance;
    }

    public static void initializeWithValuesFrom(InputStream yaml) throws IOException {
        instance = new TokenManagerConfiguration(yaml);
    }

    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    private final String BUCKET;
    private final String PUBLIC_KEY;
    private final String PRIVATE_KEY;

    private final String REDIS_HOST;
    private final int REDIS_PORT;

    private TokenManagerConfiguration(InputStream yaml) throws IOException {
        Map<String, String> data = new ObjectMapper(new YAMLFactory()).readValue(yaml, new TypeReference<Map<String, String>>(){});

        this.ACCESS_KEY = data.get("access_key");
        this.SECRET_KEY = data.get("secret_key");
        this.BUCKET = data.get("bucket");
        this.PUBLIC_KEY = data.get("public_key");
        this.PRIVATE_KEY = data.get("private_key");

        this.REDIS_HOST = data.get("redis_host");
        this.REDIS_PORT = Integer.parseInt(data.get("redis_port"));
    }

    public String getACCESS_KEY() {
        return ACCESS_KEY;
    }

    public String getSECRET_KEY() {
        return SECRET_KEY;
    }

    public String getBUCKET() {
        return BUCKET;
    }

    public String getPUBLIC_KEY() {
        return PUBLIC_KEY;
    }

    public String getPRIVATE_KEY() {
        return PRIVATE_KEY;
    }

    public String getREDIS_HOST() {
        return REDIS_HOST;
    }

    public int getREDIS_PORT() {
        return REDIS_PORT;
    }
}

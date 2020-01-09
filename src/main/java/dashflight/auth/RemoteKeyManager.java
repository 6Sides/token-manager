package dashflight.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RemoteKeyManager implements KeyManager {

    private static String ACCESS_KEY;
    private static String SECRET_KEY;
    private static String BUCKET;
    private static String PUBLIC_KEY_PATH;
    private static String PRIVATE_KEY_PATH;

    static {
        TokenManagerConfiguration config = TokenManagerConfiguration.getInstance();

        ACCESS_KEY = config.getACCESS_KEY();
        SECRET_KEY = config.getSECRET_KEY();
        BUCKET = config.getBUCKET();
        PUBLIC_KEY_PATH = config.getPUBLIC_KEY();
        PRIVATE_KEY_PATH = config.getPRIVATE_KEY();
    }

    private AmazonS3 s3client;

    public RemoteKeyManager() {
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public RSAPublicKey getPublicKey() {
        S3Object s3object = s3client.getObject(BUCKET, PUBLIC_KEY_PATH);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        return RSAKeyManager.jsonToPubKey(inputStream);
    }

    public RSAPrivateKey getPrivateKey() {
        S3Object s3object = s3client.getObject(BUCKET, PRIVATE_KEY_PATH);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        return RSAKeyManager.jsonToPrivKey(inputStream);
    }
}

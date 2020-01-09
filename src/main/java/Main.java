import com.fasterxml.jackson.databind.ObjectMapper;
import dashflight.auth.TokenManagerConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TokenManagerConfiguration.initializeWithValuesFrom(new FileInputStream(new File("config.yaml")));

        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(TokenManagerConfiguration.getInstance()));
    }
}

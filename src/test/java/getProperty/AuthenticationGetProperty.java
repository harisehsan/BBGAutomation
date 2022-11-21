package getProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AuthenticationGetProperty {
    private Properties prop = new Properties();

    private void fileInputStream() throws IOException {
        prop.load(new FileInputStream("src/test/java/properties/authentication.properties"));
    }

    public String oxidKey() throws IOException {
        fileInputStream();
        return prop.getProperty("OXID_API_KEY");
    }

    public String smDBUserName() throws IOException {
        fileInputStream();
        return prop.getProperty("SHOPMANAGEMENT_DB_USERNAME");
    }

    public String smDBPassword() throws IOException {
        fileInputStream();
        return prop.getProperty("SHOPMANAGEMENT_DB_PASSWORD");
    }

    public String skOrderkey() throws IOException {
        fileInputStream();
        return prop.getProperty("SKORDER_API_KEY");
    }

    public String newRelicEmail() throws IOException {
        fileInputStream();
        return prop.getProperty("NEW_RELIC_EMAIL");
    }

    public String newRelicPassword() throws IOException {
        fileInputStream();
        return prop.getProperty("NEW_RELIC_PASSWORD");
    }

    public String wilsonPubStageKey() throws IOException {
        fileInputStream();
        return prop.getProperty("WILSON_STAGE_PUB_KEY");
    }





}

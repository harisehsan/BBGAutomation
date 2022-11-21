package properties;

import getProperty.AuthenticationGetProperty;

import java.io.IOException;

public class WilsonOrderProperty {

    AuthenticationGetProperty authenticationGetProperty = new AuthenticationGetProperty();

    public WilsonOrderProperty() throws IOException {
        setEmail(authenticationGetProperty.newRelicEmail());
        setPasskey(authenticationGetProperty.newRelicPassword());
    }


    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPasskey(String password) {
        this.password = password;
    }

    private String email;
    private String password;

}

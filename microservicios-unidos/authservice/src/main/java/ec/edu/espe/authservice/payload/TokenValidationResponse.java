package ec.edu.espe.authservice.payload;

public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String roles;
    private String message;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid, String username, String roles, String message) {
        this.valid = valid;
        this.username = username;
        this.roles = roles;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
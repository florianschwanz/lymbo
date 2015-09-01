package de.interoberlin.lymbo.model.translate;

public class AccessControlItem {
    private String token_type;
    private String access_token;
    private String expires_in;
    private String scope;

    // --------------------
    // Getters / Setters
    // --------------------

    public String getToken_type() {
        return token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }
}
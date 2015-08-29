package de.interoberlin.lymbo.model.translate;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MicrosoftTranslator {
    private static final String TOKEN_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
    private static final String DETECT_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/Detect?";
    private static final String TRANSLATE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?";

    private static final String ENCODING = "UTF-8";
    private static final String contentType = "text/plain";

    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_SCOPE = "scope";

    private static final String PARAM_FROM = "from";
    private static final String PARAM_TO = "to";
    private static final String PARAM_TEXT = "text";

    private static final int RESPONSE_CODE_OKAY = 200;

    // --------------------
    // Methods
    // --------------------

    /**
     * Retrieves the temporary access control item
     *
     * @param clientId     id of the application
     * @param clientSecret secret key
     * @return access control item
     * @throws Exception
     */
    private static AccessControlItem getAccessControlItem(String clientId, String clientSecret) throws Exception {
        // Parameters
        ParamHolder ph = new ParamHolder();
        ph.add(new Param(PARAM_GRANT_TYPE, "client_credentials"));
        ph.add(new Param(PARAM_CLIENT_ID, URLEncoder.encode(clientId, ENCODING)));
        ph.add(new Param(PARAM_CLIENT_SECRET, URLEncoder.encode(clientSecret, ENCODING)));
        ph.add(new Param(PARAM_SCOPE, "http://api.microsofttranslator.com"));

        // Connection
        HttpsURLConnection con = (HttpsURLConnection) new URL(TOKEN_URL).openConnection();

        // Request header
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + ENCODING);
        con.setRequestProperty("Accept-Charset", ENCODING);

        // Execute request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(ph.getParamString());
        wr.flush();
        wr.close();

        try {
            if (con.getResponseCode() != RESPONSE_CODE_OKAY)
                throw new Exception("Error from Microsoft Translator API");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            // Evaluate response
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON
            return new Gson().fromJson(response.toString(), AccessControlItem.class);
        } finally {
            con.disconnect();
        }
    }

    /**
     * Translate a given text from one language to another
     *
     * @param clientId     id of the application
     * @param clientSecret secret key
     * @param text         text to be translated
     * @return translated text
     * @throws Exception
     */
    public static Language detectLanguage(String clientId, String clientSecret, String text) throws Exception {
        // Parameters
        ParamHolder ph = new ParamHolder();
        ph.add(new Param(PARAM_TEXT, URLEncoder.encode(text, ENCODING)));

        // Connection
        HttpURLConnection con = (HttpURLConnection) new URL(DETECT_URL + ph.getParamString()).openConnection();

        // Request header
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", contentType + "; charset=" + ENCODING);
        con.setRequestProperty("Accept-Charset", ENCODING);
        con.setRequestProperty("Authorization", "Bearer " + getAccessControlItem(clientId, clientSecret).getAccess_token().replaceAll("\"", ""));

        // Execute request
        try {
            if (con.getResponseCode() != RESPONSE_CODE_OKAY) {
                throw new Exception("Error from Microsoft Translator API");
            }
            return Language.fromString(inputStreamToString(con.getInputStream()));
        } finally {
            con.disconnect();
        }
    }

    /**
     * Translate a given text from one language to another
     *
     * @param clientId     id of the application
     * @param clientSecret secret key
     * @param from         source language
     * @param to           target language
     * @param text         text to be translated
     * @return translated text
     * @throws Exception
     */
    public static String getTranslation(String clientId, String clientSecret, Language from, Language to, String text) throws Exception {
        // Parameters
        ParamHolder ph = new ParamHolder();
        ph.add(new Param(PARAM_FROM, URLEncoder.encode(from.getLangCode(), ENCODING)));
        ph.add(new Param(PARAM_TO, URLEncoder.encode(to.getLangCode(), ENCODING)));
        ph.add(new Param(PARAM_TEXT, URLEncoder.encode(text, ENCODING)));

        // Connection
        HttpURLConnection con = (HttpURLConnection) new URL(TRANSLATE_URL + ph.getParamString()).openConnection();

        // Request header
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", contentType + "; charset=" + ENCODING);
        con.setRequestProperty("Accept-Charset", ENCODING);
        con.setRequestProperty("Authorization", "Bearer " + getAccessControlItem(clientId, clientSecret).getAccess_token().replaceAll("\"", ""));

        // Execute request
        try {
            if (con.getResponseCode() != RESPONSE_CODE_OKAY) {
                throw new Exception("Error from Microsoft Translator API");
            }
            return inputStreamToString(con.getInputStream());
        } finally {
            con.disconnect();
        }
    }

    /**
     * Converts an input stream to string
     *
     * @param inputStream input stream to be converted
     * @return string value
     */
    private static String inputStreamToString(final InputStream inputStream) throws IOException {
        final StringBuilder outputBuilder = new StringBuilder();

        String string;
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string.replaceAll("\uFEFF", ""));
            }
        }

        return outputBuilder.toString();
    }

    // --------------------
    // Inner classes
    // --------------------

    private class AccessControlItem {
        private String token_type;
        private String access_token;
        private String expires_in;
        private String scope;

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
}

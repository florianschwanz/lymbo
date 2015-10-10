package de.interoberlin.lymbo.model.webservice.translate;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.interoberlin.lymbo.model.webservice.Param;
import de.interoberlin.lymbo.model.webservice.ParamHolder;

public class MicrosoftDetectorTask extends AsyncTask<String, Void, Language> {
    private static final String DETECT_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/Detect?";

    private static final String ENCODING = "UTF-8";
    private static final String contentType = "text/plain";

    private static final String PARAM_TEXT = "text";

    private static final int RESPONSE_CODE_OKAY = 200;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Language doInBackground(String... params) {
        String accessToken = params[0];
        String text = params[1];

        try {
            return detectLanguage(accessToken, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Language result) {
        super.onPostExecute(result);
    }

    // --------------------
    // Methods
    // --------------------


    /**
     * Translate a given text from one language to another
     *
     * @param accessToken temporary access token
     * @param text        text to be translated
     * @return translated text
     * @throws Exception
     */
    public static Language detectLanguage(String accessToken, String text) throws Exception {
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
        con.setRequestProperty("Authorization", "Bearer " + accessToken.replaceAll("\"", ""));

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
}

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
import de.interoberlin.mate.lib.model.Log;

public class MicrosoftTranslatorTask extends AsyncTask<String, Void, String> {
    private static final String TRANSLATE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?";

    private static final String ENCODING = "UTF-8";
    private static final String contentType = "text/plain";

    private static final String PARAM_FROM = "from";
    private static final String PARAM_TO = "to";
    private static final String PARAM_TEXT = "text";

    private static final int RESPONSE_CODE_OKAY = 200;

    // --------------------
    // Constructors
    // --------------------

    public MicrosoftTranslatorTask() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String accessToken = params[0];
        ELanguage languageFrom = ELanguage.fromString(params[1]);
        ELanguage languageTo = ELanguage.fromString(params[2]);
        String text = params[3];

        try {
            return getTranslation(accessToken, languageFrom, languageTo, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    // --------------------
    // Methods
    // --------------------


    /**
     * Translate a given text from one language to another
     *
     * @param accessToken  temporary accessToken
     * @param from         source language
     * @param to           target language
     * @param text         text to be translated
     * @return translated text
     * @throws Exception
     */
    public static String getTranslation(String accessToken, ELanguage from, ELanguage to, String text) throws Exception {
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
        con.setRequestProperty("Authorization", "Bearer " + accessToken.replaceAll("\"", ""));

        // Execute request
        try {
            if (con.getResponseCode() != RESPONSE_CODE_OKAY) {
                Log.error("Error translating text RESPONSE CODE : " + con.getResponseCode());
                throw new Exception("Error from Microsoft Translator API");
            }
            return inputStreamToString(con.getInputStream()).replaceAll("\"", "");
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

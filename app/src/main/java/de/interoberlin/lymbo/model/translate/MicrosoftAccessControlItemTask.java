package de.interoberlin.lymbo.model.translate;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.mate.lib.model.Log;

public class MicrosoftAccessControlItemTask extends AsyncTask<String, Void, AccessControlItem> {
    private static final String TOKEN_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";

    private static final String ENCODING = "UTF-8";

    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_SCOPE = "scope";

    private static final int RESPONSE_CODE_OKAY = 200;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public MicrosoftAccessControlItemTask() {
    }

    public MicrosoftAccessControlItemTask(OnCompleteListener ocListener) {
        this.ocListener = ocListener;
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected AccessControlItem doInBackground(String... params) {
        String clientId = params[0];
        String clientSecret = params[1];

        try {
            return getAccessControlItem(clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AccessControlItem result) {
        super.onPostExecute(result);

        Context context = App.getContext();
        Resources res = context.getResources();

        if (result != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(res.getString(R.string.translator_access_item_token_type), result.getToken_type());
            editor.putString(res.getString(R.string.translator_access_item_access_token), result.getAccess_token());
            editor.putInt(res.getString(R.string.translator_access_item_expires_in), Integer.valueOf(result.getExpires_in()));
            editor.putString(res.getString(R.string.translator_access_item_scope), result.getScope());
            editor.putLong(res.getString(R.string.translator_access_item_timestamp), System.currentTimeMillis());
            editor.apply();

            if (ocListener != null)
                ocListener.onAccessControlItemRetrieved(result.getAccess_token());
        }
    }

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
            if (con.getResponseCode() != RESPONSE_CODE_OKAY) {
                Log.error("Error getting access token RESPONSE CODE : " + con.getResponseCode());

                Context context = App.getContext();
                Resources res = context.getResources();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(res.getString(R.string.translator_access_item_token_type), null);
                editor.putString(res.getString(R.string.translator_access_item_access_token), null);
                editor.putString(res.getString(R.string.translator_access_item_expires_in), null);
                editor.putString(res.getString(R.string.translator_access_item_scope), null);
                editor.putLong(res.getString(R.string.translator_access_item_timestamp), 0L);
                editor.apply();

                Log.error("Error from Microsoft Translator API");
                throw new Exception("Error from Microsoft Translator API");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            // Evaluate response
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (response.toString().startsWith("ArgumentException")) {
                Log.error(response.toString());
                return null;
            } else {
                // Parse JSON
                return new Gson().fromJson(response.toString(), AccessControlItem.class);
            }
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
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onAccessControlItemRetrieved(String accessToken);
    }
}

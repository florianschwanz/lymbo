package de.interoberlin.lymbo.model.webservice.web;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.webservice.Param;
import de.interoberlin.lymbo.model.webservice.ParamHolder;
import de.interoberlin.mate.lib.model.Log;

public class LymboWebDownloadTask extends AsyncTask<String, Void, String> {
    public static final String TAG = LymboWebDownloadTask.class.toString();

    private static final String ENCODING = "UTF-8";
    private static final String contentType = "text/plain";

    private static final String PARAM_ID = "id";
    private static final String PARAM_AUTHOR = "author";

    private static final int RESPONSE_CODE_OKAY = 200;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public LymboWebDownloadTask() {
    }

    public LymboWebDownloadTask(OnCompleteListener ocListener) {
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
    protected String doInBackground(String... params) {
        String accessToken = params[0];
        String id = params[1];
        String author = params[2];

        if (accessToken != null && id != null && author != null) {
            try {
                return downloadLymbo(accessToken, id, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            Log.d(TAG, result);

            if (ocListener != null)
                ocListener.onLymboDownloaded(result);
        }
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Uploads a lymbo
     *
     * @param accessToken access token
     * @param id          id of lymbo
     * @param author      author of lymbo
     * @return
     * @throws Exception
     */
    private static String downloadLymbo(String accessToken, String id, String author) throws Exception {
        // Parameters
        ParamHolder ph = new ParamHolder();
        ph.add(new Param(PARAM_ID, URLEncoder.encode(id, ENCODING)));
        ph.add(new Param(PARAM_AUTHOR, URLEncoder.encode(author, ENCODING)));

        // Connection
        final String DOWNLOAD_URL = App.getContext().getResources().getString(R.string.lymbo_download_url);
        HttpURLConnection con = (HttpURLConnection) new URL(DOWNLOAD_URL).openConnection();

        // Request header
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + ENCODING);
        con.setRequestProperty("Accept-Charset", ENCODING);
        con.setRequestProperty("Authorization", "Bearer " + accessToken.replaceAll("\"", ""));

        // Execute request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(ph.getParamString());
        wr.flush();
        wr.close();

        try {
            if (con.getResponseCode() != RESPONSE_CODE_OKAY) {
                Log.e(TAG, "Error from Lymbo Web API Download");
                Log.e(TAG, "ResponseCode : " + con.getResponseCode());
                Log.e(TAG, "ResponseMethod : " + con.getRequestMethod());

                for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
                    Log.e(TAG, entry.getKey() + " : " + entry.getValue());
                }
                throw new Exception("Error from Lymbo Web API");
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
                Log.e(TAG, response.toString());
                return null;
            } else {
                return response.toString();
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
        void onLymboDownloaded(String response);
    }
}
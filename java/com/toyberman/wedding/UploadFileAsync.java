package com.toyberman.wedding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Toyberman Maxim on 9/7/15.
 */
public class UploadFileAsync extends AsyncTask<Void, Void, String> {

    private String title;
    private String date;
    private String description;
    private String location;
    private String uid;
    //needed to post parameters and file
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private String encodedImage;
    private Activity activity;
    private ProgressDialog progressDialog;


    public UploadFileAsync(String title, String date, String description, String location, String uid, String encodedImage, Activity activity) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.location = location;
        this.uid = uid;
        this.encodedImage = encodedImage;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", "uploading details...", true);

    }

    @Override
    protected String doInBackground(Void... voids) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String response = "";


        try {

            URL url = new URL(Constants.newWeddingURL);
            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            //set properties

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("file_contents", "myfile.jpg");
            //write data to output stream
            dos = new DataOutputStream(conn.getOutputStream());
            //post parameters
            //title parameter
            postParameter(dos, title, "title");
            //date parameter
            postParameter(dos, date, "date");

            // description parameter
            postParameter(dos, description, "description");
            // location parameter
            postParameter(dos, location, "location");
            // android_id parameter
            postParameter(dos, uid, "uid");
            //post file
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file_contents\";filename=" + "myfile.jpg" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of  maximum size

            dos.writeBytes(encodedImage);
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("WEDDING", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            //get response from server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            response = stringBuilder.toString();



            dos.flush();
            dos.close();
        } catch (Exception e) {
            Log.d("check", e.getMessage());
        }
        return response;
    }



    private void postParameter(DataOutputStream dos, String parameter, String parameter_name) throws IOException {

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + parameter_name + "\"" + lineEnd);
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dos.writeBytes("Content-Length: " + title.length() + lineEnd);
        dos.writeBytes(lineEnd);

        byte[] data = parameter.getBytes("UTF-8");
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        if(parameter_name.equals("date"))Log.d("UPLOADFILEASYNC",base64);
        dos.writeBytes(base64 + lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes(lineEnd);

    }

    @Override
    protected void onPostExecute(String response) {

        String wedding_id = "";
        try {
            wedding_id = String.valueOf(new JSONObject(response).get("wedding_id"));

        } catch (JSONException e) {
            Log.d("JSONException",e.getMessage());
        }

        progressDialog.dismiss();
        Intent main_intent = new Intent(activity, MainActivity.class);
        main_intent.putExtra("wedding_id", wedding_id);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(main_intent);
    }
}

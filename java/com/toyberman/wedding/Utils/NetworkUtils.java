package com.toyberman.wedding.Utils;

import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.toyberman.wedding.Entities.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Toyberman Maxim on 9/8/15.
 */
public class NetworkUtils {


    public static String postData(final String url, final ArrayList<Pair> post_parameters) {

                Webb webb = Webb.create();
                Request request = webb.post(url);

                for (Pair p : post_parameters)
                    request.param(p.getParameter_name(), p.getGetParameter_value());

                Response response = request.ensureSuccess().asString();
                String body = response.getBody().toString();


        return body;
    }

    public static InputStream getUrlData(String urlString) {
        InputStream inputStream = null;
        try {
            //obtaining languages for spinners
            URL url = new URL(urlString);
            inputStream = url.openStream();


        } catch (IOException e) {
            Log.d("WEDDING", e.getMessage());
        }
        return inputStream;
    }
}

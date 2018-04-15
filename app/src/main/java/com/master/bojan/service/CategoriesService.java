package com.master.bojan.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by bojan on 2.4.18..
 */

public class CategoriesService extends IntentService {

    private static final String CATEGORIES_LIST_ALL = "http://api.eventful.com/json/categories/list?&app_key=k35skmHWKcS6JBJx";

    public CategoriesService() {
        super("CategoriesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;

        try {
            Uri uri = Uri.parse(CATEGORIES_LIST_ALL).buildUpon().build();
            URL url = new URL(uri.toString());

            //Create request to Eventful API and open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //open inputStream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            //empty response
            if (stringBuffer.length() == 0) {
                return;
            }

            jsonResponse = stringBuffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCategoryDataFromJson(String jsonResponse) {
        final String NAME = "name";
        final String EVENTFUL_ID = "id";
        final String IMAGE_PATH = "image";
        final String IS_PRIMARY_CATEGORY = "is_primary_category";
        final String CATEGORY = "category";

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray categoriesArray = jsonObject.getJSONArray(CATEGORY);

            Vector<ContentValues> categoriesVector = new Vector<>(categoriesArray.length());
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryJson = categoriesArray.getJSONObject(i);
                String name = parseAmpersand(categoryJson.getString(NAME));
                String eventfulId = categoryJson.getString(EVENTFUL_ID);
                Boolean isPrimary = check
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String parseAmpersand(String name) {
        String parsed = name;
        if (name.contains("&amp;")) {
            parsed = name.replace("&amp;", "&");
        }
        return parsed;
    }

    private boolean checkIfPrimary(String id){
        boolean isPrimary = false;

        if(Arrays.asList(Custom.primary_category_ids).contains(id)){
            isPrimary=true;

        }
        return isPrimary;
    }

}

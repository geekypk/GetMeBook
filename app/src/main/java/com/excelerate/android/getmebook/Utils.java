package com.excelerate.android.getmebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class with methods to help perform the HTTP request and
 * parse the response.
 */
final class Utils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static int totalItems=0;
    private static int startIndex;
    private static int maxResults;
    private static String book_request_url;

    /**
     * Query the USGS dataset and return an {@link Book} object to represent a single earthquake.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> book = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return book;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        List<Book> books = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONObject totalObjects=baseJsonResponse.optJSONObject("totalItems");
            if(totalObjects==null) totalItems = 0;
            else totalItems = Integer.parseInt(totalObjects.toString());
            JSONArray itemArray = baseJsonResponse.getJSONArray("items");

            // If there are results in the features array
            for (int i = 0; i < itemArray.length(); i++) {
                // Extract out the first feature (which is an earthquake)
                JSONObject firstFeature = itemArray.getJSONObject(i);
                JSONObject volumeInfo = firstFeature.getJSONObject("volumeInfo");
                JSONObject saleInfo = firstFeature.getJSONObject("saleInfo");

                // Extract out the title, number of people, and perceived strength values
                String title = volumeInfo.getString("title");
                JSONArray authorArray = volumeInfo.optJSONArray("authors");
                String author1;
                Bitmap icon=null;
                if (authorArray!=null) author1 = authorArray.optString(0);
                else
                author1= "Unknown";
                String countryCode=saleInfo.optString("country");
                JSONObject retailPrice = saleInfo.optJSONObject("retailPrice");
                double doublePrice;
                String price;
                Boolean isEbook=saleInfo.optBoolean("isEbook");
                if(isEbook) {
                    doublePrice = ((Double) retailPrice.opt("amount"));
                    price = String.valueOf(doublePrice);
                }
                else
                    price="Not Available";
                String currencyCode=saleInfo.optString("currencyCode");
                String language=volumeInfo.optString("language");
                JSONObject imglinks = volumeInfo.optJSONObject("imageLinks");
                if (imglinks!=null) {
                    String thumbnail = imglinks.optString("thumbnail");
                     icon = getImageicon(thumbnail);
                }
                //JSONObject accessInfo = firstFeature.getJSONObject("accessInfo");
                String url = volumeInfo.optString("infoLink");
                Book book = new Book(title, author1, price,countryCode,language, icon, url,isEbook);
                books.add(book);
                // Create a new {@link Event} object
            }
//            while(books.size()<totalItems){
//                startIndex = books.size() + 1;
//                int difference = totalItems - books.size();
//                if (difference < 40) maxResults = difference;
//                else maxResults = 40;
//                book_request_url = "https://www.googleapis.com/books/v1/volumes?q=" + BookActivity.pattern.toString() + "&maxResults=" + maxResults + "&startIndex=" + startIndex;
//
//            }
            return books;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            return null;
        }

    }
    public static Bitmap getImageicon(String thumbnail) {
        if (thumbnail == null) {
            return null;
        }
        String urldisplay = thumbnail;
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }
    public static int getTotalItems(){
        return totalItems;

    }
}

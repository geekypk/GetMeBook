package com.excelerate.android.getmebook;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Book> books = Utils.fetchBookData(mUrl);
//        List<ImageBook> imageBooks = new ArrayList<>();;
//        String urldisplay = null;
//        Bitmap bmp = null;
        if(books!=null && !books.isEmpty()) {
//            BookAdapter mAdapter = new BookAdapter(getContext(), books);
//           mAdapter.notifyDataSetChanged();
            return books;
        }
        return null;
    }
}
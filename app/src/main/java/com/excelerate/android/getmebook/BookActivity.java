package com.excelerate.android.getmebook;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, RecyclerviewOnClickListener {

    public static final String LOG_TAG = BookActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    //public ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
//    public static Uri bookUri = null;
    public static Pattern pattern;
    public static Uri bookUri = null;
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * URL for earthquake data from the USGS dataset
     */
    private String book_request_url;
    private LinearLayout mInitialStateLinearLayout;
//    private ListView bookListView;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * Adapter for the list of earthquakes
     */
//    private BookAdapter mAdapter;
    private List<Book> books = new ArrayList<>();
    private LoaderManager loaderManager;
    private int maxResults;
    private int totalItems = Utils.getTotalItems();
    private int startIndex;
    private List<Book> myDataset = new ArrayList<>();

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
//        bookListView = findViewById(R.id.list);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            TextView mInitialStateTextView = findViewById(R.id.initialTextView);
            ImageView imgview = findViewById(R.id.initialImageView);
            imgview.setImageResource(R.drawable.ic_search_black);
            mInitialStateTextView.setText(R.string.start_serching);
            mInitialStateLinearLayout = findViewById(R.id.initialLayout);
            mRecyclerView.setVisibility(View.GONE);
            mInitialStateLinearLayout.setVisibility(View.VISIBLE);
            handleIntent(getIntent(), 40, 0);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            loadingIndicator.setVisibility(View.GONE);
//            mInitialStateLinearLayout.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView = findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
//        // Find a reference to the {@link ListView} in the layout
//        // Create a new adapter that takes an empty list of earthquakes as input
//         // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
        mAdapter = new BookAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent, 40, 0);
    }

    private void handleIntent(Intent intent, int hMaxResults, int hStartIndex) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            maxResults = hMaxResults;
            startIndex = hStartIndex;
            book_request_url = "https://www.googleapis.com/books/v1/volumes?q=" + pattern.toString() + "&maxResults=" + maxResults + "&startIndex=" + startIndex;
//            View loadingIndicator = findViewById(R.id.loading_indicator);
//            loadingIndicator.setVisibility(View.GONE);
            loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
//            books=getAllBooks(books);
            mRecyclerView = findViewById(R.id.my_recycler_view);
            mEmptyStateTextView = findViewById(R.id.empty_view);
            mEmptyStateTextView.setMaxLines(2);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.loading_indicator);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mEmptyStateTextView.setLayoutParams(params);
            mEmptyStateTextView.setText("Please wait while we fetch Books for You....");
            mInitialStateLinearLayout.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            //use the query to search your data somehow
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == BOOK_LOADER_ID) {

            //YourLoaderClass1 is you loaderClass where you implement onStartLoading and loadingInBackground()
            return new BookLoader(getApplicationContext(), book_request_url);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();// find which loader you called
        List<Book> books = new ArrayList<>();
        if (id == BOOK_LOADER_ID) books = (List<Book>) data;
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        mInitialStateLinearLayout.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."

        // Clear the adapter of previous earthquake data
        mRecyclerView.setAdapter(null);

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            myDataset.addAll(books);
            mRecyclerView = findViewById(R.id.my_recycler_view);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new BookAdapter(this, myDataset);
            mRecyclerView.setAdapter(mAdapter);

        } // eg. cast data to List<String>
        else {
            loadingIndicator = findViewById(R.id.loading_indicator);
            mInitialStateLinearLayout.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView = findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_books);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
// Loader reset, so we can clear out our existing data.
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void recyclerviewClick(int position) {
        Book currentBook = myDataset.get(position);
        bookUri = Uri.parse(currentBook.getUrl());

        // Create a new intent to view the earthquake URI
        Intent websiteIntent = new Intent(BookActivity.this, WebviewActivity.class);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);

    }
}
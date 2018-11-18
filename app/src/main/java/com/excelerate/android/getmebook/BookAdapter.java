package com.excelerate.android.getmebook;

import android.icu.util.Currency;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>{
    private final RecyclerviewOnClickListener listener;
    private List<Book> mDataset;
//    public Uri bookUri=null;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookAdapter(RecyclerviewOnClickListener listener, List<Book> myDataset) {
        this.listener=listener;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BookAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Book book = mDataset.get(position);
        holder.mImageView.setImageBitmap(book.getimgIcon());
        if(book.getimgIcon()==null){
            holder.mImageView.setImageResource(R.mipmap.ic_preview);
        }
        holder.mTitleTextView.setText(book.getTitle());
        String text="By: "+book.getAuthor1();
        holder.mAuthorTextView.setText(text);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Currency currency=Currency.getInstance(new Locale(book.getCountryLanguage(),book.getCountryCode()));
//        }
        if(!book.getIsEbook()){
            holder.mLinearLayout.setOrientation(LinearLayout.VERTICAL);
            holder.mCurrencyTextView.setText("Ebook ");
            holder.mPriceTextView.setText(book.getPrice());
        }
        else {
            holder.mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            String displayCurrency = NumberFormat.getCurrencyInstance(new Locale(book.getCountryLanguage(), book.getCountryCode())).getCurrency().getSymbol() + " ";
            holder.mCurrencyTextView.setText(displayCurrency);
            holder.mPriceTextView.setText(book.getPrice());
        }
//        if (book.getPrice().equals("")||book.getPrice()==null) {
//            holder.mPriceTextView.setText(R.string.unknown);
//        bookUri = Uri.parse(book.getUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.recyclerviewClick(holder.getAdapterPosition());
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTitleTextView;
        public TextView mAuthorTextView;
        public TextView mPriceTextView;
        public TextView mCurrencyTextView;
        public LinearLayout mLinearLayout;

        public MyViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.thumbnail);
            mTitleTextView = v.findViewById(R.id.title);
            mAuthorTextView = v.findViewById(R.id.author1);
            mCurrencyTextView=v.findViewById(R.id.currency);
            mPriceTextView = v.findViewById(R.id.price);
            mLinearLayout = v.findViewById(R.id.lLayout3);
        }
    }
}
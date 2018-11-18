package com.excelerate.android.getmebook;

import android.graphics.Bitmap;

public class Book {

    public final Boolean isEbook;
    public final String title;
    public final String author1;
    public final String price;
    public final String countryCode;
    public final String countryLanguage;
    public final Bitmap iconImage;
    public final String url;


    public Book(String bookTitle, String bookauthor1, String bookprice,String bookCountryCode,String bookCountryLanguage, Bitmap bookiconImage, String bookurl,Boolean bookIsEbook) {

        title = bookTitle;
        author1 = bookauthor1;
        price = bookprice;
        countryCode=bookCountryCode;
        countryLanguage=bookCountryLanguage;
        iconImage = bookiconImage;
        url = bookurl;
        isEbook = bookIsEbook;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor1() {
        return author1;
    }

    public String getPrice() {
        return price;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getCountryLanguage() {
        return countryLanguage;
    }

    public Bitmap getimgIcon() {
        return iconImage;
    }

    public String getUrl() {
        return url;
    }
    public  Boolean getIsEbook(){return isEbook;}
}


package com.LaVocedelBrunoFranchetti.rssreader;

import android.graphics.Bitmap;

/**
 * @author Emilio Dalla Torre.
 */

public class Model {

    private String title;
    private String date;
    private String creator;
    private String link;
    private Bitmap image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    }


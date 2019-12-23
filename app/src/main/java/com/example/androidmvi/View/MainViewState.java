package com.example.androidmvi.View;

public class MainViewState {
    boolean isLoading;
    boolean isImageViewShow;
    String imageLink;
    Throwable error;

    public MainViewState(boolean isLoading, boolean isImageViewShow, String imageLink, Throwable errpr) {
        this.isLoading = isLoading;
        this.isImageViewShow = isImageViewShow;
        this.imageLink = imageLink;
        this.error = errpr;
    }
}

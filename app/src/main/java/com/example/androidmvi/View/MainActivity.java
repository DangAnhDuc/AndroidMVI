package com.example.androidmvi.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidmvi.Model.MainView;
import com.example.androidmvi.R;
import com.example.androidmvi.Utils.DataSource;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;

public class MainActivity extends MviActivity<MainView,MainPresenter> implements  MainView {

    ImageView imageView;
    Button button;
    ProgressBar progressBar;

    List<String> imageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.btn_get_data);
        progressBar=findViewById(R.id.progress_bar);
        imageView= findViewById(R.id.recycler_data);

        imageList=createListImage();
    }

    private List<String> createListImage() {
        return Arrays.asList("https://i.pinimg.com/originals/7a/d1/e3/7ad1e3626c7dbf08e5c0d2c7dd744076.jpg",
                "https://www.mordeo.org/files/uploads/2018/03/Avengers-Infinity-War-2018-950x1689.jpg",
                "https://wallpapers.moviemania.io/phone/movie/299534/89d58e/avengers-endgame-phone-wallpaper.jpg?w=1536&h=2732",
                "https://i.pinimg.com/originals/26/80/70/2680702031e0fd44872b67a56616483e.jpg",
                "https://wallpaperplay.com/walls/full/a/0/7/119622.jpg",
                "http://getwallpapers.com/wallpaper/full/0/7/5/459085.jpg");    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(new DataSource(imageList));
    }

    @Override
    public Observable<Integer> getImageIntent() {
        return RxView.clicks(button).map(click -> getRandomNumberInRange(0,imageList.size()-1));
    }

    private Integer getRandomNumberInRange(int min, int max){
        if(min >=max)
            throw new IllegalArgumentException("Max must be greater than min");
        Random r= new Random();
        return r.nextInt((max-min)+1)+min;
    }

    @Override
    public void render(MainViewState viewState) {
        if(viewState.isLoading)
        {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            button.setEnabled(false);
        }
        else if(viewState.error !=null){
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            button.setEnabled(true);
            Toast.makeText(this,viewState.error.getMessage(),Toast.LENGTH_SHORT).show();
        }
        else if(viewState.isImageViewShow){
            button.setEnabled(true);

            Picasso.get().load(viewState.imageLink).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setAlpha(0f);
                    Picasso.get().load(viewState.imageLink).into(imageView);
                    imageView.animate().setDuration(300).alpha(1f).start();
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }
}

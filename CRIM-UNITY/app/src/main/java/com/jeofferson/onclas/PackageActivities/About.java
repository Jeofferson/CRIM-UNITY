package com.jeofferson.onclas.PackageActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.tv.TvContract;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jeofferson.onclas.PackageOthers.Logos;
import com.jeofferson.onclas.R;

public class About extends AppCompatActivity {


    private Toolbar toolbar;

    private ImageView banner;

    private Logos logos;
    private String bannerUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        logos = new Logos();
//        bannerUrl = logos.banner;
//
//        banner = findViewById(R.id.banner);
//
//        Glide.with(About.this).load(bannerUrl).into(banner);

        setUpToolbar();

    }


    public void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.about));

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }

        return true;

    }


}

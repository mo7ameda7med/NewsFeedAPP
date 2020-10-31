package com.example.newsfeedapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class NewsDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private ImageView  imageView;
    private TextView appbarTitle ,appbarSubtitle,date,time,title;
    private boolean isHideToolbarView =false;
    private FrameLayout dateBehavior;
    private LinearLayout titleAppbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl,mImg, mTitle,mDate,mSource,mAuthor;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detials);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        dateBehavior = findViewById(R.id.date_behavior);
        titleAppbar = findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appbarTitle=findViewById(R.id.title_on_appbar);
        appbarSubtitle = findViewById(R.id.subtitle_on_appbar);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);


        mUrl = getIntent().getStringExtra("url");
        mImg = getIntent().getStringExtra("img");
        mTitle = getIntent().getStringExtra("title");
        mDate = getIntent().getStringExtra("date");
        mSource = getIntent().getStringExtra("source");
        mAuthor = getIntent().getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utilities.getRandomDrawbleColor());

        Glide.with(this).load(mImg).apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade()).into(imageView);

        appbarTitle.setText(mSource);
        appbarSubtitle.setText(mUrl);
//        date.setText(Utilities.);
        title.setText(mTitle);
        String author=null;
        if (mAuthor != null || mAuthor != "")
        {
            mAuthor="\u2022" +mAuthor;
        }
        else
        {
            author= "";
        }
//        time.setText(mSource+author+"\u2022" Ut);

        initWebView(mUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url)
    {
        WebView webView=findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll =appBarLayout.getTotalScrollRange();
        float percentage;
        percentage = (float) Math.abs(i) / (float) maxScroll;

        if (percentage==1f && isHideToolbarView)
        {
            dateBehavior.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.VISIBLE);
            isHideToolbarView =! isHideToolbarView;
        }
        else
        if (percentage < 1f && isHideToolbarView)
        {
            dateBehavior.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isHideToolbarView =! isHideToolbarView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id==R.id.view_web)
        {
            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mUrl));
            startActivity(i);
            return true;
        }
         else if (id==R.id.share)
        {
            try {
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plan");
                i.putExtra(Intent.EXTRA_SUBJECT,mSource);
                String body=mTitle+"\n"+mUrl+"\n" +"Share from the news App" + "\n";
                    i.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(i,"Share With :"));
            }catch (Exception e)
            {
                Toast.makeText(this, "Hmm.. Sorry, \nCannot be share",
                        Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
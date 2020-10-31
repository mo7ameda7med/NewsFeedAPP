package com.example.newsfeedapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.newsfeedapp.api.ApiClient;
import com.example.newsfeedapp.api.ApiInterface;
import com.example.newsfeedapp.models.Article;
import com.example.newsfeedapp.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY = "9e5fdc1788584dd0a8afcecba4258863";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private TextView topHeadLine;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout=findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        topHeadLine=findViewById(R.id.topHeadLine);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");
    }

    public void loadJson(final String keyword) {

        topHeadLine.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utilities.getCountry();
        String language = Utilities.getLanguage();

        Call<News> call;
        if (keyword.length()>0)
        {
             call=apiInterface.getNewsSearch(keyword,language,"publishedAT",API_KEY);

        }else
        {
            call = apiInterface.getNews(country, API_KEY);
        }
        call = apiInterface.getNews(country, API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful() && response.body().getArticles() != null)
                {
                 if (!articles.isEmpty())
                 {
                     articles.clear();
                 }
                 articles=response.body().getArticles();
                 adapter=new Adapter(articles, MainActivity.this);
                 recyclerView.setAdapter(adapter);
                 adapter.notifyDataSetChanged();

                 initListener();

                 topHeadLine.setVisibility(View.VISIBLE);
                 swipeRefreshLayout.setRefreshing(false);

                }
                else
                {
                    topHeadLine.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No Result!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                topHeadLine.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initListener()
    {
        adapter.SetOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                ImageView imageView=view.findViewById(R.id.img);
                Intent intent =new Intent(MainActivity.this,NewsDetailsActivity.class);
                Article article = articles.get(position);
                intent.putExtra("url",article.getUrl());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("img",article.getUrlToImage());
                intent.putExtra("date",article.getPublishAt());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("author",article.getAuthor());

                Pair<View,String> Pair = androidx.core.util.Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(
                       MainActivity.this, Pair
                );
                startActivity(intent,optionsCompat.toBundle());
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        SearchManager searchManager =(SearchManager) getSystemService(SEARCH_SERVICE);
        final SearchView searchView= (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        MenuItem menuItem=menu.findItem(R.id.actionSearch);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>2)
                {
                    onLoadingSwipeRefresh(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.getIcon().setVisible(false,false);
        return true;
    }

    @Override
    public void onRefresh() {
        loadJson("");
    }
    private void onLoadingSwipeRefresh(final String keyword)
    {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadJson(keyword);
            }
        });
    }
}
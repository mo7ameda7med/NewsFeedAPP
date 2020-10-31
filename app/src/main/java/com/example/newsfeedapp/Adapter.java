package com.example.newsfeedapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.newsfeedapp.models.Article;

import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    private List<Article> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public Adapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view,onItemClickListener);
    }


    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final MyViewHolder myViewHolder =holder;
        Article  article =articles.get(position);

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(Utilities.getRandomDrawbleColor());
        requestOptions.error(Utilities.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.title.setText(article.getTitle());
        holder.decs.setText(article.getDescription());
        holder.source.setText(article.getSource().getName());
//        holder.time.setText("\u2022" +Utilities.DateToTimeFormat(article.getPublishAt()));
//        holder.published_ad.setText(Utilities.DateFormat(article.getPublishAt()));
        holder.author.setText(article.getAuthor());
        }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public void SetOnItemClickListener( OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener =onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(View view,int position);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,decs,author,published_ad,source,time;
        ImageView imageView;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;
        public MyViewHolder(@NonNull View itemView ,OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            title =itemView.findViewById(R.id.title);
            decs =itemView.findViewById(R.id.desc);
            author =itemView.findViewById(R.id.author);
            published_ad =itemView.findViewById(R.id.published_At);
            source =itemView.findViewById(R.id.source);
            time =itemView.findViewById(R.id.time);
            imageView =itemView.findViewById(R.id.img);
            progressBar =itemView.findViewById(R.id.Progress_load_photo);

            this.onItemClickListener =onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.OnItemClick(v,getAdapterPosition());
        }

    }
}

package com.mylovemhz.floordoorrecords.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.net.Api;
import com.mylovemhz.floordoorrecords.net.FdRss2Parser;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements Callback {

    public static final int TYPE_LOADING = 1;
    public static final int TYPE_NEWS_ITEM = 0;

    protected Context context;
    protected List<Article> articles;
    private boolean isLoading = false;
    private Callback callback;
    private int selectedPosition = 0;

    public NewsAdapter(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        articles = new ArrayList<>();
        refresh();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public ArrayList<Article> getArticles(){
        return (ArrayList<Article>) articles;
    }

    public void setArticles(ArrayList<Article> articles){
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void refresh(int page){
        PkRSS.Builder builder = new PkRSS.Builder(context);
        builder.parser(new FdRss2Parser());
        PkRSS pkRSS = builder.build();
        pkRSS.load(Api.FLOORDOOR_RSS_URL)
                .page(page)
                .callback(this)
                .async();
    }

    public void refresh(){
        refresh(1);
    }

    @Override
    public int getItemViewType(int position) {
        if(isLoading && position == 0){
            return TYPE_LOADING;
        }
        return TYPE_NEWS_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == TYPE_LOADING){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading,parent,false);
        }else if(viewType == TYPE_NEWS_ITEM){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news,parent,false);
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case TYPE_LOADING:
                bindLoadingItem(holder);
                break;
            case TYPE_NEWS_ITEM:
                bindNewsItem(holder, position);
                break;
        }

    }

    private void bindLoadingItem(ViewHolder holder) {
        holder.progressBar.setIndeterminate(true);
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    private void bindNewsItem(final ViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPosition == position);
        final Article article = articles.get(position);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(article.getDate());
        DateFormat sdf = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

        holder.dateText.setText(sdf.format(calendar.getTime()));
        holder.titleText.setText(Html.fromHtml(article.getTitle()));
        holder.summaryText.setText(Html.fromHtml(article.getDescription()));

        try{
            Picasso.with(context)
                    .load(article.getImage())
                    .transform(new BlurTransformation(context,15))
                    .transform(new BrightnessFilterTransformation(context, -0.3f))
                    .into(holder.featuredImage);
        }catch(IllegalArgumentException e){
            holder.featuredImage.setImageResource(R.drawable.ic_logo_gray);
        }

        holder.contextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContextOptions(article);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(holder.getLayoutPosition());
            }
        });
    }

    private void showContextOptions(final Article article){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setItems(R.array.context_options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                shareArticle(article);
                                break;
                            case 1:
                                openArticleInBrowser(article);
                                break;
                        }
                    }
                });
        alert.show();
    }

    public void selectItem(int position){
        int previousItem = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousItem);
        notifyItemChanged(position);
        if(callback != null) callback.onNewsItemClicked(articles.get(position));
    }

    private void openArticleInBrowser(Article article) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, article.getSource());
            context.startActivity(browserIntent);
        }catch(Exception e){
            Toast.makeText(context, R.string.error_browser, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareArticle(Article article) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }else{
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        share.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        share.putExtra(Intent.EXTRA_TEXT, article.getSource().toString());
        context.startActivity(Intent.createChooser(share, "Share article"));
    }

    @Override
    public int getItemCount() {
        return isLoading ? articles.size() + 1 : articles.size();
    }

    @Override
    public void onPreload() {
        isLoading = true;
        notifyDataSetChanged();
    }

    @Override
    public void onLoaded(List<Article> newArticles) {
        articles.addAll(newArticles);
        isLoading = false;
        notifyDataSetChanged();
    }

    @Override
    public void onLoadFailed() {
        isLoading = false;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //New Item
        TextView dateText;
        ImageButton contextButton;
        TextView titleText;
        TextView summaryText;
        ImageView featuredImage;

        //Loading
        ProgressBar progressBar;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if(viewType == TYPE_NEWS_ITEM) {
                dateText = (TextView) itemView.findViewById(R.id.dateText);
                contextButton = (ImageButton) itemView.findViewById(R.id.contextButton);
                titleText = (TextView) itemView.findViewById(R.id.titleText);
                summaryText = (TextView) itemView.findViewById(R.id.summaryText);
                featuredImage = (ImageView) itemView.findViewById(R.id.featuredImage);
            }else if(viewType == TYPE_LOADING){
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            }
        }
    }

    public interface Callback{
        void onNewsItemClicked(Article article);
    }
}

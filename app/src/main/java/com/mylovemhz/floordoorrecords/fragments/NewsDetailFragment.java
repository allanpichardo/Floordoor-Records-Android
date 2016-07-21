package com.mylovemhz.floordoorrecords.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mylovemhz.floordoorrecords.R;
import com.pkmmte.pkrss.Article;

public class NewsDetailFragment extends Fragment {

    public static final String STATE_ARTICLE = "article";

    private WebView webView;
    private Article article;

    public NewsDetailFragment(){}

    public static NewsDetailFragment newInstance(Article article) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArticle(article);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail,
                container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null) {
            webView = (WebView) getView().findViewById(R.id.webView);
            setupWebView();
        }
    }

    private void setupWebView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if(getActivity() != null) {
                    getActivity().setProgress(progress * 1000);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getActivity(), "Oh no! " + error.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_ARTICLE)){
            article = savedInstanceState.getParcelable(STATE_ARTICLE);
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(article != null){
            loadArticlePage();
        }
    }

    private void loadArticlePage() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<HTML><HEAD><LINK href=\"file:///android_asset/style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
            sb.append(article.getContent());
            sb.append("</body></HTML>");
            webView.loadDataWithBaseURL(
                    "http://www.floordoorrecords.com",
                    sb.toString(),
                    "text/html",
                    "utf-8",
                    null
            );
        }catch(NullPointerException e){
            Toast.makeText(getContext(), R.string.error_article, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ARTICLE,article);
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}

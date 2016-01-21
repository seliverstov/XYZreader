package com.example.xyzreader.ui;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * Created by a.g.seliverstov on 21.01.2016.
 */
public class ArticleBody extends Fragment{
    private String mText;
    public ArticleBody(String text){
        this.mText=text;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_body,container,false);
        TextView articleBody = (TextView)view.findViewById(R.id.article_body);
        articleBody.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));
        articleBody.setText(Html.fromHtml(mText));
        return view;
    }
}

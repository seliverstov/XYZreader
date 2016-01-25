package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.jar.Attributes;

/**
 * Created by a.g.seliverstov on 25.01.2016.
 */
public class ArticlePhotoImageView extends ImageView {
    public ArticlePhotoImageView(Context context){
        super(context);
    }

    public ArticlePhotoImageView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public ArticlePhotoImageView(Context context, AttributeSet attributeSet, int defStyle){
        super(context,attributeSet,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}

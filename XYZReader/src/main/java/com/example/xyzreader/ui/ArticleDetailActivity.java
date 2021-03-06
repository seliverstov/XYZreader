package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    private static final String STORED_SELECTED_ITEM = ArticleDetailActivity.class.getCanonicalName()+".STORED_SELECTED_ITEM";


    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;

    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private FloatingActionButton mShareFab;
    private DynamicHeightImageView mPhoto;
    private TextView mTitle;
    private TextView mByline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mCollapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);

        mPhoto = (DynamicHeightImageView)findViewById(R.id.article_photo);
        mTitle = (TextView)findViewById(R.id.article_title);
        mByline = (TextView)findViewById(R.id.article_byline);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                    onPageChanged(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mShareFab = (FloatingActionButton)findViewById(R.id.share_fab);

        getLoaderManager().initLoader(0, null, this).forceLoad();

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }else{
            mStartId = savedInstanceState.getLong(STORED_SELECTED_ITEM);
            mSelectedItemId = mStartId;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mViewPager.setCurrentItem(position, false);
                    onPageChanged(position);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private void onPageChanged(int page){
        Log.i(TAG, "onPageChanged " + page);
        if (mCursor!=null) mCursor.moveToPosition(page);
        mTitle.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        mByline.setText(
                String.format(
                        getString(R.string.byline),
                        DateUtils.getRelativeTimeSpanString(
                        mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString(),
                        mCursor.getString(ArticleLoader.Query.AUTHOR)
                ));
        Picasso.with(this).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).into(mPhoto);
        mCollapsingToolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.TransparentText);
        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText(mCursor.getString(ArticleLoader.Query.TITLE))
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            Bundle args = new Bundle();
            args.putString(ArticleBodyFragment.TEXT,mCursor.getString(ArticleLoader.Query.BODY));
            ArticleBodyFragment fragment = new ArticleBodyFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(STORED_SELECTED_ITEM,mSelectedItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long id = savedInstanceState.getLong(STORED_SELECTED_ITEM);
        if (id > 0) mSelectedItemId=id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                supportFinishAfterTransition();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

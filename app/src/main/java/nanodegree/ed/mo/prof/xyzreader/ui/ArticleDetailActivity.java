package nanodegree.ed.mo.prof.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.ed.mo.prof.xyzreader.R;
import nanodegree.ed.mo.prof.xyzreader.data.ArticleLoader;
import nanodegree.ed.mo.prof.xyzreader.data.ItemsContract;

import static nanodegree.ed.mo.prof.xyzreader.ui.ArticleListActivity.EXTRA_CURRENT_ARTICLE_POSITION;
import static nanodegree.ed.mo.prof.xyzreader.ui.ArticleListActivity.EXTRA_STARTING_ARTICLE_POSITION;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;
    @BindView(R.id.pager)
    ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private int mCurrentPosition;
    private int mStartingPosition;
    public final static String ARTICLE_ID = "ARTICLE_ID";
    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private ArticleDetailFragment articleDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);
        mStartingPosition=getIntent().getIntExtra(EXTRA_STARTING_ARTICLE_POSITION,0);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId= ItemsContract.Items.getItemId(getIntent().getData());
            }
            mCurrentPosition=mStartingPosition;
        }else {
            mStartId=savedInstanceState.getLong(ARTICLE_ID);
            mCurrentPosition=savedInstanceState.getInt(EXTRA_CURRENT_ARTICLE_POSITION);
        }

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mCurrentPosition);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    mCurrentPosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARTICLE_ID, mStartId);
        outState.putInt(EXTRA_CURRENT_ARTICLE_POSITION, mCurrentPosition);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
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
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
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

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            articleDetailFragment = (ArticleDetailFragment) object;
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID), position, mStartingPosition);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}

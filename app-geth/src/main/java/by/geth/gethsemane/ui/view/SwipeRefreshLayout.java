package by.geth.gethsemane.ui.view;

import android.content.Context;
import android.util.AttributeSet;

public class SwipeRefreshLayout extends androidx.swiperefreshlayout.widget.SwipeRefreshLayout {

    private boolean mMeasured = false;
    private boolean mPreMeasureRefreshing = false;

    public SwipeRefreshLayout(final Context context) {
        super(context);
    }

    public SwipeRefreshLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mMeasured) {
            mMeasured = true;
            setRefreshing(mPreMeasureRefreshing);
        }
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        if (mMeasured) {
            super.setRefreshing(refreshing);
        } else {
            mPreMeasureRefreshing = refreshing;
        }
    }
}

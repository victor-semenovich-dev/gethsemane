package by.geth.gethsemane.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import by.geth.gethsemane.R;

public class AspectRatioImageView extends ImageView {
    private int mAspectRatioWidth;
    private int mAspectRatioHeight;
    private boolean mIsAspectRatioWidthBased;
    private boolean mIsAspectRatioHeightBased;

    public AspectRatioImageView(Context context) {
        this(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
            mAspectRatioWidth = attributes.getInt(R.styleable.AspectRatioImageView_aspectRatioWidth, -1);
            mAspectRatioHeight = attributes.getInt(R.styleable.AspectRatioImageView_aspectRatioHeight, -1);
            mIsAspectRatioWidthBased = attributes.getBoolean(R.styleable.AspectRatioImageView_aspectRatioWidthBased, true);
            mIsAspectRatioHeightBased = attributes.getBoolean(R.styleable.AspectRatioImageView_aspectRatioHeightBased, false);
            attributes.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mAspectRatioWidth > 0 && mAspectRatioHeight > 0) {
            if (mIsAspectRatioWidthBased)
                height = width * mAspectRatioHeight / mAspectRatioWidth;
            else if (mIsAspectRatioHeightBased)
                width = height * mAspectRatioWidth / mAspectRatioHeight;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}

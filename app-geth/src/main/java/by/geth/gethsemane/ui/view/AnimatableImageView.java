package by.geth.gethsemane.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatableImageView extends ImageView {
    private AnimationDrawable mAnimationDrawable;
    private boolean mIsAnimationStarted = false;

    public AnimatableImageView(Context context) {
        this(context, null);
    }

    public AnimatableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAnimationDrawable = (AnimationDrawable) getDrawable();
        setImageDrawable(mAnimationDrawable.getFrame(0));
    }

    public synchronized void startAnimation() {
        if (!mIsAnimationStarted) {
            setImageDrawable(mAnimationDrawable);
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable.start();
        }
        mIsAnimationStarted = true;
    }

    public synchronized void stopAnimation() {
        if (mIsAnimationStarted) {
            mAnimationDrawable.stop();
            mAnimationDrawable.selectDrawable(0);
            setImageDrawable(mAnimationDrawable.getFrame(0));
        }
        mIsAnimationStarted = false;
    }
}

package by.geth.gethsemane.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import by.geth.gethsemane.R;

public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mStrokeWidth;

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.StrokeTextView_stroke, 0);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        setTextColor(Color.BLACK);
        paint.setStrokeWidth(mStrokeWidth);

        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);

        setTextColor(Color.WHITE);
        super.onDraw(canvas);
    }
}

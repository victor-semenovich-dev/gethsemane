package by.geth.gethsemane.ui.style;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mInnerSpace;
    private int mOuterSpace;

    public HorizontalSpaceItemDecoration(int innerSpace, int outerSpace) {
        mInnerSpace = innerSpace;
        mOuterSpace = outerSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();

        if (position == 0) {
            outRect.left = mOuterSpace;
            outRect.right = mInnerSpace;
        } else if (position == totalCount - 1) {
            outRect.right = mOuterSpace;
        } else {
            outRect.right = mInnerSpace;
        }
    }
}
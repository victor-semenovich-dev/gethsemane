package by.geth.gethsemane.ui.style;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import by.geth.gethsemane.util.Utils;

/**
 * Created by Victor Semenovich on 18.02.2018.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mOuterSpace;
    private int mInnerSpace;

    public SpacesItemDecoration(int outerSpace, int innerSpace) {
        mOuterSpace = Utils.dpToPx(outerSpace);
        mInnerSpace = Utils.dpToPx(innerSpace);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int position = params.getViewAdapterPosition();
        int total = state.getItemCount();

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int columnCount = layoutManager.getSpanCount();

        int row = position / columnCount;
        int column = position % columnCount;
        int rowCount = total / columnCount + ((total % columnCount == 0) ? 0 : 1);

        if (row == 0) {
            outRect.top = mOuterSpace;
        } else if (row == rowCount - 1) {
            outRect.bottom = mOuterSpace;
        }

        if (column == 0) {
            outRect.left = mOuterSpace;
        } else if (column == columnCount - 1) {
            outRect.right = mOuterSpace;
        }

        if (row > 0) {
            outRect.top = mInnerSpace / 2;
        }
        if (row < rowCount - 1) {
            outRect.bottom = mInnerSpace / 2;
        }
        if (column > 0) {
            outRect.left = mInnerSpace / 2;
        }
        if (column < columnCount - 1){
            outRect.right = mInnerSpace / 2;
        }
    }
}
package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.Category;

/**
 * Created by Victor Semenovich on 18.02.2018.
 */
public class GalleryCategoriesAdapter extends RecyclerView.Adapter<GalleryCategoriesAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private List<Category> mCategoryList;
    private OnCategoryClickListener mOnCategoryClickListener;

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = new ArrayList<>(categoryList);
        Collections.sort(mCategoryList);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        mOnCategoryClickListener = onCategoryClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_gallery_category, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Category category = mCategoryList.get(position);
        GlideApp.with(holder.coverView)
                .load(category.getCoverUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.coverView);
        holder.titleView.setText(category.getTitle());

        if (mOnCategoryClickListener != null) {
            holder.cardView.setEnabled(true);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCategoryClickListener.onCategoryClick(category);
                }
            });
        } else {
            holder.cardView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryList == null ? 0 : mCategoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView coverView;
        TextView titleView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            coverView = itemView.findViewById(R.id.cover);
            titleView = itemView.findViewById(R.id.title);
        }
    }
}

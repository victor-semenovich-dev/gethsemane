package by.geth.gethsemane.ui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class ScheduleView extends LinearLayout {
	BaseAdapter mAdapter = null;
	DataSetObserver mObserver = null;
	
	OnItemClickListener mItemClickListener;
	OnItemLongClickListener mItemLongClickListener;

	public ScheduleView(Context context) {
		this(context, null, 0);
	}

	public ScheduleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
	}

	public void setAdapter(BaseAdapter adapter) {
		if (mAdapter != null && mObserver != null) {
			mAdapter.unregisterDataSetObserver(mObserver);
			mObserver = null;
		}
		mAdapter = adapter;
		rebuild();
		if (mAdapter != null) {
			mAdapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					rebuild();
				}

				@Override
				public void onInvalidated() {
					setAdapter(null);
				}
			});
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		mItemLongClickListener = listener;
	}
	
	public void performItemClick(int pos) {
		if (mItemClickListener != null) {
			long id = mAdapter.getItemId(pos);
			mItemClickListener.onItemClick(pos, id);
		}
	}

	private void setViewAt(int i, View view) {
		if (i < getChildCount()) {
			View viewToReplace = getChildAt(i);
			if (viewToReplace != view) {
				addView(view, i);
				removeView(viewToReplace);
			}
		} else {
			addView(view);
		}
	}

	public void rebuild() {
		int i;
		int count = (mAdapter == null) ? 0 : mAdapter.getCount();

		for (i = 0; i < count; i++) {
			View recycle = i < getChildCount() ? getChildAt(i) : null;
			View view = mAdapter.getView(i, recycle, this);
			
			final int pos = i;
			if (mItemClickListener != null) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						long id = mAdapter.getItemId(pos);
						mItemClickListener.onItemClick(pos, id);
					}
				});
			}
			if (mItemLongClickListener != null) {
				view.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						long id = mAdapter.getItemId(pos);
						mItemLongClickListener.onItemLongClick(null, v, pos, id);
						return true;
					}
				});
			}
			if (i < getChildCount()) {
				setViewAt(i, view);
			} else {
				addView(view);
			}
		}
		for (; i < getChildCount(); i++) {
			removeViewAt(i);
		}

		requestLayout();
	}
	
	public interface OnItemClickListener {
        void onItemClick(int pos, long id);
	}
}

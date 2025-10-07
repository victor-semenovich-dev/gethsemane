package by.geth.gethsemane.ui.fragment.base;

import android.animation.AnimatorInflater;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import by.geth.gethsemane.R;
import by.geth.gethsemane.ui.activity.MainActivity;
import by.geth.gethsemane.util.Utils;

public class BaseFragment extends Fragment {
    public static final String ARGS_APPLY_UI_ARGUMENTS = "ARGS_APPLY_UI_ARGUMENTS";
    public static final String ARGS_AB_TITLE = "ARGS_AB_TITLE";
    public static final String ARGS_AB_TITLE_RES_ID = "ARGS_AB_TITLE_RES_ID";
    public static final String ARGS_AB_HAS_ELEVATION = "ARGS_AB_HAS_ELEVATION";
    public static final String ARGS_UI_FLAGS = "ARGS_UI_FLAGS";

    public static final int UI_FLAG_DISPLAY_HOME_AS_UP = 1;
    public static final int UI_FLAG_DRAWER_INDICATOR_ENABLED = 2;

    private MainActivity mActivity;
    private int mUIFlags;

    public BaseFragment() {
        setArguments(new Bundle());
    }

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (getActivity() instanceof MainActivity) {
            mActivity = (MainActivity) getActivity();
            boolean isApplyArguments = getArguments().getBoolean(ARGS_APPLY_UI_ARGUMENTS, true);
            if (isApplyArguments) {
                applyArguments();
            }

            ActionBar actionBar = mActivity.getSupportActionBar();
            if (actionBar != null) {
                if (isShowToolbar())
                    mActivity.getSupportActionBar().show();
                else
                    mActivity.getSupportActionBar().hide();
            }
        }
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.hideSoftKeyboard(requireActivity());
    }

    protected boolean isShowToolbar() {
        return true;
    }

    protected void setTitle(String title) {
        if (getActivity() != null) {
            if (getActivity() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setTitle(title);
                }
            } else if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(title);
            }
        }
    }

    protected void setTitle(int titleResId) {
        if (getActivity() != null) {
            if (getActivity() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setTitle(titleResId);
                }
            } else if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(titleResId);
            }
        }
    }

    protected boolean isUIFlagEnabled(int flag) {
        return (mUIFlags & flag) == flag;
    }

    protected void showFragment(Fragment fragment) {
        mActivity.showFragment(fragment);
    }

    protected void showFragment(Fragment fragment, boolean useAnimation) {
        mActivity.showFragment(fragment, useAnimation);
    }

    private void applyArguments() {
        Bundle args = getArguments();
        if (args != null) {
            setActivityTitle(args);
            setActionBarElevation(args);
            applyUIFlags(args);
        }
    }

    private void setActivityTitle(Bundle args) {
        if (args.containsKey(ARGS_AB_TITLE))
            setTitle(args.getString(ARGS_AB_TITLE));
        else if (args.containsKey(ARGS_AB_TITLE_RES_ID))
            setTitle(args.getInt(ARGS_AB_TITLE_RES_ID));
        else
            setTitle(R.string.app_title);
    }

    private void setActionBarElevation(Bundle args) {
        boolean hasElevation = args.getBoolean(ARGS_AB_HAS_ELEVATION, true);
        AppBarLayout appBarLayout = mActivity.findViewById(R.id.appBarLayout);
        if (appBarLayout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (hasElevation) {
                appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_on));
            } else {
                appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_off));
            }
        }
    }

    private void applyUIFlags(Bundle args) {
        mUIFlags = args.getInt(ARGS_UI_FLAGS);
        boolean isDisplayHomeAsUp = isUIFlagEnabled(UI_FLAG_DISPLAY_HOME_AS_UP);
        boolean isDrawerIndicatorEnabled = isUIFlagEnabled(UI_FLAG_DRAWER_INDICATOR_ENABLED);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        mActivity.setDrawerIndicatorEnabled(isDrawerIndicatorEnabled);
    }
}

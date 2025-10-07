package by.geth.gethsemane.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.activeandroid.query.Select;
import com.bluejamesbond.text.DocumentView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Author;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.download.DownloadController;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.fragment.base.AudioFragment;
import by.geth.gethsemane.ui.view.AnimatableImageView;

public class WitnessFragment extends AudioFragment implements View.OnClickListener {

    public static WitnessFragment newInstance(long id) {
        WitnessFragment fragment = new WitnessFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP);
        args.putLong(ARGS_WITNESS_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public static final String ARGS_WITNESS_ID = "ARGS_WITNESS_ID";

    private long mWitnessId;
    private Witness mWitness;
    private Author mAuthor;

    private DocumentView mContentView;

    private ViewGroup mMediaGroup;
    private AnimatableImageView mAudioButton;
    private View mDownloadButton;
    private ProgressBar mDownloadProgressBar;
    private View mDeleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mWitnessId = args.getLong(ARGS_WITNESS_ID);
            mWitness = new Select()
                    .from(Witness.class)
                    .where(Witness.COLUMN_ID + " = ?", mWitnessId)
                    .executeSingle();
            mAuthor = new Select()
                    .from(Author.class)
                    .where(Author.COLUMN_ID + " = ?", mWitness.getAuthorId())
                    .executeSingle();
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_witness, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContentView = view.findViewById(R.id.content);

        updateContent();

        updateTextSize();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        TextView titleView = view.findViewById(R.id.media_title);
        TextView subtitleView = view.findViewById(R.id.media_subtitle);
        titleView.setText(mWitness.getTitle());

        String dateStr = dateFormat.format(mWitness.getDate());
        if (mAuthor == null) {
            subtitleView.setText(dateStr);
        } else {
            subtitleView.setText(getString(R.string.fragment_sermon_list_subtitle_pattern, dateStr,
                    mAuthor.getName()));
        }

        mMediaGroup = view.findViewById(R.id.media_group);
        mAudioButton = view.findViewById(R.id.media_audio_play);
        mDownloadButton = view.findViewById(R.id.media_download_button);
        mDownloadProgressBar = view.findViewById(R.id.media_download_progress);
        mDeleteButton = view.findViewById(R.id.media_download_delete);

        mAudioButton.setOnClickListener(this);
        mDownloadButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getContext() == null) {
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());

        ApiService.getWitness(getContext(), mWitnessId);

        updateDownloadUI();
    }

    @Override
    public void onStop() {
        if (getContext() == null) {
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mApiServiceReceiver);

        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_witness, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_witness_menu_font_size:
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.fragment_witness_menu_font_size)
                            .setSingleChoiceItems(R.array.fragment_witness_menu_font_size_items,
                                    AppPreferences.getInstance().getFontSize(),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AppPreferences.getInstance().setFontSize(which);
                                            updateTextSize();
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.media_audio_play:
                playStop(mWitness);
                break;
            case R.id.media_download_button:
                download(mWitness);
                break;
            case R.id.media_download_delete:
                delete(mWitness);
                break;
        }
    }

    @Override
    protected void updateAudioUI() {
        if (isInProgress(mWitness)) {
            mMediaGroup.setVisibility(View.GONE);
        } else {
            mMediaGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void updateDownloadUI() {
        if (TextUtils.isEmpty(mWitness.getAudioLocalUri())) {
            if (DownloadController.getInstance().isDownloadInProgress(mWitness)) {
                mDownloadButton.setVisibility(View.GONE);
                mDownloadProgressBar.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.GONE);
            } else {
                mDownloadButton.setVisibility(View.VISIBLE);
                mDownloadProgressBar.setVisibility(View.GONE);
                mDeleteButton.setVisibility(View.GONE);
            }
        } else {
            mDownloadButton.setVisibility(View.GONE);
            mDownloadProgressBar.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateContent() {
        if (!TextUtils.isEmpty(mWitness.getContent()))
            mContentView.setText(Html.fromHtml(mWitness.getContent()));
    }

    private void updateTextSize() {
        mContentView.getDocumentLayoutParams().setTextSize(TypedValue.COMPLEX_UNIT_SP,
                14 + 2 * AppPreferences.getInstance().getFontSize());
        mContentView.getDocumentLayoutParams().setHyphenated(true);
        mContentView.setText(mContentView.getText());
    }

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (ApiService.ACTION_REQUEST_SUCCESSFUL.equals(action)
                    && ApiService.REQUEST_GET_WITNESS.equals(requestType)) {
                mWitness = new Select()
                        .from(Witness.class)
                        .where(Witness.COLUMN_ID + " = ?", mWitnessId)
                        .executeSingle();
                updateContent();
            }
        }
    };
}

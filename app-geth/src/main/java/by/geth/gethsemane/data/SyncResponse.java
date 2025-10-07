package by.geth.gethsemane.data;

import com.google.gson.annotations.SerializedName;

public class SyncResponse {
    @SerializedName("status")
    private boolean mIsSuccess;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("min_app_version")
    private int mMinAppVersion;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getMinAppVersion() {
        return mMinAppVersion;
    }

    @Override
    public String toString() {
        return "SyncResponse{" +
                "isSuccess=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", minAppVersion=" + getMinAppVersion() +
                '}';
    }
}

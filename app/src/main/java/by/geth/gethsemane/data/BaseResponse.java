package by.geth.gethsemane.data;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    public BaseResponse() {
    }

    public BaseResponse(boolean status, String message) {
        setStatus(status);
        setMessage(message);
    }

    @SerializedName("status")
    private boolean mStatus;

    @SerializedName("message")
    private String mMessage;

    public boolean getStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status=" + getStatus() +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}

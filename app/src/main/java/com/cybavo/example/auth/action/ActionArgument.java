package com.cybavo.example.auth.action;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.cybavo.auth.service.auth.TwoFactorAuthenticationAction;
import com.cybavo.example.auth.R;

public class ActionArgument implements Parcelable {

    private static final String TAG = ActionArgument.class.getSimpleName();

    public static ActionArgument fromAction(TwoFactorAuthenticationAction action) {
        return new ActionArgument(
                action.type, action.token, action.deviceId,
                action.messageType, action.messageTitle, action.messageBody, action.messageData,
                action.clientInfo.platform, action.clientInfo.ip,
                action.createTime,
                action.rejectable, action.inputPinCode, action.state,
                action.userAction, action.updatedTime);
    }

    public int type;
    public String token;
    public String deviceId;
    public int messageType;
    public String messageTitle;
    public String messageBody;
    public String messageData;
    public long createTime;
    public boolean rejectable;
    public boolean inputPinCode;
    public int state;
    public int userAction;
    public long updatedTime;
    public int clientPlatform;
    public String clientIp;

    private ActionArgument(int type, String token, String deviceId,
                           int messageType, String messageTitle, String messageBody, String messageData,
                           int clientPlatform, String clientIp, long createTime, boolean rejectable, boolean inputPinCode,
                           int state,
                           int userAction,
                           long updatedTime) {
        this.type = type;
        this.token = token;
        this.deviceId = deviceId;
        this.messageType = messageType;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.messageData = messageData;
        this.createTime = createTime;
        this.rejectable = rejectable;
        this.inputPinCode = inputPinCode;
        this.state = state;
        this.userAction = userAction;
        this.updatedTime = updatedTime;
        this.clientPlatform = clientPlatform;
        this.clientIp = clientIp;
    }

    protected ActionArgument(Parcel in) {
        type = in.readInt();
        token = in.readString();
        deviceId = in.readString();
        messageType = in.readInt();
        messageTitle = in.readString();
        messageBody = in.readString();
        messageData = in.readString();
        createTime = in.readLong();
        rejectable = in.readByte() != 0;
        inputPinCode = in.readByte() != 0;
        state = in.readInt();
        userAction = in.readInt();
        updatedTime = in.readLong();
        clientPlatform = in.readInt();
        clientIp = in.readString();
    }

    public String getTitle(Context context) {
        switch (type) {
            case TwoFactorAuthenticationAction.Types.SETUP_PIN_CODE:
                return context.getString(R.string.action_setup_pin_code);
            case TwoFactorAuthenticationAction.Types.CUSTOM_PIN_CODE_ACTION:
            case TwoFactorAuthenticationAction.Types.CUSTOM_OTP_ACTION:
                return messageTitle;
            default:
                return context.getString(R.string.action_unknown, type);
        }
    }

    public String getBody(Context context) {
        switch (type) {
            case TwoFactorAuthenticationAction.Types.SETUP_PIN_CODE:
                return context.getString(R.string.desc_setup_pin_code);
            case TwoFactorAuthenticationAction.Types.CUSTOM_PIN_CODE_ACTION:
            case TwoFactorAuthenticationAction.Types.CUSTOM_OTP_ACTION:
                return messageBody;
            default:
                return context.getString(R.string.desc_action_unknown, type);
        }
    }

    public String getStateDesc(Context context) {
        switch (state) {
            case TwoFactorAuthenticationAction.State.CREATED:
                return context.getString(R.string.label_action_state_created);
            case TwoFactorAuthenticationAction.State.DONE:
                return context.getString(R.string.label_action_state_done);
            case TwoFactorAuthenticationAction.State.PASSED:
                return context.getString(R.string.label_action_state_passed);
            case TwoFactorAuthenticationAction.State.CANCELLED:
                return context.getString(R.string.label_action_state_cancelled);
            case TwoFactorAuthenticationAction.State.FAILED:
                return context.getString(R.string.label_action_state_failed);
            default:
                return context.getString(R.string.label_action_state_unknown);
        }
    }

    public String getUserActionDesc(Context context) {
        switch (userAction) {
            case TwoFactorAuthenticationAction.UserAction.NONE:
                return context.getString(R.string.label_action_user_action_none);
            case TwoFactorAuthenticationAction.UserAction.ACCEPT:
                return context.getString(R.string.label_action_user_action_accept);
            case TwoFactorAuthenticationAction.UserAction.REJECT:
                return context.getString(R.string.label_action_user_action_reject);
            default:
                return context.getString(R.string.label_action_user_action_unknown);
        }
    }

    public static final Creator<ActionArgument> CREATOR = new Creator<ActionArgument>() {
        @Override
        public ActionArgument createFromParcel(Parcel in) {
            return new ActionArgument(in);
        }

        @Override
        public ActionArgument[] newArray(int size) {
            return new ActionArgument[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(token);
        dest.writeString(deviceId);
        dest.writeInt(messageType);
        dest.writeString(messageTitle);
        dest.writeString(messageBody);
        dest.writeString(messageData);
        dest.writeLong(createTime);
        dest.writeByte((byte) (rejectable ? 1 : 0));
        dest.writeByte((byte) (inputPinCode ? 1 : 0));
        dest.writeInt(state);
        dest.writeInt(userAction);
        dest.writeLong(updatedTime);
        dest.writeInt(clientPlatform);
        dest.writeString(clientIp);
    }
}

package com.cybavo.example.auth.action.detail;

import android.app.Application;
import android.util.Log;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.PinSecret;
import com.cybavo.auth.service.auth.results.TwoFactorAuthenticateResult;
import com.cybavo.example.auth.service.ServiceHolder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AuthActionViewModel extends AndroidViewModel {

    private static final String TAG = AuthActionViewModel.class.getSimpleName();

    public static class Factory implements ViewModelProvider.Factory {

        private final Application mApp;

        private final String mToken;
        private final String mDeviceId;
        public Factory(Application application, String token, String deviceId) {
            mApp = application;
            mToken = token;
            mDeviceId = deviceId;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AuthActionViewModel(mApp, mToken, mDeviceId);
        }

    }

    private final String mToken;
    private final String mDeviceId;

    private Authenticator mService;
    private MutableLiveData<Boolean> mLoading;
    private MutableLiveData<Throwable> mError;
    private MutableLiveData<Boolean> mSuccess;
    private MutableLiveData<Boolean> mAccept;

    public AuthActionViewModel(Application app, String token, String deviceId) {
        super(app);
        mToken = token;
        mDeviceId = deviceId;
        mService = ServiceHolder.get(app);

        mError = new MutableLiveData<>();
        mLoading = new MutableLiveData<>();
        mLoading.setValue(false);
        mSuccess = new MutableLiveData<>();
        mSuccess.setValue(false);

        mAccept = new MutableLiveData<>();
    }

    LiveData<Boolean> getAccept() {
        return mAccept;
    }

    void setAccept(boolean accept) {
        mAccept.setValue(accept);
    }

    LiveData<Throwable> getError() {
        return mError;
    }

    LiveData<Boolean> getLoading() {
        return mLoading;
    }

    LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    void approve(String message) {
        approve(null, message);
    }

    void approve(PinSecret pinSecret, String message) {
        mLoading.setValue(true);
        final Callback<TwoFactorAuthenticateResult> callback = new Callback<TwoFactorAuthenticateResult>() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "approve failed", error);
                mError.setValue(error);
                mLoading.setValue(false);
            }

            @Override
            public void onResult(TwoFactorAuthenticateResult TwoFactorAuthenticateResult) {
                mSuccess.setValue(true);
                mLoading.setValue(false);
            }
        };
        if (pinSecret != null) {
            mService.approve(mToken, mDeviceId, message, pinSecret, callback);
        } else {
            mService.approve(mToken, mDeviceId, message, callback);
        }
    }

    void reject(String message) {
        mLoading.setValue(true);
        mService.reject(mToken, mDeviceId, message, new Callback<TwoFactorAuthenticateResult>() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "reject failed", error);
                mError.setValue(error);
                mLoading.setValue(false);
            }

            @Override
            public void onResult(TwoFactorAuthenticateResult TwoFactorAuthenticateResult) {
                mSuccess.setValue(true);
                mLoading.setValue(false);
            }
        });
    }
}

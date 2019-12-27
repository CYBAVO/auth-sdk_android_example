package com.cybavo.example.auth;

import android.app.Application;
import android.util.Log;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.PairingStateListener;
import com.cybavo.auth.service.auth.TwoFactorAuthenticationAction;
import com.cybavo.auth.service.auth.results.GetTwoFactorAuthenticationsResult;
import com.cybavo.auth.service.auth.results.SetPushTokenResult;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel implements PairingStateListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private Authenticator mService;
    private MutableLiveData<Pairing[]> mPairings;
    private MutableLiveData<Throwable> mError;
    private MutableLiveData<Boolean> mLoading;

    private MutableLiveData<TwoFactorAuthenticationAction[]> mActions;
    private MutableLiveData<Long> mUpdatedTime;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mService = ServiceHolder.get(application);
        mUpdatedTime = new MutableLiveData<>();
        mUpdatedTime.setValue(0L);
        mError = new MutableLiveData<>();
        mLoading = new MutableLiveData<>();
        mLoading.setValue(false);
        initPush();
    }

    @Override
    public void onPairingStateChanged(Pairing[] pairings) {
        if (mPairings != null) {
            mPairings.setValue(pairings);
            if (pairings.length > 0) {
                fetch2FaActions();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mPairings != null) {
            mService.removePairingStateListener(this);
        }
    }

    public LiveData<Pairing[]> getPairings() {
        if (mPairings == null) {
            mPairings = new MutableLiveData<>();
            mPairings.setValue(mService.getPairings());
            mService.addPairingStateListener(this);
        }
        return mPairings;
    }

    public LiveData<TwoFactorAuthenticationAction[]> get2FaActions() {
        if (mActions == null) {
            mActions = new MutableLiveData<>();
            mActions.setValue(new TwoFactorAuthenticationAction[0]);
            fetch2FaActions();
        }
        return mActions;
    }

    public void refresh2FaActions() {
        if (mActions == null) {
            get2FaActions();
        } else {
            fetch2FaActions();
        }
    }

    public LiveData<Long> getActionsUpdatedTime() {
        return mUpdatedTime;
    }

    public LiveData<Throwable> getError() {
        return mError;
    }

    public void clearError() {
        mError.setValue(null);
    }

    public LiveData<Boolean> getLoading() {
        return mLoading;
    }

    private void fetch2FaActions() {
        if (mLoading.getValue() == true) {
            return;
        }

        if (mPairings.getValue().length == 0) {
            return;
        }

        final long since = (System.currentTimeMillis() / 1000L) - 60 * 60 * 24; // Only get last 24HRs

        mLoading.setValue(true);
        mService.getTwoFactorAuthentications(since, new Callback<GetTwoFactorAuthenticationsResult>() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "getTwoFactorAuthentications failed", error);
                mError.setValue(error);
                mLoading.setValue(false);
            }

            @Override
            public void onResult(GetTwoFactorAuthenticationsResult result) {
                mUpdatedTime.setValue(result.updatedTime);
                mActions.setValue(result.actions);
                mLoading.setValue(false);
            }
        });
    }

    private void initPush() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            final String token = task.getResult().getToken();
            Log.d(TAG, "FCM token: " + token);
            if (mService.getPairings().length > 0) {
                mService.setPushToken(token, new Callback<SetPushTokenResult>() {
                    @Override
                    public void onResult(SetPushTokenResult setPushTokenResult) {
                        // Good
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "setPushToken failed", error);
                    }
                });
            }
        });
    }
}

package com.cybavo.example.auth.pairing;

import android.app.Application;
import android.util.Log;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.results.UnpairResult;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ManagePairingViewModel extends AndroidViewModel {

    private final static String TAG = ManagePairingViewModel.class.getSimpleName();

    private Authenticator mService;
    private MutableLiveData<Pairing> mToUnpair;
    private MutableLiveData<Throwable> mError;
    private MutableLiveData<Boolean> mInProgress;

    public ManagePairingViewModel(@NonNull Application application) {
        super(application);
        mService = ServiceHolder.get(application);
        mError = new MutableLiveData<>();
        mInProgress = new MutableLiveData<>();
        mInProgress.setValue(false);
        mToUnpair = new MutableLiveData<>();
    }

    void setToUnpair(Pairing pairing) {
        mToUnpair.setValue(pairing);
    }

    LiveData<Pairing> getToUnpair() {
        return mToUnpair;
    }

    void unpair() {
        if (mToUnpair.getValue() == null) {
            return;
        }

        mInProgress.setValue(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            mService.unpair(new String[]{ mToUnpair.getValue().deviceId }, new Callback<UnpairResult>() {
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "unpair failed", error);
                    mError.setValue(error);
                    mInProgress.setValue(false);
                }

                @Override
                public void onResult(UnpairResult pairResult) {
                    mToUnpair.setValue(null);
                    mInProgress.setValue(false);
                }
            });
        });
    }

    LiveData<Throwable> getError() {
        return mError;
    }

    LiveData<Boolean> getInProgress() {
        return mInProgress;
    }
}

package com.cybavo.example.auth.pairing;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.results.PairResult;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NewPairingViewModel extends AndroidViewModel {

    private final static String TAG = NewPairingViewModel.class.getSimpleName();

    private Authenticator mService;
    private MutableLiveData<Boolean> mPaired;
    private MutableLiveData<Throwable> mError;
    private MutableLiveData<Boolean> mInProgress;

    public NewPairingViewModel(@NonNull Application application) {
        super(application);
        mService = ServiceHolder.get(application);
        mError = new MutableLiveData<>();
        mInProgress = new MutableLiveData<>();
        mInProgress.setValue(false);
    }

    void pair(String token) {
        mInProgress.setValue(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            String pushToken = "";
            try {
                pushToken = task.getResult().getToken();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "FirebaseInstanceId.getInstance() failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            mService.pair(token, pushToken, new HashMap<>(), new Callback<PairResult>() {
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "pair failed", error);
                    mError.setValue(error);
                    mInProgress.setValue(false);
                }

                @Override
                public void onResult(PairResult pairResult) {
                    mPaired.setValue(true);
                    mInProgress.setValue(false);
                }
            });
        });
    }

    LiveData<Boolean> getPaired() {
        if (mPaired == null) {
            mPaired = new MutableLiveData<>();
            mPaired.setValue(false);
        }
        return mPaired;
    }

    LiveData<Throwable> getError() {
        return mError;
    }

    LiveData<Boolean> getInProgress() {
        return mInProgress;
    }
}

package com.cybavo.example.auth.security;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SecurityCheckViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mDeviceSecure;
    private MutableLiveData<List<SecurityItem>> mSecurityItems;

    public SecurityCheckViewModel(@NonNull Application application) {
        super(application);
        mSecurityItems = new MutableLiveData<>();
        mSecurityItems.setValue(new ArrayList<>());
    }

    LiveData<Boolean> getDeviceSecure() {
        if (mDeviceSecure == null) {
            mDeviceSecure = new MutableLiveData<>();
            new FetchSecurityStateTask(mSecurityItems, mDeviceSecure).execute(getApplication().getApplicationContext());
        }
        return mDeviceSecure;
    }

    LiveData<List<SecurityItem>> getSecurityItems() {
        return mSecurityItems;
    }
}

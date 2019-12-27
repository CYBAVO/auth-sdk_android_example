package com.cybavo.example.auth.security;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.cybavo.example.auth.R;
import com.cybavo.security.mobile.DeviceSecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.lifecycle.MutableLiveData;

class FetchSecurityStateTask extends AsyncTask<Context, SecurityItem, Boolean> {

    private static final String TAG = FetchSecurityStateTask.class.getSimpleName();

    private MutableLiveData<List<SecurityItem>> mProgress;
    private MutableLiveData<Boolean> mResult;
    public FetchSecurityStateTask(MutableLiveData<List<SecurityItem>> progress, MutableLiveData<Boolean> result) {
        mProgress = progress;
        mResult = result;
    }

    @Override
    protected Boolean doInBackground(Context... args) {
        Context context = args[0];
        boolean secure = true;

        secure &= !runSecurityItem(R.id.security_jail_broken,
                () -> DeviceSecurity.isJailBroken(context), true);
        secure &= !runSecurityItem(R.id.security_hook,
                () -> DeviceSecurity.isHooked(context), true);
        secure &= !runSecurityItem(R.id.security_virtual_app,
                () -> DeviceSecurity.isVirtualApp(context), true);
        secure &= !runSecurityItem(R.id.security_emulator,
                () -> DeviceSecurity.isEmulator(context), true);

        // non fatal, don't count as inSecure
        runSecurityItem(R.id.security_mock_location,
                () -> DeviceSecurity.isMockLocationEnabled(context), false);
        runSecurityItem(R.id.security_ext_storage,
                () -> DeviceSecurity.isOnExternalStorage(context), false);
        runSecurityItem(R.id.security_dev_settings,
                () -> DeviceSecurity.isDevelopmentSettingsEnabled(context), false);
        runSecurityItem(R.id.security_debugger,
                () -> DeviceSecurity.isDebuggingEnabled(context), false);
        runSecurityItem(R.id.security_adb,
                () -> DeviceSecurity.isAdbEnabled(context), false);

        return secure;
    }

    @Override
    protected void onProgressUpdate(SecurityItem... progress) {
        List<SecurityItem> items = new ArrayList<>(mProgress.getValue());
        items.addAll(Arrays.asList(progress));
        mProgress.postValue(items);
    }

    @Override
    protected void onPostExecute(Boolean secure) {
        mResult.setValue(secure);
    }

    private final static long INTERVAL = 500;
    private boolean runSecurityItem(int id, Callable<Boolean> callable, boolean fatal) {
        long start = SystemClock.uptimeMillis();
        boolean detected = false;
        try {
            detected = callable.call();
            long sleep = INTERVAL - (SystemClock.uptimeMillis() - start);
            if (sleep > 0) {
                try { Thread.sleep(sleep); } catch (Exception e) { /* noop */ }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception occurred", e);
        }

        onProgressUpdate(new SecurityItem(id, detected ?
                (fatal ? SecurityItem.State.FATAL : SecurityItem.State.WARNING) : SecurityItem.State.PASS));

        return detected;
    }
}

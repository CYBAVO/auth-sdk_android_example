package com.cybavo.example.auth.pairing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.results.PairResult;
import com.cybavo.auth.service.view.PairListener;
import com.cybavo.auth.service.view.PairMode;
import com.cybavo.auth.service.view.ScanPairView;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.config.Config;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.app.Activity.RESULT_OK;
import static com.cybavo.auth.service.view.PairImagePicker.EXTRA_DEVICE_ID;
import static com.cybavo.auth.service.view.PairImagePicker.EXTRA_ERROR_MSG;
import static com.cybavo.auth.service.view.PairImagePicker.REQUEST_CODE_PICK_QR_IMAGE;

public class ScanPairDialog  extends DialogFragment implements PairListener{
    View progress;
    Authenticator authenticator;
    DialogInterface.OnClickListener onPickClickListener;
    public static ScanPairDialog newInstance(DialogInterface.OnClickListener onPickClickListener){
        ScanPairDialog dialog = new ScanPairDialog(onPickClickListener);
        return dialog;
    }
    public ScanPairDialog(DialogInterface.OnClickListener onPickClickListener){
        this.onPickClickListener = onPickClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle saveInstanceState){
        authenticator = ServiceHolder.get(getContext());
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_scan_pair, null, false);
        final ScanPairView scanPairView = view.findViewById(R.id.scanPairView);
        progress = view.findViewById(R.id.progress);

        scanPairView.setPairListener(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            try {
                scanPairView.setPushDeviceToken(task.getResult().getToken());
                scanPairView.setMode(PairMode.TOKEN);
            } catch (Exception e) {
                Toast.makeText(getContext(), "FirebaseInstanceId.getInstance() failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog)
                .setView(view)
//                .setTitle(R.string.desc_pair_scan)
                .setNeutralButton(R.string.pick, onPickClickListener)
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();


        return dialog;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            dismiss();
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_PICK_QR_IMAGE:
                String err = data.getStringExtra(EXTRA_ERROR_MSG);
                String deviceId = data.getStringExtra(EXTRA_DEVICE_ID);
                if(!TextUtils.isEmpty(deviceId)){
                    onPairSuccess(deviceId);
                    return;
                }
                if(!TextUtils.isEmpty(err)){
                    onPairError(new Exception(err));
                }else{
                    onPairError(new Exception("NA"));
                }
                break;
        }
    }

    public void onPairError(Throwable error) {
        progress.setVisibility(View.GONE);
        if (getParentFragment() instanceof OnPairListener) {
            ((OnPairListener) getParentFragment()).onPairError(error);
        }
        dismiss();
    }

    @Override
    public void onPairSuccess(PairResult pairResult) {
        onPairSuccess(pairResult.deviceId);
    }

    public void onPairSuccess(String deviceId) {
        progress.setVisibility(View.GONE);
        Pairing[] pairings = authenticator.getPairings();
        for (Pairing pairing : pairings) {
            if (TextUtils.equals(pairing.deviceId, deviceId)) {
//                mPaired.setValue(pairing);
                if (getParentFragment() instanceof OnPairListener) {
                    ((OnPairListener) getParentFragment()).onPairSuccess(pairing);
                }
                dismiss();
                break;
            }
        }
    }

    public void onPair() {
     progress.setVisibility(View.VISIBLE);
    }

    public String getEndpoint() {
        return Config.getEndpoint(getContext());
    }

    public String getApiCode() {
        return Config.getApiCode(getContext());
    }
}

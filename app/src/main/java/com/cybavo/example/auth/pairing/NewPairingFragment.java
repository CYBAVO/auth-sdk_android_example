package com.cybavo.example.auth.pairing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.api.DeviceInsecureError;
import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.results.PairResult;
import com.cybavo.auth.service.view.PairListener;
import com.cybavo.auth.service.view.PairImagePicker;
import com.cybavo.auth.service.view.PairMode;
import com.cybavo.auth.service.view.ScanPairView;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.common.InputDialog;
import com.cybavo.example.auth.config.Config;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import static android.app.Activity.RESULT_OK;
import static com.cybavo.auth.service.view.PairImagePicker.EXTRA_DEVICE_ID;
import static com.cybavo.auth.service.view.PairImagePicker.EXTRA_ERROR_MSG;
import static com.cybavo.auth.service.view.PairImagePicker.REQUEST_CODE_PICK_QR_IMAGE;

public class NewPairingFragment extends Fragment implements InputDialog.Listener, OnPairListener, DialogInterface.OnClickListener{

    private NewPairingViewModel mViewModel;
    private View mProgress;
    private Button mScan;
    private Button mInputToken;
//    private ScanPairView scanPairView;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_pairing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScan = view.findViewById(R.id.scan);
        mInputToken = view.findViewById(R.id.input_token);
        mProgress = view.findViewById(R.id.progress);

        mScan.setOnClickListener(v -> scanQrCode());
        mInputToken.setOnClickListener(v -> inputToken());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NewPairingViewModel.class);

        mViewModel.getPaired().observe(this, paired -> {
            if (paired != null) {
                goFinish(paired);
            }
        });

        mViewModel.getError().observe(this, error -> {
            if (error != null) {
                // show errors of device insecure
                if (error instanceof DeviceInsecureError) {
                    Snackbar.make(getView(),
                            error.getMessage() + ": " + Arrays.toString(((DeviceInsecureError) error).getErrors()),
                            Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        mViewModel.getInProgress().observe(this, inProgress -> {
            mProgress.setVisibility(inProgress ? View.VISIBLE : View.GONE);
            mScan.setEnabled(!inProgress);
            mInputToken.setEnabled(!inProgress);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_PICK_QR_IMAGE:
                String err = data.getStringExtra(EXTRA_ERROR_MSG);
                String deviceId = data.getStringExtra(EXTRA_DEVICE_ID);
                if(!TextUtils.isEmpty(deviceId)){
                    Pairing[] pairings = ServiceHolder.get(getContext()).getPairings();
                    for (Pairing pairing : pairings) {
                        if (TextUtils.equals(pairing.deviceId, deviceId)) {
                            goFinish(pairing);
                        }
                    }
                    return;
                }
                if(!TextUtils.isEmpty(err)){
                    onPairError(new Exception(err));
                }else{
                    onPairError(new Exception("NA"));
                }
                break;
        }
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            String token = result.getContents();
//            if (!TextUtils.isEmpty(token)) {
//                mViewModel.pair(token);
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                for(int grantResult: grantResults){
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                scanQrCode();
            break;
        }
    }
    private void scanQrCode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkAndRequestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            return;
        }

        ScanPairDialog dialog = ScanPairDialog.newInstance(this);
        dialog.show(getChildFragmentManager(), "scanPair");
//        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
//        integrator.initiateScan();
    }

    private void inputToken() {
        InputDialog.newInstance(
                getString(R.string.title_input_token),
                getString(R.string.hint_input_token)
        ).show(getChildFragmentManager(), "token");
    }

    private void goFinish(Pairing pairing) {
        Navigation.findNavController(getView()).
                navigate(NewPairingFragmentDirections.actionFinish(
                        pairing.serviceName, pairing.iconUrl
                ));
    }
    private boolean checkAndRequestPermissions(String[] permissions) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(int i = 0; i < permissions.length; i++){
            if(ContextCompat.checkSelfPermission(getActivity(),permissions[i]) != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(permissions[i]);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onInputMessage(String token) {
        if (!TextUtils.isEmpty(token)) {
            mViewModel.pair(token);
        }
    }

    @Override
    public void onPairSuccess(Pairing paired) {
        goFinish(paired);
    }

    @Override
    public void onPairError(Throwable error) {
        if (error != null) {
            // show errors of device insecure
            if (error instanceof DeviceInsecureError) {
                Snackbar.make(getView(),
                        error.getMessage() + ": " + Arrays.toString(((DeviceInsecureError) error).getErrors()),
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            String pushToken = "";
            try {
                pushToken = task.getResult().getToken();
                PairImagePicker.with(this)
                        .setEndpoint(Config.getEndpoint(getContext()))
                        .setApiCode(Config.getApiCode(getContext()))
                        .setMode(PairMode.TOKEN)
                        .setPushDeviceToken(pushToken).start();
            } catch (Exception e) {
                Toast.makeText(getContext(), "FirebaseInstanceId.getInstance() failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

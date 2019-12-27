package com.cybavo.example.auth.pairing;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cybavo.example.auth.R;
import com.cybavo.example.auth.common.InputDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

public class NewPairingFragment extends Fragment implements InputDialog.Listener {

    private NewPairingViewModel mViewModel;
    private View mProgress;
    private Button mScan;
    private Button mInputToken;

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
            if (paired) {
                goFinish();
            }
        });

        mViewModel.getError().observe(this, error -> {
            if (error != null) {
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String token = result.getContents();
            if (!TextUtils.isEmpty(token)) {
                mViewModel.pair(token);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void scanQrCode() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    private void inputToken() {
        InputDialog.newInstance(
                getString(R.string.title_input_token),
                getString(R.string.hint_input_token)
        ).show(getChildFragmentManager(), "token");
    }

    private void goFinish() {
        Navigation.findNavController(getView()).
                navigate(NewPairingFragmentDirections.actionFinish());
    }

    @Override
    public void onInputMessage(String token) {
        if (!TextUtils.isEmpty(token)) {
            mViewModel.pair(token);
        }
    }
}

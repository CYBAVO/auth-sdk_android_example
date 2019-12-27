package com.cybavo.example.auth.security;

import android.content.Intent;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cybavo.example.auth.R;
import com.cybavo.example.auth.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class SecurityCheckFragment extends Fragment {

    private final static int[][] SECURITY_ITEMS = {
            {R.id.security_jail_broken, R.string.label_security_jail_broken},
            {R.id.security_hook, R.string.label_security_hook},
            {R.id.security_virtual_app, R.string.label_security_virtual_app},
            {R.id.security_emulator, R.string.label_security_emulator},
            {R.id.security_mock_location, R.string.label_security_mock_location},
            {R.id.security_ext_storage, R.string.label_security_ext_storage},
            {R.id.security_dev_settings, R.string.label_security_dev_settings},
            {R.id.security_debugger, R.string.label_security_debugger},
            {R.id.security_adb, R.string.label_security_adb},
    };

    private LongSparseArray<SecurityItemView> mSecurityItemViews;
    private TextView mAlert;
    private Button mIgnore;

    private SecurityCheckViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_security_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlert = view.findViewById(R.id.insecure_alert);
        mIgnore = view.findViewById(R.id.ignore);

        // init security item views
        mSecurityItemViews = new LongSparseArray<>();
        for (int[] item : SECURITY_ITEMS) {
            final int id = item[0];
            final int titleRes = item[1];
            final SecurityItemView itemView = view.findViewById(id);
            itemView.setTitle(titleRes);
            mSecurityItemViews.append(id, itemView);
        }

        mIgnore.setOnClickListener(v -> goMain());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SecurityCheckViewModel.class);
        mViewModel.getSecurityItems().observe(this, items -> {
            for (SecurityItem item : items) {
                mSecurityItemViews.get(item.id).setState(item.state);
            }
        });

        mViewModel.getDeviceSecure().observe(this, isSecure -> {
            if (isSecure) {
                getView().postDelayed(() -> goMain(), 500);
            } else { // false
                mAlert.setVisibility(View.VISIBLE);
                mIgnore.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goMain() {
        startActivity(new Intent(getContext(), MainActivity.class).
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        getActivity().overridePendingTransition(0, R.anim.scale_up_exit);
    }
}

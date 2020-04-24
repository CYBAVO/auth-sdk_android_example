package com.cybavo.example.auth.pairing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cybavo.example.auth.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class FinishPairingFragment extends Fragment {

    private static final String ARG_SERVICE_NAME = "service_name";
    private static final String ARG_ICON_URL = "icon_url";

    private TextView mServiceName;
    private ImageView mIcon;
    private Button mDone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finish_pairing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mServiceName = view.findViewById(R.id.service_name);
        mIcon = view.findViewById(R.id.icon);
        mDone = view.findViewById(R.id.done);

        final String serviceName = getArguments().getString(ARG_SERVICE_NAME);
        final String iconUrl = getArguments().getString(ARG_ICON_URL);

        mServiceName.setText(serviceName);

        final Drawable fallback = ContextCompat.getDrawable(getContext(), R.drawable.fig_security);
        DrawableCompat.setTint(fallback, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        if (TextUtils.isEmpty(iconUrl)) {
            mIcon.setImageDrawable(fallback);
        } else {
            Glide.with(this)
                    .load(iconUrl).fallback(fallback)
                    .into(mIcon);
        }

        mDone.setOnClickListener(v -> quit());
    }

    private void quit() {
        Navigation.findNavController(getView()).popBackStack(R.id.fragment_actions, false);
    }
}

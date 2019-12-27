package com.cybavo.example.auth.pairing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cybavo.example.auth.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class FinishPairingFragment extends Fragment {

    private Button mDone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finish_pairing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDone = view.findViewById(R.id.done);
        mDone.setOnClickListener(v -> quit());
    }

    private void quit() {
        Navigation.findNavController(getView()).popBackStack(R.id.fragment_actions, false);
    }
}

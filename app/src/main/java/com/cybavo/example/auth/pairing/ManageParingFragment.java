package com.cybavo.example.auth.pairing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.example.auth.MainViewModel;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.common.ConfirmDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ManageParingFragment extends Fragment
        implements ConfirmDialog.Listener {

    MainViewModel mMainViewModel;
    ManagePairingViewModel mViewModel;

    RecyclerView mPairingList;
    PairingListAdapter mAdapter;
    TextView mEmptyMsg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_paring, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPairingList = view.findViewById(R.id.pairing_list);
        mPairingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PairingListAdapter(pairing -> mViewModel.setToUnpair(pairing));
        mPairingList.setAdapter(mAdapter);

        mEmptyMsg = view.findViewById(R.id.empty_msg);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(ManagePairingViewModel.class);

        mViewModel.getToUnpair().observe(this, toUnpair -> {
            if (toUnpair != null) {
                confirmUnpair(toUnpair);
            }
        });
        mViewModel.getInProgress().observe(this, inProgress -> {
            mAdapter.setDisabled(inProgress);
        });

        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mMainViewModel.getPairings().observe(this, pairings -> {
            mEmptyMsg.setVisibility(pairings.length == 0 ? View.VISIBLE : View.GONE);
            mPairingList.setVisibility(pairings.length > 0 ? View.VISIBLE : View.GONE);
            mAdapter.updatePairings(pairings);
        });
    }

    private void confirmUnpair(Pairing pairing) {
        ConfirmDialog.newInstance(
                getString(R.string.title_confirm_unpair),
                getString(R.string.message_confirm_unpair, pairing.deviceId)
        ).show(getChildFragmentManager(), "unpair");
    }

    @Override
    public void onConfirm() {
        mViewModel.unpair();
    }
}

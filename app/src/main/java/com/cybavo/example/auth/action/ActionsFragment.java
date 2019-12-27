package com.cybavo.example.auth.action;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.TwoFactorAuthenticationAction;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.MainViewModel;
import com.cybavo.example.auth.push.PushNotificationService;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ActionsFragment extends Fragment implements ActionListAdapter.ActionListListener {

    private MainViewModel mViewModel;

    private TextView mEmptyMsg;
    private ImageView mEmptyFig;
    private Button mPair;
    private RecyclerView mActionList;
    private ActionListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mEmptyMsg = view.findViewById(R.id.empty_msg);
        mEmptyFig = view.findViewById(R.id.empty_fig);
        mPair = view.findViewById(R.id.pair_now);
        mPair.setOnClickListener(v -> goPairing());

        mActionList = view.findViewById(R.id.action_list);
        mActionList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ActionListAdapter(this);
        mActionList.setAdapter(mAdapter);

        mSwipeRefresh = view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(() -> mViewModel.refresh2FaActions());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewModel.getPairings().observe(this, this::onPairingsUpdate);
        mViewModel.getLoading().observe(this, loading -> mPair.setEnabled(!loading));
        mViewModel.getError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                mViewModel.clearError();
            }
        });

        mViewModel.get2FaActions().observe(this, this::onActionsUpdate);
        mViewModel.getLoading().observe(this, loading -> mSwipeRefresh.setRefreshing(loading));

        observePushNotification();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pair:
                goPairing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onPairingsUpdate(Pairing[] pairings) {
        updateEmptyState(pairings.length == 0,
                mViewModel.get2FaActions().getValue().length == 0);
        mAdapter.updatePairings(pairings);
        mSwipeRefresh.setEnabled(pairings.length > 0);
        getActivity().invalidateOptionsMenu();
    }

    private void onActionsUpdate(TwoFactorAuthenticationAction[] actions) {
        mAdapter.updateActions(actions);
        updateEmptyState( mViewModel.getPairings().getValue().length == 0,
                actions.length == 0);
    }

    private void updateEmptyState(boolean noPairing, boolean noAction) {
        if (noPairing) {
            mEmptyMsg.setVisibility(View.VISIBLE);
            mEmptyMsg.setText(R.string.desc_empty_pairings);
            mEmptyFig.setVisibility(View.VISIBLE);
            mEmptyFig.setImageResource(R.drawable.fig_new_pairing);
            mPair.setVisibility(View.VISIBLE);
            mActionList.setVisibility(View.GONE);
        } else if (noAction) {
            mEmptyMsg.setVisibility(View.VISIBLE);
            mEmptyMsg.setText(R.string.desc_empty_action);
            mEmptyFig.setVisibility(View.VISIBLE);
            mEmptyFig.setImageResource(R.drawable.fig_empty_actions);
            mPair.setVisibility(View.GONE);
            mActionList.setVisibility(View.GONE);
        } else { // has action
            mEmptyMsg.setVisibility(View.GONE);
            mEmptyFig.setVisibility(View.GONE);
            mPair.setVisibility(View.GONE);
            mActionList.setVisibility(View.VISIBLE);
        }
    }

    private void goPairing() {
        Navigation.findNavController(getView()).
                navigate(ActionsFragmentDirections.actionPair());
    }

    private void observePushNotification() {
        PushNotificationService.getNewActions().observe(this, idTokenMap -> {
            if (idTokenMap != null) {
                // Simply refresh, check deviceId -> action-token map for further handling
                mViewModel.refresh2FaActions();
                // reset state
                PushNotificationService.clearNewActions();
            }
        });
    }

    @Override
    public void onClickAction(TwoFactorAuthenticationAction action) {
        if (action.state == TwoFactorAuthenticationAction.State.CREATED) {
            // not completed
            if (action.inputPinCode) {
                Navigation.findNavController(getView()).
                        navigate(ActionsFragmentDirections.actionPinCode(
                                ActionArgument.fromAction(action)
                        ));
            } else {
                Navigation.findNavController(getView()).
                        navigate(ActionsFragmentDirections.actionOtp(
                                ActionArgument.fromAction(action)
                        ));
            }
        } else {
            // completed
            Navigation.findNavController(getView()).
                    navigate(ActionsFragmentDirections.actionCompleted(
                            ActionArgument.fromAction(action)
                    ));
        }
    }
}

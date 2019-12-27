package com.cybavo.example.auth.action.detail;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybavo.example.auth.MainViewModel;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.action.ActionArgument;
import com.cybavo.example.auth.common.InputDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

public class OtpActionFragment extends Fragment implements InputDialog.Listener {

    private static final String ARG_ACTION = "action";

    private AuthActionViewModel mViewModel;
    private MainViewModel mMainViewModel;
    private ActionArgument mAction;

    private TextView mType;
    private ImageView mFigure;
    private TextView mBody;
    private TextView mData;
    private TextView mTime;
    private ContentLoadingProgressBar mProgress;
    private Button mAccept;
    private Button mReject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getParcelable(ARG_ACTION);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mType = view.findViewById(R.id.type);
        mBody = view.findViewById(R.id.body);
        mData = view.findViewById(R.id.data);
        mTime = view.findViewById(R.id.time);
        mAccept = view.findViewById(R.id.accept);
        mReject = view.findViewById(R.id.reject);
        mProgress = view.findViewById(R.id.progress);
        mFigure = view.findViewById(R.id.figure);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mAction.getTitle(getContext()));

        if (mAction.messageType != 0) {
            mType.setVisibility(View.VISIBLE);
            mType.setText(Integer.toString(mAction.messageType));
        } else {
            mType.setVisibility(View.GONE);
        }

        mBody.setText(mAction.getBody(getContext()));
        if (mAction.messageData != null) {
            mData.setText(mAction.messageData);
        } else {
            mData.setVisibility(View.GONE);
        }

        mTime.setText(
                DateFormat.format("yyyy-M-d HH:mm:ss", new Date(mAction.createTime * 1000L))
        );
        if (mAction.rejectable) {
            mAccept.setText(R.string.action_accept);
            mReject.setVisibility(View.VISIBLE);
        } else {
            mAccept.setText(R.string.action_submit);
            mReject.setVisibility(View.GONE);
        }

        mAccept.setOnClickListener(v -> mViewModel.setAccept(true));
        mReject.setOnClickListener(v -> mViewModel.setAccept(false));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, new AuthActionViewModel.Factory(
                getActivity().getApplication(), mAction.token, mAction.deviceId)).
                get(AuthActionViewModel.class);

        mViewModel.getLoading().observe(this, loading -> {
            mAccept.setEnabled(!loading);
            mReject.setEnabled(!loading);
            mProgress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        });
        mViewModel.getAccept().observe(this, accept -> {
            if (accept != null) {
                // show dialog to get user message
                InputDialog.newInstance(
                        getString(R.string.title_input_message),
                        getString(R.string.hint_input_message)
                ).show(getChildFragmentManager(), "message");
            }
        });
        mViewModel.getError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        mViewModel.getSuccess().observe(this, success -> {
            if (success) {
                mMainViewModel.refresh2FaActions();
                quit();
            }
        });

        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public void onInputMessage(String message) {
        if (mViewModel.getAccept().getValue()) { // approve
            mViewModel.approve(message);
        } else {
            mViewModel.reject(message);
        }
    }

    private void quit() {
        Navigation.findNavController(getView()).popBackStack(R.id.fragment_actions, false);
    }

}

package com.cybavo.example.auth.action.detail;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybavo.auth.service.auth.TwoFactorAuthenticationAction;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.action.ActionArgument;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

public class CompletedActionFragment extends Fragment {

    private static final String ARG_ACTION = "action";

    private ActionArgument mAction;

    private TextView mType;
    private ImageView mFigure;
    private TextView mBody;
    private TextView mData;
    private TextView mTime;
    private TextView mResult;
    private TextView mResultTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getParcelable(ARG_ACTION);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mType = view.findViewById(R.id.type);
        mBody = view.findViewById(R.id.body);
        mData = view.findViewById(R.id.data);
        mTime = view.findViewById(R.id.time);
        mResult = view.findViewById(R.id.result);
        mResultTime = view.findViewById(R.id.result_time);
        mFigure = view.findViewById(R.id.figure);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mAction.getTitle(getContext()));

        mFigure.setImageResource(mAction.inputPinCode ? R.drawable.fig_pin_code : R.drawable.fig_otp);

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

        mResult.setVisibility(View.VISIBLE);
        mResult.setText(String.format("%s/%s",
                mAction.getStateDesc(getContext()), mAction.getUserActionDesc(getContext())));
        mResultTime.setVisibility(View.VISIBLE);
        mResultTime.setText(
                DateFormat.format("yyyy-M-d HH:mm:ss", new Date(mAction.updatedTime * 1000L))
        );
        if (mAction.userAction == TwoFactorAuthenticationAction.UserAction.ACCEPT) {
            ImageViewCompat.setImageTintList(mFigure,
                    ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccept)));
            mResult.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccept));
        } else if (mAction.userAction == TwoFactorAuthenticationAction.UserAction.REJECT) {
            ImageViewCompat.setImageTintList(mFigure,
                    ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorReject)));
            mResult.setTextColor(ContextCompat.getColor(getContext(), R.color.colorReject));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

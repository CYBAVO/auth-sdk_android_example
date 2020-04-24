/**
 * Copyright (c) 2019 CYBAVO, Inc.
 * https://www.cybavo.com
 *
 * All rights reserved.
 */

package com.cybavo.example.auth.action;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.auth.service.auth.TwoFactorAuthenticationAction;
import com.cybavo.example.auth.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ViewHolder> {

    public interface ActionListListener {
        void onClickAction(TwoFactorAuthenticationAction action);
    }
    private final ActionListListener mListener;
    private final List<TwoFactorAuthenticationAction> mItems = new ArrayList<>();
    private Map<String, Pairing> mPairings = new HashMap<>();

    public ActionListAdapter(ActionListListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TwoFactorAuthenticationAction item = mItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateActions(TwoFactorAuthenticationAction[] actions) {
        mItems.clear();
        mItems.addAll(Arrays.asList(actions));
        notifyDataSetChanged();
    }

    public void updatePairings(Pairing[] pairings) {
        mPairings.clear();
        for (Pairing pairing : pairings) {
            mPairings.put(pairing.deviceId, pairing);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bullet;
        TextView title;
        TextView serviceName;
        TextView deviceId;
        TextView state;
        TextView time;
        TextView type;
        ImageView actionIcon;

        ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            serviceName = view.findViewById(R.id.service_name);
            deviceId = view.findViewById(R.id.deviceId);
            state = view.findViewById(R.id.state);
            time = view.findViewById(R.id.time);
            type = view.findViewById(R.id.type);
            bullet = view.findViewById(R.id.bullet);
            actionIcon = view.findViewById(R.id.action);
        }

        void bind(TwoFactorAuthenticationAction action) {

            Context context = itemView.getContext();

            Pairing pairing = mPairings.get(action.deviceId);
            serviceName.setText(pairing != null ? pairing.serviceName : "-");

            deviceId.setText(String.format("(%s)", action.deviceId));

            final ActionArgument arg = ActionArgument.fromAction(action);
            title.setText(arg.getTitle(context));

            if (arg.messageType != 0) {
                type.setVisibility(View.VISIBLE);
                type.setText(Integer.toString(arg.messageType));
            } else {
                type.setVisibility(View.GONE);
            }
            time.setText(DateFormat.format("M/d HH:mm", new Date(action.createTime * 1000L)));

            actionIcon.setImageResource(action.inputPinCode ? R.drawable.ic_pin_code : R.drawable.ic_otp);
            state.setText(String.format("%s/%s", arg.getStateDesc(context), arg.getUserActionDesc(context)));

            switch (action.userAction) {
                case TwoFactorAuthenticationAction.UserAction.NONE:
                    ImageViewCompat.setImageTintList(bullet,
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPending)));
                    break;
                case TwoFactorAuthenticationAction.UserAction.ACCEPT:
                    ImageViewCompat.setImageTintList(bullet,
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccept)));
                    break;
                case TwoFactorAuthenticationAction.UserAction.REJECT:
                    ImageViewCompat.setImageTintList(bullet,
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorReject)));
                    break;
                default:
                    ImageViewCompat.setImageTintList(bullet,
                            ColorStateList.valueOf(Color.BLACK));
                    break;
            }

            itemView.setAlpha(action.state == TwoFactorAuthenticationAction.State.CREATED ? 1 : .5f);

            // onClick event
            itemView.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onClickAction(action);
                }
            });
        }
    }
}

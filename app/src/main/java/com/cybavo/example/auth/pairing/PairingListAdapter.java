package com.cybavo.example.auth.pairing;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cybavo.auth.service.auth.Pairing;
import com.cybavo.example.auth.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PairingListAdapter extends RecyclerView.Adapter<PairingListAdapter.ViewHolder> {

    public interface PairingListListener {
        void onUnpair(Pairing pairing);
    }

    private final PairingListListener mListener;
    private final List<Pairing> mItems = new ArrayList<>();
    private boolean mDisabled = false;

    public PairingListAdapter(PairingListListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_pairing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Pairing item = mItems.get(position);
        holder.bind(item, mDisabled);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updatePairings(Pairing[] pairings) {
        mItems.clear();
        mItems.addAll(Arrays.asList(pairings));
        notifyDataSetChanged();
    }

    public void setDisabled(boolean disabled) {
        if (mDisabled == disabled) {
            return;
        }
        mDisabled = disabled;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView serviceName;
        TextView deviceId;
        TextView user;
        TextView pairedAt;
        Button unpair;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            serviceName = view.findViewById(R.id.service_name);
            user = view.findViewById(R.id.user);
            deviceId = view.findViewById(R.id.deviceId);
            pairedAt = view.findViewById(R.id.paired_at);
            unpair = view.findViewById(R.id.unpair);
        }

        void bind(Pairing pairing, boolean disabled) {
            String title = pairing.serviceName;
            if (!pairing.isValid) {
                title = "(Disconnected) " + title;
            }
            itemView.setAlpha(pairing.isValid ? 1f : 0.5f);
            serviceName.setText(title);
            user.setText(String.format("%s <%s>(%s)", pairing.userName, pairing.userEmail, pairing.userAccount));
            deviceId.setText(pairing.deviceId);
            pairedAt.setText(
                    pairedAt.getContext().getString(R.string.template_paired_at,
                            DateFormat.getDateFormat(itemView.getContext()).format(new Date(pairing.pairedAt * 1000L)))
            );
            if (TextUtils.isEmpty(pairing.iconUrl)) {
                icon.setImageResource(R.drawable.ic_pairing_service);
            } else {
                Glide.with(icon)
                        .load(pairing.iconUrl).fallback(R.drawable.ic_pairing_service)
                        .into(icon);
            }
            // onClick event
            unpair.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onUnpair(pairing);
                }
            });
            unpair.setEnabled(!disabled);
        }
    }
}

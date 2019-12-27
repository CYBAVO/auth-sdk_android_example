package com.cybavo.example.auth.pairing;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        TextView title;
        TextView pairedAt;
        Button unpair;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            pairedAt = view.findViewById(R.id.paired_at);
            unpair = view.findViewById(R.id.unpair);
        }

        void bind(Pairing pairing, boolean disabled) {
            title.setText(pairing.deviceId);
            pairedAt.setText(
                    pairedAt.getContext().getString(R.string.template_paired_at,
                            DateFormat.getDateFormat(itemView.getContext()).format(new Date(pairing.pairedAt * 1000L)))
            );
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

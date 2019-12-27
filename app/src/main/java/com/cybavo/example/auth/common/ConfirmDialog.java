package com.cybavo.example.auth.common;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.cybavo.example.auth.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    public interface Listener {
        void onConfirm();
    }

    public static ConfirmDialog newInstance(String title, String message) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    public ConfirmDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_unpair, (dlg, which) -> onConfirm())
                .create();
        return dialog;
    }

    private void onConfirm() {
        if (getParentFragment() instanceof ConfirmDialog.Listener) {
            ((ConfirmDialog.Listener) getParentFragment()).onConfirm();
            dismiss();
        }
    }
}

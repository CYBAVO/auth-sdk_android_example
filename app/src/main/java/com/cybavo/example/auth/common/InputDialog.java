package com.cybavo.example.auth.common;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cybavo.example.auth.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InputDialog extends DialogFragment {

    private final static String ARG_TITLE = "title";
    private final static String ARG_PLACEHOLDER = "placeholder";

    public interface Listener {
        void onInputMessage(String message);
    }

    public static InputDialog newInstance(String title, String placeholder) {
        InputDialog dialog = new InputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_PLACEHOLDER, placeholder);
        dialog.setArguments(args);
        return dialog;
    }

    public InputDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();

        final String title = getArguments().getString(ARG_TITLE);
        final String placeholder = getArguments().getString(ARG_PLACEHOLDER);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_input, null, false);
        EditText messageInput = view.findViewById(R.id.message_input);
        messageInput.setHint(placeholder);

        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> onMessageInput(messageInput.getText().toString()))
                .create();
    }

    private void onMessageInput(String message) {
        if (getParentFragment() instanceof Listener) {
            ((Listener) getParentFragment()).onInputMessage(message);
            dismiss();
        }
    }
}

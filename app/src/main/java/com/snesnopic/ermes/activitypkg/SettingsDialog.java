package com.snesnopic.ermes.activitypkg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.snesnopic.ermes.R;

public class SettingsDialog extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.settings_dialog, null));
        builder.setMessage(R.string.settings)
                .setPositiveButton(R.string.edit, (dialog, id) -> {
                    //TODO: applica modifiche, check del nome inserito e check della password
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //non succede nulla
                });
        return builder.create();
    }
}
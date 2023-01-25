package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.snesnopic.ermes.R;

public class CreateGroupDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.create_group_dialog, null));
        builder.setMessage(R.string.insert_group_name)
                .setPositiveButton(R.string.create, (dialog, id) -> {
                    //TODO: crea gruppo, check del nome inserito
                    EditText et = getDialog().findViewById(R.id.editTextNewGroupName);
                    String newGroupName = et.getText().toString();
                    if(connection.createGroup(newGroupName)) {
                        System.out.println("Gruppo creato");
                    }
                    else {
                        System.out.println("Gruppo non creato");

                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //non succede nulla
                });
        return builder.create();
    }
}
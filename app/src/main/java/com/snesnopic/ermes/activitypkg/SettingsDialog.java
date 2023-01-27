package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.thisUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.Connessione;

import java.io.File;

public class SettingsDialog extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dialog, null);
        builder.setView(view);
        EditText newUserNameEditText = view.findViewById(R.id.editTextNewUsername);
        EditText oldPasswordEditText = view.findViewById(R.id.editTextOldPassword);
        EditText newPasswordEditText = view.findViewById(R.id.editTextNewPassword);
        Switch showOldPasswordSwitch = view.findViewById(R.id.showOldPasswordSwitch);
        Switch showNewPasswordSwitch = view.findViewById(R.id.showNewPasswordSwitch);

        showOldPasswordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                oldPasswordEditText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            else
                oldPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        });
        //codice per mostrare o no la vecchia (sopra) e nuova (sotto) password
        showNewPasswordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                newPasswordEditText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            else
                newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        oldPasswordEditText.setText(thisUser.password);
        newUserNameEditText.setText(thisUser.username);
        builder.setMessage(R.string.settings)
                .setPositiveButton(R.string.edit, (dialog, id) -> {
                    //TODO: applica modifiche, check del nome inserito e check della password
                    if(!newUserNameEditText.getText().toString().equals(thisUser.username)) {
                        if(newUserNameEditText.getText().toString().length() > 5) {
                            if(connection.changeUsername(newUserNameEditText.getText().toString()))
                                Snackbar.make(view, "Il tuo nuovo username è: "+newUserNameEditText.getText().toString(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    if(newPasswordEditText.getText().toString().length() > 5) {
                        if(newPasswordEditText.getText().toString().equals(oldPasswordEditText.getText().toString())) {
                            return;
                        }
                        else if(connection.changeUserPassword(newPasswordEditText.getText().toString())) {
                            System.out.println("Password modificata in: "+newPasswordEditText.getText().toString());
                                //modifica il file di testo
                            Snackbar.make(view,"Password modificata!",Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //non succede nulla
                });
        return builder.create();
    }

    private void writeResources(boolean writeUsername, boolean writePassword) {

    }
}
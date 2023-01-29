package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.thisUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import java.io.IOException;
import java.io.OutputStreamWriter;

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
                    boolean changeusername = false;
                    boolean changepassword = false;
                    String newUsername = newUserNameEditText.getText().toString();
                    if(!newUsername.equals(thisUser.username) && newUsername.length() > 5) {
                        if(connection.changeUsername(newUsername))
                        {
                            changeusername = true;
                            Snackbar.make(view, "Il tuo nuovo username è: "+newUsername, Snackbar.LENGTH_LONG).show();
                        }
                    }
                    String newPassword = newPasswordEditText.getText().toString();
                    if(!newPassword.equals(thisUser.password) && newPassword.length() > 5) {
                        if(connection.changeUserPassword(newPassword))
                        {
                            changepassword = true;
                            Snackbar.make(view,"Password modificata!",Snackbar.LENGTH_LONG).show();
                        }

                    }
                    try {
                        OutputStreamWriter write = new OutputStreamWriter(getContext().openFileOutput("resources", Context.MODE_PRIVATE));     //apre il file resources om scrittura
                        if(changeusername)
                            write.write(newUsername);
                        else
                            write.write(thisUser.username);
                        write.write(10);
                        if(changepassword)
                            write.write(newPassword);
                        else
                            write.write(thisUser.password);
                        write.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //non succede nulla
                });
        return builder.create();
    }
}
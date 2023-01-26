package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.snesnopic.ermes.R;

public class CreateGroupDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_group_dialog, null);
        builder.setView(view);
        builder.setMessage(R.string.insert_group_name)
                .setPositiveButton(R.string.create, (dialog, id) -> {
                    //TODO: crea gruppo, check del nome inserito
                    EditText et = view.findViewById(R.id.editTextNewGroupName);
                    String newGroupName = et.getText().toString();
                    if(connection.createGroup(newGroupName)) {
                        System.out.println("Gruppo creato");
                        MyGroupsFragment.adapter.notifyDataSetChanged(); //aggiorna la lista di gruppi
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
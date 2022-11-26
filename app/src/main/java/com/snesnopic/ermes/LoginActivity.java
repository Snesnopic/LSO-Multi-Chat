package com.snesnopic.ermes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {
    Context context = this;
    Button loginButton;
    Button registerButton;
    String email;
    String password;
    File file;                                                 //file che contiene alcune informazioni dell'utente
    File path;                                                 //path dove viene creato il file
    FileInputStream fis;
    Connessione connection;
    CheckBox checkbox;
    boolean save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        connection = Connessione.getInstance("192.168.41.155", 8989);
        connection.start();
        if(!connection.isConnected()) {
            ImageView image = (ImageView) findViewById(R.id.logoImage);    //serve così può uscire la notifica in basso
            Snackbar.make(image, "ATTENZIONE! Connessione non stabilita!", Snackbar.LENGTH_LONG).show();
        }

        checkbox = (CheckBox) findViewById(R.id.keepMeSignedInBox);
        //pulsante Login
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            EditText em = findViewById(R.id.editTextTextEmailAddress);
            EditText pw = findViewById(R.id.editTextTextPassword);
            email = em.getText().toString();
            password = pw.getText().toString();
            if(email.isEmpty() || password.isEmpty())
            {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.fields_error)
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else
            {
                if(connection.isConnected()) {
                    if(connection.provaLogin(email, password,false))
                    {
                        if(checkbox.isChecked()) {
                            try {
                                FileInputStream fis = context.openFileInput("resources");
                                OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput("resources", Context.MODE_PRIVATE));
                                write.write(email+" || "+password);
                                write.close();

                            } catch (FileNotFoundException e) {
                                path = getFilesDir();
                                file = new File(path, "resources");
                                OutputStreamWriter write = null;
                                try {
                                    write = new OutputStreamWriter(context.openFileOutput("resources", Context.MODE_PRIVATE));
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                                try {
                                    write.write(email+" || "+password);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                try {
                                    write.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent mainIntent = new Intent(this,MainActivity.class);
                        startActivity(mainIntent);

                    }
                }
                else {
                    Snackbar.make(view, "Non è stato possibile stabilire la connessione. Riprovare.", Snackbar.LENGTH_LONG).show();
                }
            }

        });

        //pulsante Register
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            EditText em = findViewById(R.id.editTextTextEmailAddress);
            EditText pw = findViewById(R.id.editTextTextPassword);
            email = em.getText().toString();
            password = pw.getText().toString();
            if(email.isEmpty() || password.isEmpty())
            {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.fields_error)
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else
            {
                if(connection.provaLogin(email, password,true))
                {
                    //crea utente e poi fai il login
                    Intent mainIntent = new Intent(this,MainActivity.class);
                    startActivity(mainIntent);
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.user_exists_error)
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

        });

    }
    //verifica che l'utente che si vuole creare non esiste già
    private boolean AreCredentialsDontExist(String email) {
        return true;
    }
    //verifica che l'utente che vuole fare il login esista davvero
    private boolean AreCredentialsRight(String email, String password) { return true;}
}
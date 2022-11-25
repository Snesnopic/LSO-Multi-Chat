package com.snesnopic.ermes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        TextView testo = (TextView) findViewById(R.id.textTest);
        connection = Connessione.getInstance("192.168.41.155", 8989);
        connection.start();
        if(connection.isAlive()) {
            testo.setVisibility(View.VISIBLE);
            testo.setText("thread vivo");
        }

        //Crea un file all'apertura dell'app
        try {
            FileInputStream fis = context.openFileInput("resources");
            testo.setVisibility(View.VISIBLE);
            //testo.setText("file esistente");
        } catch (FileNotFoundException e) {
            testo.setVisibility(View.VISIBLE);
            //testo.setText("file inesistente");
            path = getFilesDir();
            file = new File(path, "resources");
        }


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
                if(AreCredentialsRight(email,password))
                {
                    Intent mainIntent = new Intent(this,MainActivity.class);
                    startActivity(mainIntent);
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
                if(AreCredentialsDontExist(email))
                {
                    //crea utente e poi fa il login
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
    private boolean AreCredentialsRight(String email, String password) {
        return true;
    }
}
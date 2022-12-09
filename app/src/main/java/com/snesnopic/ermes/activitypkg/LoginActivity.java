package com.snesnopic.ermes.activitypkg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.Context;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.Connessione;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    Context context = this;
    Button loginButton;
    Button registerButton;
    String username;
    String password;
    File path;                                                 //path dove viene creato il file
    File file;                                                 //file che contiene alcune informazioni dell'utente
    Connessione connection;
    CheckBox checkbox;

    protected void tryLogin(String username, String password, boolean isRegister) {

        if (username.isEmpty() || password.isEmpty())
            new AlertDialog.Builder(this).setMessage(R.string.fields_error).setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        else {
            if (connection.isConnected() && connection.login(username, password, isRegister))
            {
                //crea utente e poi fai il login
                //istanzia utente
                if (checkbox.isChecked())
                    writeResources(username, password);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else
                new AlertDialog.Builder(this).setMessage(R.string.login_failed).setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        path = getFilesDir();
        file = new File(path, "resources");

        try {
                connection = Connessione.getInstance("192.168.1.20", 8989);
                connection.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("[ERRORE in onCreate() || LoginActivity.java 72] Errore nella creazione del Thread connessione (probabilmente gia' esistente)");
        }


        //aspetta per tre volte (circa 5 secondi) la connessione, altrimenti va avanti. Aggiunto perche' c'era l'alto rischio che la main activity si avviasse prima di stabilire la connessione
        // e risultava sempre offline nonostante non lo fosse.

        for (int i = 0; i < 3 && !connection.isConnected(); i++) {
            try {
                //noinspection BusyWait
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!connection.isConnected()) {
            ImageView image = (ImageView) findViewById(R.id.logoImage);    //serve così può uscire la notifica in basso
            Snackbar.make(image, "ATTENZIONE! Connessione non stabilita!", Snackbar.LENGTH_LONG).show();
        }
        else {
            try {                   //tenta di leggere il file risorse
                BufferedReader br = new BufferedReader(new FileReader(file));
                username = br.readLine();
                password = br.readLine();
                br.close();
                tryLogin(username,password,false);
            }
             catch (IOException e) {
                System.out.println("File risorse non esistente");
             }
            initialize();
        }

    }

    private void initialize() {
        EditText em = findViewById(R.id.editTextUsername);
        EditText pw = findViewById(R.id.editTextPassword);
        checkbox = findViewById(R.id.keepMeSignedInBox);
        //pulsante Login
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> tryLogin(em.getText().toString(),pw.getText().toString(),false));

        //pulsante Register
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> tryLogin(em.getText().toString(),pw.getText().toString(),true));
    }

    private void writeResources(String username, String password) {
        try {
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput("resources", Context.MODE_PRIVATE));     //apre il file resources om scrittura
            write.write(password);
            write.write(10);
            write.write(username);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
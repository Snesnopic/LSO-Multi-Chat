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
import android.window.SplashScreen;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    Context context = this;
    Button loginButton;
    Button registerButton;
    String email;
    String password;
    File path;                                                 //path dove viene creato il file
    File file;                                                 //file che contiene alcune informazioni dell'utente
    FileInputStream fis;
    Connessione connection;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        path = getFilesDir();
        file = new File(path, "resources");

        connection = Connessione.getInstance("192.168.1.20", 8989);
        connection.start();


        //aspetta per tre volte (circa 5 secondi) la connessione, altrimenti va avanti. Aggiunto perche' c'era l'alto rischio che la main activity si avviasse prima di stabilire la connessione
        // e risultava sempre offline nonostante non lo fosse.

        for(int i = 0; i < 3 && !connection.isConnected();i++) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!connection.isConnected()) {
            ImageView image = (ImageView) findViewById(R.id.logoImage);    //serve così può uscire la notifica in basso
            Snackbar.make(image, "ATTENZIONE! Connessione non stabilita!", Snackbar.LENGTH_LONG).show();
        }
        else {
            try {                   //tenta di leggere il file risorse

                BufferedReader br = new BufferedReader(new FileReader(file));
                if(br.readLine().equals("1")) {
                    email = br.readLine();
                    password = br.readLine();
                    br.close();
                    Intent mainIntent = new Intent(this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else initialize();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initialize();
    }

    private void initialize() {
        checkbox = (CheckBox) findViewById(R.id.keepMeSignedInBox);
        //pulsante Login
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            EditText em = findViewById(R.id.editTextTextEmailAddress);
            EditText pw = findViewById(R.id.editTextTextPassword);
            email = em.getText().toString();
            password = pw.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.fields_error)
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                if(connection.isConnected()) {
                    if(connection.login(email, password,false)) {
                        if(checkbox.isChecked()) writeResources(email, password);
                        Intent mainIntent = new Intent(this,MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    }
                }
                else Snackbar.make(view, "Non è stato possibile stabilire la connessione. Riprovare.", Snackbar.LENGTH_LONG).show();
            }

        });

        //pulsante Register
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            EditText em = findViewById(R.id.editTextTextEmailAddress);
            EditText pw = findViewById(R.id.editTextTextPassword);
            email = em.getText().toString();
            password = pw.getText().toString();
            if(email.isEmpty() || password.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.fields_error)
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                if(connection.isConnected()) {
                    if(connection.login(email, password,true)) {  //aggiungere condizione che controlla se le credenziali non siano già state usate
                        //crea utente e poi fai il login
                        if(checkbox.isChecked()) writeResources(email, password);
                        Intent mainIntent = new Intent(this,MainActivity.class);
                        startActivity(mainIntent);
                    }
                    else {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.user_exists_error)
                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
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

    private void writeResources(String email, String password) {
        try {
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput("resources", Context.MODE_PRIVATE));     //apre il file resources om scrittura
            write.write("1");
            write.write(10);
            write.write(password);
            write.write(10);
            write.write(email);
            write.close();


        } catch (FileNotFoundException e) {
            OutputStreamWriter write = null;
            File writePath = getFilesDir();
            File writeFile = new File(writePath, "resources");

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
}
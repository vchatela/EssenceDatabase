package com.example.EssenseDatabase;

/**
 * Author :Valentin Chatelard
 * Email  :chatelar@etud.insa-toulouse.fr
 * Website: valentindu64.ddns.net
 **/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.*;

public class main extends Activity {

    /**
     * Called when the activity is first created.
     */
    AlertDialog.Builder   alert;
    double saveEuro = -1;
    double saveEuroLitre = -1;
    double saveKm = -1;
    boolean envoi;
    Button ValiderButton = null;
    Button btnRetour = null;
    EditText editTextKm = null;
    EditText editTextEuro = null;
    EditText editTextEuroLitre = null;
    EditText editTextResult = null;
    int retry = 0;

    AlertDialog.Builder builder;

    InputStream is=null;
    String result=null;
    String line=null;
    int code;

    // TODO : each user have to have a personnal essenceDatabase with his vehicule
    // TODO : page avec résultat
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // récupération des vues
        ValiderButton = (Button) findViewById(R.id.okButton);
        btnRetour = (Button) findViewById(R.id.buttonRetour);
        editTextEuro = (EditText) findViewById(R.id.editTextEuro);
        editTextEuroLitre = (EditText) findViewById(R.id.editTextEuroLitre);
        editTextResult = (EditText) findViewById(R.id.editTextResult);
        editTextKm = (EditText) findViewById(R.id.editTextKm);
        editTextResult.setEnabled(false);
        // on attribue un listener adapté aux vue
        ValiderButton.setOnClickListener(validerButton);
        // TODO : bar de progression

        btnRetour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), login.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});

        builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                envoi = true;
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                envoi = false;
                dialog.dismiss();
            }
        });



    }
    public void getYesNoWithExecutionStop(String title, String message) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };


        // make a text input dialog and show it
        alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                envoi = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                envoi = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        /*alert = builder.create();
        builder.setTitle("Attention !");
        builder.setMessage("Voulez vous renvoyer les mêmes valeurs ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Oui", new OkOnClickListener());
        builder.setNegativeButton("Non", new NotOkOnClickListener());
        alert.show();*/
        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

    }

    //TODO : gestion de la nouvelle activité : résultat de la database (moyenne etc)
    private OnClickListener validerButton = new OnClickListener() {
        @Override
        public void onClick(View v){
            editTextResult.setText("");
            // Ici on vérifie les valeurs avant d'envoyer à la database
            if (editTextEuro.getText().length() == 0 || editTextEuroLitre.getText().length() == 0 || editTextKm.getText().length() == 0){
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
                return;
            }
            envoi = true;
            retry ++;
            double valueEuro = Double.parseDouble(editTextEuro.getText().toString());
            double valueEuroLitre = Double.parseDouble(editTextEuroLitre.getText().toString());
            double valueKm = Double.parseDouble(editTextKm.getText().toString());
            // on vérifie les valeurs
            if (!(valueEuro < 0 || valueEuroLitre < 0 || valueKm < 0 || valueEuro > 90 || valueEuroLitre > 3 || valueKm > 1500)){
                // si c'est les memes valeurs on vérifie que l'utilisateur veut les renvoyers
                if (saveEuroLitre == valueEuroLitre && saveEuro == valueEuro && saveKm == valueKm){
                    // On demande si l'utilisateur veut les renvoyer


                    getYesNoWithExecutionStop("Attention !","Voulez vous renvoyer les mêmes valeurs ?");

                }
                // on envoi à la base de donnée
                if(envoi) {
                    editTextResult.setText("Connexion... (" + retry + ")");
                    try {
                        if (insert()) {
                            editTextResult.setText("Envoi effectué !");
                            retry = 0;
                            saveEuro = valueEuro;
                            saveEuroLitre = valueEuroLitre;
                            saveKm = valueKm;
                            editTextEuro.setText("");
                            editTextEuroLitre.setText("");
                            editTextKm.setText("");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    /*private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            envoi = true;
            alert.dismiss();
        }
    };
    private final class NotOkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            envoi = false;
            alert.dismiss();
        }
    }*/
    public boolean insert() throws InterruptedException {
        Date date = new Date();
        DateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateformat.format(date);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://88.142.52.11/android/insert.php?Prix="+editTextEuro.getText().toString()+
                            "&Distance="+editTextKm.getText().toString()+"&PrixLitre="+editTextEuroLitre.getText().toString()+"&Date="+dateString);
                   // httpclient.execute(httppost);
                    Log.e("pass 1", "connection success ");
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).start();
        return true;
    }

    @Override
    // méthode qui se lance lorsqu'on appuie sur le bouton meu du téléphone
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // TODO : gérer le menu :)
        // inflater.inflate(R.layout.menu, menu);
        return true;
    }
}

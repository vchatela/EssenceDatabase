package com.example.EssenseDatabase;

/**
 * Author :Valentin Chatelard
 * Email  :chatelar@etud.insa-toulouse.fr
 * Website: valentindu64.ddns.net
 **/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.*;

public class main extends Activity {

    /**
     * Called when the activity is first created.
     */

    Button ValiderButton = null;
    Button btnRetour = null;
    EditText editTextKm = null;
    EditText editTextEuro = null;
    EditText editTextEuroLitre = null;
    EditText editTextResult = null;
    int retry = 0;

    InputStream is=null;
    String result=null;
    String line=null;
    int code;

    // TODO : each user have to have a personnal essenceDatabase with its vehicule
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

    }
    //TODO : récupérer précédentes valeurs
    //TODO :  reset des valeurs après envois
    //TODO : nouvelle activité : résultat de la database (moyenne etc)
    private OnClickListener validerButton = new OnClickListener() {
        // TODO : vérifier changement des valeurs si reclique alors que valeur déjà dans la base de donnée
        @Override
        public void onClick(View v){
            // Ici on vérifie les valeurs avant d'envoyer à la database
            if (editTextEuro.getText().length() == 0 || editTextEuroLitre.getText().length() == 0 || editTextKm.getText().length() == 0){
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
                return;
            }
            retry ++;
            double valueEuro = Double.parseDouble(editTextEuro.getText().toString());
            double valueEuroLitre = Double.parseDouble(editTextEuroLitre.getText().toString());
            double valueKm = Double.parseDouble(editTextKm.getText().toString());
            // on vérifie les valeurs
            if (!(valueEuro < 0 || valueEuroLitre < 0 || valueKm < 0 || valueEuro > 90 || valueEuroLitre > 3 || valueKm > 1500)){
                // on envoi à la base de donnée
                editTextResult.setText("Connexion... ("+ retry +")");
                try {
                    if (insert()){
                        editTextResult.setText("Envoi effectué !");
                        retry = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    public boolean insert() throws InterruptedException {
        Date date = new Date();
        DateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateformat.format(date);
/*
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("Prix",editTextEuro.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("Distance",editTextKm.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("PrixLitre",editTextEuroLitre.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("Date",dateString));
*/      new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://88.142.52.11/android/insert.php?Prix="+editTextEuro.getText().toString()+
                            "&Distance="+editTextKm.getText().toString()+"&PrixLitre="+editTextEuroLitre.getText().toString()+"&Date="+dateString);
                    httpclient.execute(httppost);
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

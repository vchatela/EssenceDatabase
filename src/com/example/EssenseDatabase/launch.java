package com.example.EssenseDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static android.text.format.DateFormat.getBestDateTimePattern;

/**
 * Created by valentin on 24/06/15.
 */
public class launch extends Activity{

    public TextView textOk;
    public ProgressBar p;
    public TextClock t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t= (TextClock) findViewById(R.id.textClock);
        // TODO : format 24h
        setContentView(R.layout.launch);
        // On va vérifier le réseau puis login si ok
        new NetCheck().execute();
        // maintenant une fois le thread terminé on vérifie le résultat et on lance le bon
        //TODO : on lance la page suivante ...

    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args){
            boolean test = false;


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        test = true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return test;
        }
        @Override
        protected void onPostExecute(Boolean th){
            textOk = (TextView) findViewById(R.id.textOk);
            p = (ProgressBar) findViewById(R.id.progressBar);
            if(th == true){
                // on met le message vérification à OK
                p.setVisibility(View.INVISIBLE);
                textOk.setText("Connecté");
                textOk.setGravity(Gravity.CENTER);
            }
            else{
                // sinon a error Connection
                p.setVisibility(View.INVISIBLE);
                textOk.setText("Error in Network Connection");
                textOk.setGravity(Gravity.CENTER);
            }
        }
    }
    public void goLogin(View view){
        Intent myIntent = new Intent(view.getContext(), passwordReset.class);
        startActivityForResult(myIntent, 0);
        finish();
    }
}

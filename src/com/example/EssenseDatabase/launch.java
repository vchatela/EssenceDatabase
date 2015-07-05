package com.example.EssenseDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.text.format.DateFormat.getBestDateTimePattern;

/**
 * Created by valentin on 24/06/15.
 */

// TODO : add retry button to restart testing connexion
public class launch extends Activity{
    public int semaphore=1;
    public boolean thread= false;
    public TextView textOk;
    public ProgressBar p;
    public TextClock t;
    public AsyncTask<String,String,Boolean> sync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t= (TextClock) findViewById(R.id.textClock);
        // TODO : format 24h
        setContentView(R.layout.launch);
        // On va vérifier le réseau puis login si ok
        sync = new NetCheck().execute();

        // maintenant une fois le thread terminé on vérifie le résultat et on lance le bon

         try {
             sync.get(2000, TimeUnit.MILLISECONDS);
         }
        catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (TimeoutException e) {
             e.printStackTrace();
         }
        Log.d("DEBUG", sync.getStatus().toString());

        if (thread) {
            p = (ProgressBar) findViewById(R.id.progressBar);
            p.setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        Intent i = new Intent(getApplicationContext(), login.class);
                        startActivity(i);
                        //launch.this.finish();
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            // TODO : on propose à l'utilisateur d'allumer si éteins (uniquement si éteins)
        }
    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            semaphore =0;
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
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            semaphore=1;
            if (test)
                thread = true;
            else
                thread = false;
            return test;
        }
        @Override
        protected void onPostExecute(Boolean th){
            textOk = (TextView) findViewById(R.id.textOk);
            if(th == true){
                // on met le message vérification à OK
                textOk.setText("Connecté");
                textOk.setGravity(Gravity.CENTER);
                p = (ProgressBar) findViewById(R.id.progressBar);
                p.setVisibility(View.INVISIBLE);
            }
            else{
                // sinon a error Connection
                p = (ProgressBar) findViewById(R.id.progressBar);
                p.setVisibility(View.INVISIBLE);
                textOk.setText("Error in Network Connection");
                textOk.setGravity(Gravity.CENTER);
            }
        }
    }
}

package net.valentinc.EssenceDatabase;

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
import android.widget.Button;
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
    public Button but;
//TODO : error network check it
    public void testNetwork(){
        // On va vérifier le réseau puis login si ok
        sync = new NetCheck().execute();

        try {
            sync.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG", sync.getStatus().toString());
        thread = true;
    }

    public void activity_next(){
        if (thread) {
            but.setVisibility(View.INVISIBLE);
            but.setEnabled(false);

            p = (ProgressBar) findViewById(R.id.progressBar);
            p.setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        Intent i = new Intent(getApplicationContext(), AddInfo.class);
                        startActivity(i);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            but.setVisibility(View.VISIBLE);
            but.setEnabled(true);
            // TODO : on propose à l'utilisateur d'allumer si éteins (uniquement si éteins)
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        t= (TextClock) findViewById(R.id.textClock);
        but = (Button) findViewById(R.id.but);



        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNetwork();
                activity_next();
            }
        });


        but.setVisibility(View.INVISIBLE);
        but.setEnabled(false);

        // TODO : format 24h

        // on test le réseau
        testNetwork();
        activity_next();
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
            thread = test;
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
                //TODO : but as button
                textOk.setGravity(Gravity.CENTER);
            }
        }
    }
}

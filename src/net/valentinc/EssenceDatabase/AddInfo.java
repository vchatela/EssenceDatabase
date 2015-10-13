package net.valentinc.EssenceDatabase;

/**
 * Author :Valentin Chatelard
 * Email  :chatelar@etud.insa-toulouse.fr
 * Website: valentindu64.ddns.net
 **/

import android.app.Activity;
import android.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.OnClickListener;

public class AddInfo extends Activity {

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
    Button btnResultat = null;
    Button btnManage = null;
    EditText editTextKm = null;
    EditText editTextEuro = null;
    EditText editTextEuroLitre = null;
    int retry = 0;

    private double valueEuro;
    private double valueEuroLitre;
    private double valueKm;

    // TODO : each user have to have a personnal essenceDatabase with his vehicule
    // TODO : page pour supprimer des valeurs  - créer liste radio avec les valeurs / dates, et mettre en place la suppression ! (via JSON par la date)
    private OnClickListener validerButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Ici on vérifie les valeurs avant d'envoyer à la database
            if (editTextEuro.getText().length() == 0 || editTextEuroLitre.getText().length() == 0 || editTextKm.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
                return;
            }
            envoi = true;
            retry++;
            valueEuro = Double.parseDouble(editTextEuro.getText().toString());
            valueEuroLitre = Double.parseDouble(editTextEuroLitre.getText().toString());
            valueKm = Double.parseDouble(editTextKm.getText().toString());
            // on vérifie les valeurs
            if (!(valueEuro < 0 || valueEuroLitre < 0 || valueKm < 0 || valueEuro > 90 || valueEuroLitre > 3 || valueKm > 1500)) {
                // si c'est les memes valeurs on vérifie que l'utilisateur veut les renvoyers
                if (saveEuroLitre == valueEuroLitre && saveEuro == valueEuro && saveKm == valueKm) {
                    // On demande si l'utilisateur veut les renvoyer
                    getYesNoWithExecutionStop("Attention !", "Voulez vous renvoyer les mêmes valeurs ?");
                }
                // on envoi à la base de donnée
                if(retry != 1)
                    Toast.makeText(getApplicationContext(), "Connexion... (" + retry + ")",
                        Toast.LENGTH_LONG).show();
                if (envoi) {
                    try {
                        if (insert()) {
                            Toast.makeText(getApplicationContext(), "Envoi effectué !",
                                    Toast.LENGTH_LONG).show();
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
            } else {
                Toast.makeText(getApplicationContext(), "Valeurs non correctes",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // récupération des vues
        ValiderButton = (Button) findViewById(R.id.okButton);
        btnRetour = (Button) findViewById(R.id.buttonRetour);
        btnResultat = (Button) findViewById(R.id.buttonResultats);
        btnManage = (Button) findViewById(R.id.buttonManage);

        editTextEuro = (EditText) findViewById(R.id.editTextEuro);
        editTextEuroLitre = (EditText) findViewById(R.id.editTextEuroLitre);
        editTextKm = (EditText) findViewById(R.id.editTextKm);
        // on attribue un listener adapté aux vue
        ValiderButton.setOnClickListener(validerButton);

        btnResultat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), result.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });

        btnManage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), Manage.class);
                startActivityForResult(myIntent, 0);
                finish();
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
                retry = 0;
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

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

    }

    public boolean insert() throws InterruptedException {
        Date date = new Date();
        DateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateformat.format(date);
        final boolean[] issue = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    HttpURLConnection conn;
                    URL url = new URL("http://88.142.52.11/android/insert.php?Prix="+valueEuro+
                            "&Distance="+valueKm+"&PrixLitre="+valueEuroLitre+"&Date="+dateString);

                     conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    String code = "{\"code\":0}";
                    if(total.toString().equals(code)){
                        issue[0] = true;
                    }
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());
                    issue[0] = true;
                }
            }
        });
        t.start();
        t.join();
        if(issue[0]){
            Toast.makeText(getApplicationContext(), "MySQL error. Please resend it.",
                    Toast.LENGTH_LONG).show();
        }
        return !issue[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

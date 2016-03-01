package net.valentinc.EssenceDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by valentin on 25/06/15.
 *
 */
public class result extends Activity {
    Button btnRafraichir = null;
    Button btnRetour = null;

    TextView TextKmTotal = null;
    TextView TextEssenceTotal = null;
    TextView TextPrixTotal = null;

    TextView TextPrixCent = null;
    TextView TextPrixKm = null;
    TextView TextPrixCovoit = null;

    TextView TextViewInfo = null;
    ArrayList<String> donnees;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        // on récupère les edittext et les boutons
        btnRafraichir = (Button) findViewById(R.id.buttonRafraichir);
        btnRetour  = (Button) findViewById(R.id.buttonRetour);

        TextKmTotal = (TextView) findViewById(R.id.textViewKmTotal);
        TextEssenceTotal = (TextView) findViewById(R.id.EssenceTotal);
        TextPrixTotal = (TextView) findViewById(R.id.textViewPrixTotal);

        TextPrixCent = (TextView) findViewById(R.id.textViewL100);
        TextPrixKm = (TextView) findViewById(R.id.textViewPrixKm);

        TextPrixCovoit = (TextView) findViewById(R.id.PrixCovoit);

        TextViewInfo = (TextView) findViewById(R.id.TextViewInfo);

        // On s'occupe des clics sur Rafraichir et Retour
        btnRetour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddInfo.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});
        btnRafraichir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // c'est ici qu'on met a jours les valeurs de la DB
                try {
                    update_db();
                    update_view();
                } catch (Exception e) {
                    Log.i("debug :", e.toString());
                }
            }
        });
    }

    public void update_db() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                String result = new String();
                donnees = new ArrayList<>();

                try {
                    URL url = new URL("http://88.142.52.11/android/getdata.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    is = conn.getInputStream();


                    //conversion de la réponse en chaine de caractère
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        StringBuilder sb = new StringBuilder();

                        result = reader.readLine();

                        is.close();

                    } catch (Exception e) {
                        Log.i("tagconvertstr", "" + e.toString());
                    }

                    //recuperation des donnees json
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("kmt").toString())));
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("lt").toString())));
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("pt").toString())));
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("pkm").toString())));
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("lcent").toString())));
                        donnees.add(NumberFormat.getNumberInstance(Locale.FRANCE).format(Float.parseFloat(jsonObj.get("pc").toString())));
                        donnees.add(jsonObj.get("date").toString());
                        donnees.add(jsonObj.get("nbr").toString());

                    } catch (JSONException e) {
                        Log.i("tagjsonexp", "" + e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Error while parsing",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                    Log.i("pass 1", "connection success ");

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }
            }
        });
        t.start();

        t.join();
    }

    public void update_view() {
        TextKmTotal.setText(donnees.get(0));
        TextEssenceTotal.setText(donnees.get(1));
        TextPrixTotal.setText(donnees.get(2));
        TextPrixKm.setText(donnees.get(3));
        TextPrixCent.setText(donnees.get(4));
        TextPrixCovoit.setText(donnees.get(5));
        TextViewInfo.setText("Depuis :  " + donnees.get(6) + "     avec    " + donnees.get(7) + "  échantillons. ");
    }
}

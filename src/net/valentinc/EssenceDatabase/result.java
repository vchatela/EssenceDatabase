package net.valentinc.EssenceDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by valentin on 25/06/15.
 */
public class result extends Activity {
    Button btnRafraichir = null;
    Button btnRetour = null;
    TextView TextKmMoyen = null;
    TextView TextKmTotal = null;
    TextView TextKmDernier = null;

    TextView TextEssenceMoyen = null;
    TextView TextEssenceDernier = null;
    TextView TextEssenceTotal = null;

    TextView TextPrixMoyen= null;
    TextView TextPrixDernier = null;
    TextView TextPrixTotal = null;
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
        TextKmMoyen= (TextView) findViewById(R.id.textViewKmMoyen);
        TextKmDernier = (TextView) findViewById(R.id.textViewKmDernier);
        TextKmTotal = (TextView) findViewById(R.id.textViewKmTotal);

        TextEssenceMoyen = (TextView) findViewById(R.id.textViewEssenceMoyenPlein);
        TextEssenceDernier = (TextView) findViewById(R.id.textViewEssenceDernierPlein);
        TextEssenceTotal = (TextView) findViewById(R.id.EssenceTotal);

        TextPrixDernier = (TextView) findViewById(R.id.textViewPrix);
        TextPrixMoyen = (TextView) findViewById(R.id.textViewPrixMoyen);
        TextPrixKm = (TextView) findViewById(R.id.textViewPrixKm);
        TextPrixTotal = (TextView) findViewById(R.id.textViewPrixTotal);
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
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                donnees = new ArrayList<>();

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://88.142.52.11/android/getdata.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();

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
                        donnees.add(jsonObj.get("akm").toString() + " km");
                        donnees.add(jsonObj.get("lkm").toString() + " km");
                        donnees.add(jsonObj.get("al").toString() + " litres");
                        donnees.add(jsonObj.get("ll").toString() + " litres");
                        donnees.add(jsonObj.get("tl").toString() + " litres");
                        donnees.add(jsonObj.get("ap").toString() + " €");
                        donnees.add(jsonObj.get("lp").toString() + " €");
                        donnees.add(jsonObj.get("pkm").toString() + " €");
                        donnees.add(jsonObj.get("kmt").toString() + " km");
                        donnees.add(jsonObj.get("pt").toString() + " €");
                        donnees.add(jsonObj.get("pc").toString() + " €");
                        donnees.add(jsonObj.get("date").toString());
                        donnees.add(jsonObj.get("nbr").toString());

                    } catch (JSONException e) {
                        Log.i("tagjsonexp", "" + e.toString());
                    } catch (ParseException e) {
                        Log.i("tagjsonpars", "" + e.toString());
                    }
                    Log.e("pass 1", "connection success ");

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }
            }
        });
        t.start();

        t.join();
    }

    public void update_view() {
        TextKmMoyen.setText(donnees.get(0));
        TextKmDernier.setText(donnees.get(1));
        TextEssenceMoyen.setText(donnees.get(2));
        TextEssenceDernier.setText(donnees.get(3));
        TextEssenceTotal.setText(donnees.get(4));
        TextPrixMoyen.setText(donnees.get(5));
        TextPrixDernier.setText(donnees.get(6));
        TextPrixKm.setText(donnees.get(7));
        TextKmTotal.setText(donnees.get(8));
        TextPrixTotal.setText(donnees.get(9));
        TextPrixCovoit.setText(donnees.get(10));
        TextViewInfo.setText("Since :  " + donnees.get(11) + "     avec    " + donnees.get(12) + "  échantillons. ");
    }
}

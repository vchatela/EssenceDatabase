package com.example.EssenseDatabase;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.*;

public class EssenceDatabase extends Activity {
    /**
     * Called when the activity is first created.
     */
    Button ValiderButton = null;
    EditText editTextKm = null;
    EditText editTextEuro = null;
    EditText editTextEuroLitre = null;
    EditText editTextResult = null;

    InputStream is=null;
    String result=null;
    String line=null;
    int code;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // récupération des vues
        ValiderButton = (Button) findViewById(R.id.okButton);
        editTextEuro = (EditText) findViewById(R.id.editTextEuro);
        editTextEuroLitre = (EditText) findViewById(R.id.editTextEuroLitre);
        editTextResult = (EditText) findViewById(R.id.editTextResult);
        editTextKm = (EditText) findViewById(R.id.editTextKm);

        // on attribue un listener adapté aux vue
        ValiderButton.setOnClickListener(validerButton);

    }
    private OnClickListener validerButton = new OnClickListener() {
        @Override
        public void onClick(View v){
            // Ici on vérifie les valeurs avant d'envoyer à la database
            int valueEuro = Integer.parseInt(editTextEuro.getText().toString());
            int valueEuroLitre = Integer.parseInt(editTextEuroLitre.getText().toString());
            int valueKm = Integer.parseInt(editTextKm.getText().toString());
            // on vérifie les valeurs
            if (!(valueEuro < 0 || valueEuroLitre < 0 || valueKm < 0 || valueEuro > 90 || valueEuroLitre > 3 || valueKm > 1500)){
                // on envoi à la base de donnée
                editTextResult.setText("Connexion...");
                if (! insert()){

                }
                else {

                }
            }

        }
    };
    public boolean insert(){
        Date date = new Date();
        DateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateformat.format(date);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("Prix",editTextEuro.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("Distance",editTextKm.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("PrixLitre",editTextEuroLitre.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("Date",dateString));

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://88.142.52.11/insert.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }
        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

    return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.layout.main, menu);
        return true;
    }
}

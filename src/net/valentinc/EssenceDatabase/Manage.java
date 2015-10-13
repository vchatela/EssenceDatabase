package net.valentinc.EssenceDatabase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ValentinC on 08/10/2015.
 *
 */
public class Manage extends Activity {
    // Spinner to manage (modify - delete) inside DB
    private Spinner spinnerListValue;
    private Button okButton;
    private Button modifyButton;
    private Button deleteButton;
    private EditText textEditDistance;
    private EditText textEditPrix;
    private EditText textEditPrixLitre;

    private JSONArray jsonResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage);


        textEditDistance = (EditText) findViewById(R.id.textViewEditDistance);
        textEditPrix = (EditText) findViewById(R.id.textViewEditPrix);
        textEditPrixLitre = (EditText) findViewById(R.id.textViewEditPrixLitre);

        spinnerListValue = (Spinner) findViewById(R.id.spinner);
        spinnerListValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO : update la view en bas
                textEditDistance.setText("");
                textEditPrix.setText("");
                textEditPrixLitre.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        setEditable(false);

        okButton = (Button) findViewById(R.id.buttonOK);
        modifyButton = (Button) findViewById(R.id.buttonModificate);
        deleteButton = (Button) findViewById(R.id.buttonDelete);

        okButton.setVisibility(View.INVISIBLE);

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);

                setEditable(true);
                spinnerListValue.setEnabled(false);

                okButton.setVisibility(View.VISIBLE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        okButton.setVisibility(View.INVISIBLE);
                        //TODO : send the request
                            sendModification();

                        setEditable(false);
                        spinnerListValue.setEnabled(true);

                        modifyButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteElement();
            }
        });

        updateSpinnerValue();

    }

    private void setEditable(boolean b){

        textEditDistance.setFocusable(b);
        textEditPrix.setFocusable(b);
        textEditPrixLitre.setFocusable(b);
        textEditDistance.setClickable(b);
        textEditPrix.setClickable(b);
        textEditPrixLitre.setClickable(b);
    }

    public void sendModification(){
            //TODO : request to sendModification
    }

    public void deleteElement(){
            //TODO : request to deleteElement
    }

    public void updateSpinnerValue(){
            //TODO : getValue from json in website API
        List<String> list = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    HttpURLConnection conn;
                    URL url = new URL("http://88.142.52.11/android/getRowData.php");

                    conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = r.readLine()) != null) {
                        jsonResult = new JSONArray(line);
                    }
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO : mise en forme
                        /*
        [{"1":["58","500","1.379","2015-03-30"]},{"2":["35","450","1.415","2015-04-05"]}]
                         */
            int i = 1;
        try {
            while(jsonResult.get(i) != null){
                // TODO : list.add();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add TOTAL (string) to Spinner with
        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerListValue.setAdapter(dataAdapter);
    }
}

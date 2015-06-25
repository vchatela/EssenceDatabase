package com.example.EssenseDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by valentin on 25/06/15.
 */
public class result extends Activity {
    Button btnRafraichir = null;
    Button btnRetour = null;
    TextView TextKmMoyen = null;
    TextView TextKmDernier = null;
    TextView TextEuroMoyen= null;
    TextView TextEuroDernier = null;
    TextView TextPrixMoyen= null;
    TextView TextPrixDernier = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // on récupère les edittext et les boutons
        btnRafraichir = (Button) findViewById(R.id.buttonRafraichir);
        btnRetour  = (Button) findViewById(R.id.buttonRetour);
        TextKmMoyen= (TextView) findViewById(R.id.textViewKmMoyen);
        TextKmDernier = (TextView) findViewById(R.id.textViewKmDernier);
        TextEuroMoyen = (TextView) findViewById(R.id.textViewEssenceMoyenPlein);
        TextEuroDernier = (TextView) findViewById(R.id.textViewEssenceDernierPlein);
        TextPrixDernier = (TextView) findViewById(R.id.textViewPrix);
        TextPrixMoyen = (TextView) findViewById(R.id.textViewPrixMoyen);

        // On s'occupe des clics sur Rafraichir et Retour
        btnRetour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), main.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});
    }

    private View.OnClickListener RafraichirButton = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            // TODO : ici on redemande les données à la DB
        }
    };

}

package fr.psyk.tusmotssolver;

import static java.lang.reflect.Array.getInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String BUNDLE_STATE_COMPTEUR = "BUNDLE_STATE_COMPTEUR";
   List<String> retourMots;
    List<String> listOk = new ArrayList<>();
    int nbLettre=4, combienDeLettre;
    String total = "AZERTYUIOPMLKJHGFDSQWXCVBN";
    int compteur ;
    int compteur_affichage =0;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_COMPTEUR = "SHARED_PREF_USER_INFO_COMPTEUR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compteur = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_COMPTEUR, -1);
        ListView listView = findViewById(R.id.listView);
        TextView text_compteur = findViewById(R.id.textView_TricheCounter);
        EditText code1=findViewById(R.id.main_l1);
        EditText code2=findViewById(R.id.main_l2);
        EditText code3=findViewById(R.id.main_l3);
        EditText code4=findViewById(R.id.main_l4);
        EditText code5=findViewById(R.id.main_l5);
        EditText code6=findViewById(R.id.main_l6);
        EditText code7=findViewById(R.id.main_l7);
        EditText code8=findViewById(R.id.main_l8);
        EditText code9=findViewById(R.id.main_l9);
        EditText code10=findViewById(R.id.main_l10);
        EditText code11=findViewById(R.id.main_l11);
        EditText code12=findViewById(R.id.main_l12);


        code1.addTextChangedListener(new MyTextWatcher(1, code1.getNextFocusDownId()));
        code2.addTextChangedListener(new MyTextWatcher(1, code2.getNextFocusDownId()));
        code3.addTextChangedListener(new MyTextWatcher(1, code3.getNextFocusDownId()));
        code4.addTextChangedListener(new MyTextWatcher(1, code4.getNextFocusDownId()));
        code5.addTextChangedListener(new MyTextWatcher(1, code5.getNextFocusDownId()));
        code6.addTextChangedListener(new MyTextWatcher(1, code6.getNextFocusDownId()));
        code7.addTextChangedListener(new MyTextWatcher(1, code7.getNextFocusDownId()));
        code8.addTextChangedListener(new MyTextWatcher(1, code8.getNextFocusDownId()));
        code9.addTextChangedListener(new MyTextWatcher(1, code9.getNextFocusDownId()));
        code10.addTextChangedListener(new MyTextWatcher(1, code10.getNextFocusDownId()));
        code11.addTextChangedListener(new MyTextWatcher(1, code11.getNextFocusDownId()));
        code12.addTextChangedListener(new MyTextWatcher(1, code12.getNextFocusDownId()));

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        int newVersionDbMots = 0;
        int versionDbMots;
        versionDbMots = db.getVersion();

        InputStream insertsStream = this.getResources().openRawResource(R.raw.mots1);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));
        try {
            String insertStmt = insertReader.readLine();
            newVersionDbMots = Integer.parseInt(insertStmt);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (versionDbMots != newVersionDbMots){


            try {
                int versionAUpdate = db.insertFromFile(this,R.raw.mots1);
                System.out.println("la nouvelle version est = "+ versionAUpdate);
                db.updateVersion(versionAUpdate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("meme version");
        }



        combienDeLettre =recupValueSeek();
        Button buttonGo = findViewById(R.id.game_button_go);
        EditText lettrearemove = findViewById(R.id.main_lettreasupprimer);
        EditText lettreAContenir = findViewById(R.id.main_lettrecontenu);
        buttonGo.setOnClickListener(v -> {
            compteur = compteur +1;
            text_compteur.setText(String.valueOf(compteur));
           String masque = gesMasque();
            String lettreAEnlever = lettrearemove.getText().toString();
            String lettreaprendre = lettreAContenir.getText().toString();
            creeMaliste(nbLettre,masque,lettreAEnlever,lettreaprendre);
            creeViewList();

        });

        TextView textView = findViewById(R.id.textView_Comptient);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compteur_affichage> 10){
                    text_compteur.setText(String.valueOf(compteur));
                    text_compteur.setVisibility(View.VISIBLE);
                }else{
                    compteur_affichage = compteur_affichage + 1;
                //    System.out.println(compteur_affichage);
                }
            }
        });
        Button buttonReset = findViewById(R.id.button_reset);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code1.setText(" ");
                code2.setText(" ");
                code3.setText(" ");
                code4.setText(" ");
                code5.setText(" ");
                code6.setText(" ");
                code7.setText(" ");
                code8.setText(" ");
                code9.setText(" ");
                code10.setText(" ");
                code11.setText(" ");
                code12.setText(" ");
                lettrearemove.setText("");
                lettreAContenir.setText("");
                listView.setAdapter(null);
                compteur_affichage = 0;
                text_compteur.setVisibility(View.INVISIBLE);
                recupValueSeek();
            }
        });


    }

    private void creeViewList() {
        ListView listView = findViewById(R.id.listView);

        if (retourMots.size()> 1000){

            retourMots.subList(1000, retourMots.size()).clear();
        }
        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, retourMots);
        listView.setAdapter(arrayAdapter);




        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, retourMots);
        listView.setAdapter(adapter);
    }

    private String gesMasque() {

        String motmasque ="";

        EditText l1 = findViewById(R.id.main_l1);
        EditText l2 = findViewById(R.id.main_l2);
        EditText l3 = findViewById(R.id.main_l3);
        EditText l4 = findViewById(R.id.main_l4);
        EditText l5 = findViewById(R.id.main_l5);
        EditText l6 = findViewById(R.id.main_l6);
        EditText l7 = findViewById(R.id.main_l7);
        EditText l8 = findViewById(R.id.main_l8);
        EditText l9 = findViewById(R.id.main_l9);
        EditText l10 = findViewById(R.id.main_l10);
        EditText l11 = findViewById(R.id.main_l11);
        EditText l12 = findViewById(R.id.main_l12);

        if (total.contains(l1.getText())){
            motmasque = motmasque + l1.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l2.getText())){
            motmasque = motmasque + l2.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l3.getText())){
            motmasque = motmasque + l3.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l4.getText())){
            motmasque = motmasque + l4.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l5.getText())){
            motmasque = motmasque + l5.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l6.getText())){
            motmasque = motmasque + l6.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l7.getText())){
            motmasque = motmasque + l7.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l8.getText())){
            motmasque = motmasque + l8.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l9.getText())){
            motmasque = motmasque + l9.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l10.getText())){
            motmasque = motmasque + l10.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l11.getText())){
            motmasque = motmasque + l11.getText();
        }else{
            motmasque = motmasque + "_";
        }
        if (total.contains(l12.getText())){
            motmasque = motmasque + l12.getText();
        }else{
            motmasque = motmasque + "_";
        }

        motmasque = motmasque.substring(0,nbLettre);


        return motmasque;
    }

    private int recupValueSeek() {


        TextView mTextView_Seek = findViewById(R.id.game_textView_Seek);
        SeekBar mySeekBar = findViewById(R.id.game_seekBar);
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mTextView_Seek.setText(String.valueOf(progress));
                String valeur = mTextView_Seek.getText().toString();
                nbLettre = Integer.parseInt(valeur);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        return nbLettre;
    }

    private void creeMaliste(int nblettre, String masque, String remove, String aprendre) {
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        retourMots = db.getmot(nblettre, masque,remove,aprendre);




    }

    private class MyTextWatcher implements TextWatcher {

        private final int maxLength;
        private final EditText next;

        MyTextWatcher(int maxLength, int nextId) {
            this.maxLength = maxLength;
            this.next = (nextId != -1) ? findViewById(nextId) : null;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() >= maxLength && next != null) {
                next.requestFocus();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }
    }
    private static class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();



        getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                .edit()
                .putInt(SHARED_PREF_USER_INFO_COMPTEUR, compteur)
                .apply();

    }

}
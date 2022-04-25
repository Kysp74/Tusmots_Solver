package fr.psyk.tusmotssolver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.style.AlignmentSpan;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Woordle";



    

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.

        String scriptVersion = "CREATE TABLE IF NOT EXISTS version (version INTEGER);";
        // Execute Script.
        db.execSQL(scriptVersion);
        String scriptVersion2 = "INSERT INTO version (version) VALUES (0);";
        // Execute Script.
        db.execSQL(scriptVersion2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        Log.i(TAG, "drop table");
                db.execSQL("DROP TABLE IF EXISTS version");

        // Recreate
        onCreate(db);
    }


    public int insertFromFile(Context context, int resourceId) throws IOException {

        SQLiteDatabase db = this.getWritableDatabase();

        // Reseting Counter
        int version = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();
            if (insertStmt.length()>2){
                db.execSQL(insertStmt);

            }else
                version = Integer.parseInt(insertStmt);

        }
        insertReader.close();

        // returning number of inserted rows
        return version;
    }

    public int getVersion(){

        String Query = "SELECT  * FROM version";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "MyDatabaseHelper.getVersion");
        Cursor cursor = db.rawQuery(Query,null);
        if (cursor != null)
            cursor.moveToFirst();

        int version =  Integer.parseInt(cursor.getString(0));
        // return note
        return version;
    }

 public void updateVersion(int version){
        //mettre la version au meme nombre que le premier caractère du ficheri
     Log.i(TAG, "MyDatabaseHelper.updateversion");
     SQLiteDatabase db = this.getWritableDatabase();
     ContentValues cv = new ContentValues();
     cv.put("version",version);
     String where = "rowid=1";
     db.update("version", cv, where,null);
        }

        public List<String> getmot(int nblettre,String masque){
            List<String> motatrouver = new ArrayList<>();
            Log.i(TAG, "MyDatabaseHelper.getmot de " + nblettre + " lettres");
           String Query = " SELECT * FROM l"+nblettre+" WHERE mots LIKE '"+masque+"';";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(Query,null);
            if (cursor.moveToFirst()) {
               do {
                    motatrouver.add(cursor.getString(0));

                } while (cursor.moveToNext());
            }

           if (motatrouver.size()> 1000){

               motatrouver.subList(1000, motatrouver.size()).clear();
           }



        return motatrouver ;
        }

        public boolean motEstPresent(String motATester){
            String nomTable = "l" + motATester.length();
            String sql = "SELECT  count(*) FROM " +nomTable+ " WHERE mots = '" +motATester + "';";
            SQLiteDatabase db = this.getReadableDatabase();
            Log.i(TAG, "MyDatabaseHelper.getMotExiste");
            SQLiteStatement statement = db.compileStatement(sql);

            try {
                return statement.simpleQueryForLong() > 0;
            } finally {
                statement.close();
            }
        }

}










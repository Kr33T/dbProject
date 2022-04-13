package com.example.dbproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnClear;
    EditText etMarka, etModel, etGosnom;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etMarka = (EditText) findViewById(R.id.etMarka);
        etModel = (EditText) findViewById(R.id.etModel);
        etGosnom = (EditText) findViewById(R.id.etGosnom);

        etMarka = (EditText) findViewById(R.id.etMarka);
        etMarka.setOnFocusChangeListener(this::onFocusChange);
        etModel = (EditText) findViewById(R.id.etModel);
        etModel.setOnFocusChangeListener(this::onFocusChange);
        etGosnom = (EditText) findViewById(R.id.etGosnom);
        etGosnom.setOnFocusChangeListener(this::onFocusChange);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();
    }

    public void onFocusChange(View view, boolean b) {
        switch(view.getId()){
            case R.id.etMarka:
                if(b){
                    etMarka.setText("");
                }
                else if(etMarka.getText().toString().equals("")){
                    etMarka.setText("Марка");
                }
                break;
            case R.id.etModel:
                if(b){
                    etModel.setText("");
                }
                else if(etModel.getText().toString().equals("")){
                    etModel.setText("Модель");
                }
                break;
            case R.id.etGosnom:
                if(b){
                    etGosnom.setText("");
                }
                else if(etGosnom.getText().toString().equals("")){
                    etGosnom.setText("Гос. номер");
                }
                break;
        }
    }

    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_AUTOMOBILES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int markaIndex = cursor.getColumnIndex(DBHelper.KEY_MARKA);
            int modelIndex = cursor.getColumnIndex(DBHelper.KEY_MODEL);
            int gosNomIndex = cursor.getColumnIndex(DBHelper.KEY_GOSNOM);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                outputID.setTextSize(12);
                dbOutputRow.addView(outputID);

                TextView outputMarka = new TextView(this);
                params.weight = 3.0f;
                outputMarka.setLayoutParams(params);
                outputMarka.setText(cursor.getString(markaIndex));
                outputMarka.setTextSize(12);
                dbOutputRow.addView(outputMarka);

                TextView outputModel = new TextView(this);
                params.weight = 3.0f;
                outputModel.setLayoutParams(params);
                outputModel.setText(cursor.getString(modelIndex));
                outputModel.setTextSize(12);
                dbOutputRow.addView(outputModel);

                TextView outputGosNom = new TextView(this);
                params.weight = 3.0f;
                outputGosNom.setLayoutParams(params);
                outputGosNom.setText(cursor.getString(gosNomIndex));
                outputGosNom.setTextSize(12);
                dbOutputRow.addView(outputGosNom);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("-");
                deleteBtn.setTextSize(12);
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                String marka = etMarka.getText().toString();
                String model = etModel.getText().toString();
                String gosNom = etGosnom.getText().toString();
                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_MARKA, marka);
                contentValues.put(DBHelper.KEY_MODEL, model);
                contentValues.put(DBHelper.KEY_GOSNOM, gosNom);

                database.insert(DBHelper.TABLE_AUTOMOBILES, null, contentValues);
                etMarka.setText("Марка");
                etModel.setText("Модель");
                etGosnom.setText("Гос. номер");
                UpdateTable();
                break;
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_AUTOMOBILES, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_AUTOMOBILES, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf(v.getId())});

                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_AUTOMOBILES, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int markaIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MARKA);
                    int modelIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MODEL);
                    int gosNomIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_GOSNOM);
                    int realID = 1;
                    do{
                        if(cursorUpdater.getInt(idIndex) > idIndex){
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_MARKA, cursorUpdater.getString(markaIndex));
                            contentValues.put(DBHelper.KEY_MODEL, cursorUpdater.getString(modelIndex));
                            contentValues.put(DBHelper.KEY_GOSNOM, cursorUpdater.getString(gosNomIndex));
                            database.replace(DBHelper.TABLE_AUTOMOBILES, null, contentValues);
                        }
                        realID++;
                    } while(cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast() && (cursorUpdater.getInt(idIndex) == realID)){
                        database.delete(DBHelper.TABLE_AUTOMOBILES, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                break;
        }
    }

}
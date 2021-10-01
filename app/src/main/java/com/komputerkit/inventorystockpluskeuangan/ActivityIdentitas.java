package com.komputerkit.inventorystockpluskeuangan;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ActivityIdentitas extends AppCompatActivity {

    Toolbar appbar;
    Database db;
    View v;
    TextInputEditText edt1,edt2,edt3,edt4,edt5,edt6;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identitas);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Identitas",getSupportActionBar());

        db = new Database(this);
        v = this.findViewById(android.R.id.content);

        btnClear = (Button) findViewById(R.id.btnClear);
        edt1 = (TextInputEditText)findViewById(R.id.eNama);
        edt2 = (TextInputEditText)findViewById(R.id.eAlamat);
        edt3 = (TextInputEditText)findViewById(R.id.eTelp);
        edt4 = (TextInputEditText)findViewById(R.id.cap1);
        edt5 = (TextInputEditText)findViewById(R.id.cap2);
        edt6 = (TextInputEditText)findViewById(R.id.cap3);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText("");edt2.setText("");edt3.setText("");edt4.setText("");edt5.setText("");edt6.setText("");
                edt1.getText().clear();edt2.getText().clear();edt3.getText().clear();edt4.getText().clear();edt5.getText().clear();edt6.getText().clear();
            }
        });

        setText() ;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setText(){
        Cursor c = db.sq(Query.selectwhere("tblidentitas")+Query.sWhere("ididentitas","1"));
        if(c.getCount() == 1){
            c.moveToNext();
            Function.setText(v,R.id.eNama,Function.getString(c,"nama")) ;
            Function.setText(v,R.id.eAlamat,Function.getString(c,"alamat")) ;
            Function.setText(v,R.id.eTelp,Function.getString(c,"telp")) ;
            Function.setText(v,R.id.cap1,Function.getString(c,"cap1")) ;
            Function.setText(v,R.id.cap2,Function.getString(c,"cap2")) ;
            Function.setText(v,R.id.cap3,Function.getString(c,"cap3")) ;
        }

    }

    public void simpan(View view){
        Cursor c = db.sq(Query.select("tblidentitas")) ;
        String[] p = { "1",Function.getText(v,R.id.eNama),
                Function.getText(v,R.id.eAlamat),
                Function.getText(v,R.id.eTelp),
                Function.getText(v,R.id.cap1),
                Function.getText(v,R.id.cap2),
                Function.getText(v,R.id.cap3)
        } ;
        String[] p1 = { Function.getText(v,R.id.eNama),
                Function.getText(v,R.id.eAlamat),
                Function.getText(v,R.id.eTelp),
                Function.getText(v,R.id.cap1),
                Function.getText(v,R.id.cap2),
                Function.getText(v,R.id.cap3),
                "1"
        } ;

        String nama=Function.getText(v,R.id.eNama );
        String alamat=Function.getText(v,R.id.eAlamat );
        String telepon=Function.getText(v,R.id.eTelp );
        String cap1=Function.getText(v,R.id.cap1 );
        String cap2=Function.getText(v,R.id.cap2 );
        String cap3=Function.getText(v,R.id.cap3 );

        String q = "" ;
        if (nama.equals("") || alamat.equals("") || telepon.equals("") || cap1.equals("") || cap2.equals("") || cap3.equals("")){
            Toast.makeText(this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            if(c.getCount() == 1){
                q = Query.splitParam("UPDATE tblidentitas SET nama=? , alamat=? ,telp=? ,cap1=? , cap2=? , cap3=? WHERE ididentitas=?   ",p1) ;
            } else {
                q = Query.splitParam("INSERT INTO tblidentitas VALUES(?,?,?,?,?,?,?)",p) ;
            }
            if(db.exc(q)){
                Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal disimpan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

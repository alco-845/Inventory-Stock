package com.komputerkit.inventorystockpluskeuangan;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityTambahKategori extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    TextInputEditText edtKategori;
    String Kategori;
    Integer idKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kategori);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Tambah Kategori",getSupportActionBar());

        btnSimpan=(Button) findViewById(R.id.Simpan);
        edtKategori=(TextInputEditText)findViewById(R.id.tKategori);

        Bundle extra = getIntent().getExtras();
        if (extra==null){
            //Insert
            idKategori=null;
        }else {
            idKategori = extra.getInt("idkategori");
            edtKategori.setText(extra.getString("kategori"));
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kategori=edtKategori.getText().toString();
                if (Kategori.equals("")){
                    Toast.makeText(ActivityTambahKategori.this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else {
                    Database db = new Database(ActivityTambahKategori.this);
                    if (idKategori==null) {
                        if (db.insertKategori(Kategori)) {
                            Toast.makeText(ActivityTambahKategori.this, "Tambah kategori berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahKategori.this, "Tambah data gagal", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (db.updateKategori(idKategori,Kategori)){
                            Toast.makeText(ActivityTambahKategori.this, "Berhasil memperbaharui data", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ActivityTambahKategori.this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }});

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

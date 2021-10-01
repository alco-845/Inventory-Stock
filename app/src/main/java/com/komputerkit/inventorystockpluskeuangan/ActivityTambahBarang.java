package com.komputerkit.inventorystockpluskeuangan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class ActivityTambahBarang extends AppCompatActivity {

    Toolbar appbar;
    Spinner spKat, spSatuan;
    Button bSimpan;
    TextInputEditText edtNamaBarang, edtStok;
    Integer idBarang, idKat, idSat;
    String namaBarang, stok, sKat="", sSat="", deviceid;
    SharedPreferences getPrefs ;
    View v;
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_barang);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Tambah Barang",getSupportActionBar());

        v = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config",this.MODE_PRIVATE));

        Database db = new Database(this);
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        deviceid = Function.getDecrypt(getPrefs.getString("deviceid","")) ;

        bSimpan = (Button) findViewById(R.id.bSimpan);
        edtNamaBarang = (TextInputEditText) findViewById(R.id.eBarang);
        edtStok = (TextInputEditText) findViewById(R.id.eStok);

        final List<String> getIdKat = db.getIdKategori();
        spKat = (Spinner) findViewById(R.id.spKat);
        getKategoriData();
        spKat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                sKat = parent.getItemAtPosition(pos).toString();
                idKat = Function.strToInt(getIdKat.get(parent.getSelectedItemPosition()));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final List<String> getIdSat = db.getIdSatuan();
        spSatuan=(Spinner)findViewById(R.id.spSatuan);
        getSatuanData();
        spSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sSat = parent.getItemAtPosition(position).toString();
                idSat = Function.strToInt(getIdSat.get(parent.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            //Insert
            idBarang = null;
        } else {
            idBarang = extra.getInt("idbarang");
            idKat = extra.getInt("idkategori");
            idSat = extra.getInt("idsatuan");
            Cursor c = db.sq("SELECT * FROM tblkategori WHERE idkategori<"+Function.intToStr(idKat));
            Cursor cur = db.sq("SELECT * FROM tblsatuan WHERE idsatuan<"+Function.intToStr(idSat));
            spKat.setSelection(c.getCount());
            spSatuan.setSelection(cur.getCount());
            edtNamaBarang.setText(extra.getString("barang"));
            edtStok.setText(extra.getString("stok"));
        }

        bSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namaBarang = edtNamaBarang.getText().toString();
                stok = edtStok.getText().toString();

                if (namaBarang.equals("") || stok.equals("") || stok.equals("0")) {
                    Toast.makeText(ActivityTambahBarang.this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    Database db = new Database(ActivityTambahBarang.this);
                    if (idBarang == (null) || idKat == (null) || idSat == (null)){
                        if (sKat.equals("")||sSat.equals("")){
                            Toast.makeText(ActivityTambahBarang.this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
                        }else {
                            if (db.insertBarang(idKat,idSat,namaBarang, Function.changeComa(stok))) {
                                Toast.makeText(ActivityTambahBarang.this, "Tambah Barang berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                                tambahlimit();
                            } else {
                                Toast.makeText(ActivityTambahBarang.this, "Tambah data gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        if (db.updateBarang(idBarang, idKat, idSat, namaBarang, stok)) {
                            Toast.makeText(ActivityTambahBarang.this, "Berhasil memperbaharui data", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahBarang.this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void tambahlimit() {
        boolean status = ActivityBarang.status;
        if (!status){
            int batas = Function.strToInt(config.getCustom("barang", "1"))+1;
            config.setCustom("barang", Function.intToStr(batas));
        }
    }

    private void getKategoriData(){
        Database db = new Database(this);
        List<String> labels = db.getKategori();

        ArrayAdapter<String> data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,labels);
        data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKat.setAdapter(data);
    }

    private void getSatuanData(){
        Database db = new Database(this);
        List<String> labels = db.getSatuan();

        ArrayAdapter<String> data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,labels);
        data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSatuan.setAdapter(data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

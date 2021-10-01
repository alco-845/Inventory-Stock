package com.komputerkit.inventorystockpluskeuangan;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityTambahSatuan extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    TextInputEditText edtSatuanKecil, edtSatuanBesar, edtnilai;
    String SatuanKecil, SatuanBesar;
    Integer idSatuan;
    String Nilai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_satuan);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Tambah Satuan",getSupportActionBar());

        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        edtSatuanKecil = (TextInputEditText) findViewById(R.id.eSatuanKecil);
        edtSatuanBesar = (TextInputEditText) findViewById(R.id.eSatuanBesar);
        edtnilai = (TextInputEditText) findViewById(R.id.eNilai);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            //Insert
            idSatuan = null;
        } else {
            idSatuan = extra.getInt("idsatuan");
            edtSatuanKecil.setText(extra.getString("satuankecil"));
            edtSatuanBesar.setText(extra.getString("satuanbesar"));
            edtnilai.setText(extra.getString("nilai"));
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SatuanKecil = edtSatuanKecil.getText().toString();
                SatuanBesar = edtSatuanBesar.getText().toString();
                Nilai = edtnilai.getText().toString();

                if (SatuanKecil.equals("") || SatuanBesar.equals("") || Nilai.equals("") || Nilai.equals("0")) {
                    Toast.makeText(ActivityTambahSatuan.this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    Database db = new Database(ActivityTambahSatuan.this);
                    if (idSatuan == null) {
                        if (db.insertSatuan(SatuanKecil, SatuanBesar, Function.changeComa(Nilai))) {
                            Toast.makeText(ActivityTambahSatuan.this, "Tambah satuan berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahSatuan.this, "Tambah data gagal", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (db.updateSatuan(idSatuan, SatuanKecil, SatuanBesar, Nilai)) {
                            Toast.makeText(ActivityTambahSatuan.this, "Berhasil memperbaharui data", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahSatuan.this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.komputerkit.inventorystockpluskeuangan;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class ActivityTambahPelanggan extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    TextInputEditText edtNamaPelanggan, edtAlamat, edtNoTelp;
    String namapelanggan, alamat;
    Integer idPelanggan;
    String notelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pelanggan);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Tambah Pelanggan",getSupportActionBar());

        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        edtNamaPelanggan = (TextInputEditText) findViewById(R.id.tNama);
        edtAlamat = (TextInputEditText) findViewById(R.id.tAlamat);
        edtNoTelp = (TextInputEditText) findViewById(R.id.tTelp);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            //Insert
            idPelanggan = null;
        } else {
            idPelanggan = extra.getInt("idpelanggan");
            edtNamaPelanggan.setText(extra.getString("pelanggan"));
            edtAlamat.setText(extra.getString("alamatpel"));
            edtNoTelp.setText(extra.getString("telppel"));
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namapelanggan = edtNamaPelanggan.getText().toString();
                alamat = edtAlamat.getText().toString();
                notelp = edtNoTelp.getText().toString();

                if (namapelanggan.equals("") || alamat.equals("") || notelp.equals("")) {
                    Toast.makeText(ActivityTambahPelanggan.this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    Database db = new Database(ActivityTambahPelanggan.this);
                    if (idPelanggan == null) {
                        if (db.insertPelanggan(namapelanggan, alamat, notelp)) {
                            Toast.makeText(ActivityTambahPelanggan.this, "Tambah Pelanggan berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahPelanggan.this, "Tambah data gagal", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (db.updatePelanggan(idPelanggan, namapelanggan, alamat, notelp)) {
                            Toast.makeText(ActivityTambahPelanggan.this, "Berhasil memperbaharui data", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahPelanggan.this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show();
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

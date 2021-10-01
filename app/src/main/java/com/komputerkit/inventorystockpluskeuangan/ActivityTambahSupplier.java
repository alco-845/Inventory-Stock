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

public class ActivityTambahSupplier extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    TextInputEditText edtNamaSupplier, edtAlamat, edtNoTelp;
    String namasupplier, alamat;
    Integer idsupplier;
    String notelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_supplier);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Tambah Supplier",getSupportActionBar());

        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        edtNamaSupplier= (TextInputEditText) findViewById(R.id.etNama);
        edtAlamat = (TextInputEditText) findViewById(R.id.etAlamat);
        edtNoTelp = (TextInputEditText) findViewById(R.id.etTelp);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            //Insert
            idsupplier = null;
        } else {
            idsupplier = extra.getInt("idsupplier");
            edtNamaSupplier.setText(extra.getString("supplier"));
            edtAlamat.setText(extra.getString("alamatsupp"));
            edtNoTelp.setText(extra.getString("telpsupp"));
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namasupplier= edtNamaSupplier.getText().toString();
                alamat = edtAlamat.getText().toString();
                notelp = edtNoTelp.getText().toString();

                if (namasupplier.equals("") || alamat.equals("") || notelp.equals("")) {
                    Toast.makeText(ActivityTambahSupplier.this, "Isi data terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    Database db = new Database(ActivityTambahSupplier.this);
                    if (idsupplier== null) {
                        if (db.insertSupplier(namasupplier, alamat, notelp)) {
                            Toast.makeText(ActivityTambahSupplier.this, "Tambah Supplier berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahSupplier.this, "Tambah data gagal", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (db.updateSupplier(idsupplier, namasupplier, alamat, notelp)) {
                            Toast.makeText(ActivityTambahSupplier.this, "Berhasil memperbaharui data", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityTambahSupplier.this, "Gagal memperbaharui data", Toast.LENGTH_SHORT).show();
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

package com.komputerkit.inventorystockpluskeuangan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ActivityMaster extends AppCompatActivity {

    Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        getSupportActionBar().setElevation(0);
        Function.btnBack("Master",getSupportActionBar());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void identitas(View view) {
        Intent intent = new Intent(this, ActivityIdentitas.class);
        startActivity(intent);
    }

    public void kategori(View view) {
        Intent intent = new Intent(this, ActivityKategori.class);
        startActivity(intent);
    }

    public void satuan(View view) {
        Intent intent = new Intent(this, ActivitySatuan.class);
        startActivity(intent);
    }

    public void barang(View view) {
        Intent intent = new Intent(this, ActivityBarang.class);
        startActivity(intent);
    }

    public void supplier(View view) {
        Intent intent = new Intent(this, ActivitySupplier.class);
        startActivity(intent);
    }

    public void pelanggan(View view) {
        Intent intent = new Intent(this, ActivityPelanggan.class);
        startActivity(intent);
    }
}

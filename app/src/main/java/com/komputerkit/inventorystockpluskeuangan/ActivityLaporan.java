package com.komputerkit.inventorystockpluskeuangan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ActivityLaporan extends AppCompatActivity {

    Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Laporan",getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void pelanggan(View v){
        Intent i = new Intent(this, ActivityLaporanMaster.class) ;
        i.putExtra("type","pelanggan") ;
        startActivity(i);
    }
    public void barang(View v){
        Intent i = new Intent(this, ActivityLaporanMaster.class) ;
        i.putExtra("type","barang") ;
        startActivity(i);
    }

    public void distributor(View v){
        Intent i = new Intent(this,ActivityLaporanMaster.class) ;
        i.putExtra("type","distributor") ;
        startActivity(i);
    }

    public void penyimpanan(View v){
        Intent i = new Intent(this,ActivityLaporanPenjualan.class) ;
        i.putExtra("type","penyimpanan") ;
        startActivity(i);

    }

    public void penjualan(View v){
        Intent i = new Intent(this,ActivityLaporanPenjualan.class) ;
        i.putExtra("type","penjualan") ;
        startActivity(i);
    }

    public void keuangan(View view) {
        Intent i = new Intent(this,ActivityLaporanKeuangan.class) ;
        i.putExtra("type","keuangan") ;
        startActivity(i);
    }
}

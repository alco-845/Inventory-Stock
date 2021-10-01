package com.komputerkit.inventorystockpluskeuangan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class ActivityTransaksi extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    Toolbar appbar;
    Database db;
    View view;
    static boolean status;
    BillingProcessor bp;
    String deviceid;
    SharedPreferences getPrefs ;
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        getSupportActionBar().setElevation(0);
        Function.btnBack("Transaksi",getSupportActionBar());

        db = new Database(this);
        view = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config", this.MODE_PRIVATE));
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        deviceid = Function.getDecrypt(getPrefs.getString("deviceid","")) ;
        bp = new BillingProcessor(this, Function.getBase64Code(), this);
        bp.initialize();

        if (!bp.isPurchased("inventorystockpk")){
            status = false;
        } else {
            status = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean limit(String item){
        int batas = Function.strToInt(config.getCustom(item, "1"));
        if (batas>5){
            return false;
        } else {
            return true;
        }
    }

    public void penyimpanan(View view) {
        Cursor c = db.sq("SELECT * FROM tblbarang WHERE idbarang");
        Cursor cur = db.sq("SELECT * FROM tblsatuan WHERE idsatuan");
        if (c.getCount()==0 && cur.getCount()==0){
            Toast.makeText(this, "Silahkan Masukan Data di menu Master", Toast.LENGTH_SHORT).show();
        } else {
            if (status){
                Intent intent = new Intent(this, ActivityPenyimpanan.class);
                startActivity(intent);
            } else {
                if (limit("penyimpanan")){
                    Intent intent = new Intent(this, ActivityPenyimpanan.class);
                    startActivity(intent);
                } else {
                    bp.purchase(this, "inventorystockpk");
                }
            }
        }
    }

    public void penjualan(View view) {
        Cursor c = db.sq("SELECT * FROM tblbarang WHERE idbarang");
        if (c.getCount()==0){
            Toast.makeText(this, "Silahkan Masukan Data di menu Master", Toast.LENGTH_SHORT).show();
        } else {
        if (status){
            Intent intent = new Intent(this, ActivityPenjualan.class);
            startActivity(intent);
        } else {
            if (limit("penjualan")){
                Intent intent = new Intent(this, ActivityPenjualan.class);
                startActivity(intent);
            } else {
                bp.purchase(this, "inventorystockpk");
            }
            }
        }
    }

    public void masuk(View view) {
        Intent intent = new Intent(this, ActivityTambahPemasukan.class);
        startActivity(intent);
    }

    public void keluar(View view) {
        Cursor c = db.sq("SELECT * FROM tbltransaksi WHERE idtransaksi");
        if (c.getCount()==0){
            Toast.makeText(this, "Silahkan Inputkan Pemasukan", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ActivityTambahPengeluaran.class);
            startActivity(intent);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        status = true;
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}

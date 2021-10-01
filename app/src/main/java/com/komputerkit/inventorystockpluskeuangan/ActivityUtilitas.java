package com.komputerkit.inventorystockpluskeuangan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class ActivityUtilitas extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    Toolbar appbar;
    static final Integer WRITE_EXST = 0x3 ;
    Database db ;
    Config config ;
    View v ;
    boolean bBackup = false;
    boolean bRestore= false;
    boolean bReset = false;
    SharedPreferences getPrefs ;

    String deviceid ;

    BillingProcessor bp;
    boolean status;
    ConstraintLayout pro;

    TextView desc4, sub4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilitas);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        getSupportActionBar().setElevation(0);
        Function.btnBack("Utilitas",getSupportActionBar());

        config = new Config(getSharedPreferences("config",this.MODE_PRIVATE));
        db = new Database(this) ;
        db.cektbl() ;
        v = this.findViewById(android.R.id.content);

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        bBackup = getPrefs.getBoolean("inBackup",false) ;
        bRestore = getPrefs.getBoolean("inRestore",false) ;
        bReset = getPrefs.getBoolean("inReset",false) ;
        deviceid = Function.getDecrypt(getPrefs.getString("deviceid","")) ;

        pro = (ConstraintLayout)findViewById(R.id.pro);
        desc4 = (TextView)findViewById(R.id.jdul1);
        sub4 = (TextView)findViewById(R.id.sjudul1);

        bp = new BillingProcessor(this, Function.getBase64Code(), this);
        bp.initialize();

        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(ActivityUtilitas.this, "inventorystockpk");
            }
        });

        String kondisi = config.getCustom("cek", "belum");
        if (kondisi.equals("ok")){
            desc4.setText("Terima Kasih");
            sub4.setText("Pembayaran Anda Berhasil");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void reset(View view){
        reset2() ;
    }

    public void backup(View v){
        if (status) {
            Intent i = new Intent(this, ActivityBackup.class);
            startActivity(i);
        } else {
            bp.purchase(this, "inventorystockpk");
        }
    }

    public void restore(View v){
        if (status){
            Intent i = new Intent(this,ActivityRestore.class);
            startActivity(i);
        } else {
            bp.purchase(this, "inventorystockpk");
        }
    }

    public void reset2(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Anda yakin Akan Reset Aplikasi ini ? ");
        alertDialogBuilder.setPositiveButton("Iya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        reset1();
                    }
                });

        alertDialogBuilder.setNegativeButton("Batal",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void reset1(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Reset akan menghilangkan atau menghapus semua data dalam Aplikasi ini ? ");
        alertDialogBuilder.setPositiveButton("Reset",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Function.deleteFile("data/data/com.komputerkit.inventorystockpluskeuangan/databases/"+"db_stock") ;
                        db.cektbl() ;
                        Intent i=new Intent(ActivityUtilitas.this,ActivitySplash.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        Toast.makeText(ActivityUtilitas.this, "Reset Data Berhasil, Aplikasi Terestart", Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("Batal",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        status = true;
        config.setCustom("cek", "ok");
        desc4.setText("Terima Kasih");
        sub4.setText("Pembayaran Anda Berhasil");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String kondisi = config.getCustom("cek", "belum");
        if (kondisi.equals("ok")){
            desc4.setText("Terima Kasih");
            sub4.setText("Pembayaran Anda Berhasil");
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}

package com.komputerkit.inventorystockpluskeuangan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityTambahPengeluaran extends AppCompatActivity {

    Button btnSimpan;
    ImageButton btntgltransaksi;
    EditText edtTglTransaksi,edtKeterangan;
    View v;
    Database db;
    Toolbar appbar;

    Config temp;

    int year, month, day ;
    Calendar calendar ;

    String faktur="00000000", saldo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengeluaran);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Pengeluaran",getSupportActionBar());

        temp = new Config(getSharedPreferences("temp",this.MODE_PRIVATE));
        db=new Database(this);
        v = this.findViewById(android.R.id.content);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        cek();
        getTotal();

        String date_n=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Function.setText(v, R.id.edtTglTransaksi,date_n);
        btntgltransaksi = (ImageButton)findViewById(R.id.ibtnTglTransaksi);
        btntgltransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void cek(){
        Cursor c = db.sq(Query.selectwhere("tbltransaksi")+Query.sWhere("keterangantransaksi", "0"));
        if (c.getCount()>0){
            c.moveToLast();
            String faktur = Function.getString(c, "fakturtransaksi");
            Function.setText(v, R.id.edtNomorFakturTransaksi, faktur);
        } else {
            getFaktur();
        }
    }

    private void getFaktur(){
        List<Integer> idtransaksi = new ArrayList<Integer>();
        String q="SELECT idtransaksi FROM tbltransaksi";
        Cursor c = db.sq(q);
        if (c.moveToNext()){
            do {
                idtransaksi.add(c.getInt(0));
            }while (c.moveToNext());
        }
        String tempFaktur="";
        int IdFaktur=0;
        if (c.getCount()==0){
            tempFaktur=faktur.substring(0,faktur.length()-1)+"1";
        }else {
            IdFaktur = idtransaksi.get(c.getCount()-1)+1;
            tempFaktur = faktur.substring(0,faktur.length()-String.valueOf(IdFaktur).length())+String.valueOf(IdFaktur);
        }
        Function.setText(v,R.id.edtNomorFakturTransaksi,tempFaktur);
    }

    public void setDate(int i) {
        showDialog(i);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this, dTerima, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener dTerima = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int thn, int bln, int day) {
            Function.setText(v, R.id.edtTglTransaksi, Function.setDatePickerNormal(thn,bln+1,day)) ;
        }
    };

    private void keluar(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Anda yakin ingin keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ActivityTambahPengeluaran.this, ActivityTransaksi.class) ;
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                startActivity(i);
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();

    }

    @Override
    public void onBackPressed() {
        keluar();
    }

    public String convertDate(String date){
        String[] a = date.split("/") ;
        return a[2]+a[1]+a[0];
    }

    public void simpan(View view){
        String sald;

        String eFaktur = Function.getText(v, R.id.edtNomorFakturTransaksi);
        String eTglT = Function.getText(v, R.id.edtTglTransaksi);
        String eHKeluar = Function.getText(v, R.id.edtHargaKeluar);
        String eKet = Function.getText(v, R.id.edtKeteranganTransaksi);
        String esaldo = Function.getText(v, R.id.tvSaldo);
        if (TextUtils.isEmpty(eFaktur) || TextUtils.isEmpty(eHKeluar) || Function.strToDouble(eHKeluar)==0 || Function.strToDouble(eFaktur)==0){
            Toast.makeText(this, "Masukan Data Dengan Benar", Toast.LENGTH_SHORT).show();
        } else {
            Cursor cursor = db.sq("SELECT * FROM tbltransaksi");
            if (cursor.getCount()>0){
                cursor.moveToLast();
                sald = Function.getString(cursor, "saldo");
            } else {
                sald = "0";
            }
            double saldo = Function.strToDouble(sald) - Function.strToDouble(eHKeluar);
            String idtransaksi = String.valueOf(eFaktur);
            String notransaksi = String.valueOf(eFaktur);
            int stat = 1;
            String qTrans;
            String[] simpan ={
                    idtransaksi,
                    convertDate(eTglT),
                    notransaksi,
                    eFaktur,
                    eKet,
                    eHKeluar,
                    String.valueOf(saldo),
                    String.valueOf(stat)
            };

            String q = Query.selectwhere("tbltransaksi") + Query.sWhere("fakturtransaksi", eFaktur);
            Cursor c = db.sq(q);
            if (c.getCount()==0) {
                qTrans = Query.splitParam("INSERT INTO tbltransaksi (idtransaksi,tgltransaksi,notransaksi,fakturtransaksi,keterangantransaksi,keluar,saldo,status) VALUES (?,?,?,?,?,?,?,?)",simpan);
            } else {
                qTrans = Query.splitParam("INSERT INTO tbltransaksi (idtransaksi,tgltransaksi,notransaksi,fakturtransaksi,keterangantransaksi,keluar,saldo,status) VALUES (?,?,?,?,?,?,?,?)",simpan);
            }

            if(Function.strToDouble(Function.getText(v, R.id.edtHargaKeluar)) <= Function.strToDouble(sald)){
                if (db.exc(qTrans)){
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                    finish();
                }else if (db.exc(qTrans)) {
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Harga keluar Lebih Besar Dari Saldo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getTotal(){
        double sald = 0.0;
        Cursor c = db.sq(Query.select("tbltransaksi"));
        if (c.getCount()==0){
            saldo= String.valueOf(0);
            sald = Function.strToDouble(saldo);
        } else {
            c.moveToLast();
            saldo=Function.getString(c, "saldo");
            sald = Function.strToDouble(saldo);
        }
        Function.setText(v, R.id.tvSaldo, "Rp. "+Function.removeE(sald));
    }
}

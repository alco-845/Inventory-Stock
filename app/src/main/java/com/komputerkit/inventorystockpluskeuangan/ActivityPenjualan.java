package com.komputerkit.inventorystockpluskeuangan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityPenjualan extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    Spinner spSatuan;
    EditText edtNamaPelanggan;
    ImageButton btnTglJual,btnCariPelanggan,btnCariBarang;
    TextView tvSatuan;
    View v;
    Database db;

    int year, month, day ;
    Calendar calendar ;
    Config config, temp;

    SharedPreferences getPrefs ;
    String faktur="00000000",deviceid,plgn,idjual;
    TextInputEditText pel;
    int tIdsatuan,tIdpelanggan,tIdbarang,tIdkategori,tJumlah=0, isikeranjang=0;
    String tnPelanggan,tnAlamatPel,tnTelpPel,tnBarang="",tStok="",tKategori="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Barang Keluar",getSupportActionBar());

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        deviceid = Function.getDecrypt(getPrefs.getString("deviceid","")) ;

        temp = new Config(getSharedPreferences("temp",this.MODE_PRIVATE));
        config = new Config(getSharedPreferences("config", this.MODE_PRIVATE));
        db=new Database(this);
        v = this.findViewById(android.R.id.content);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        cek();
        loadCart();
        btnCari();
        setEdit(tnBarang);

        pel = (TextInputEditText) findViewById(R.id.edtNamaPelanggan);

        String date_n=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Function.setText(v, R.id.edtTglJual,date_n);
        btnTglJual= (ImageButton)findViewById(R.id.ibtnTglJual);
        btnTglJual.setOnClickListener(new View.OnClickListener() {
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

    private void getFaktur(){
        List<Integer> idjual = new ArrayList<Integer>();
        String q="SELECT idjual FROM tbljual";
        Cursor c = db.sq(q);
        if (c.moveToNext()){
            do {
                idjual.add(c.getInt(0));
            }while (c.moveToNext());
        }
        String tempFaktur="";
        int IdFaktur=0;
        if (c.getCount()==0){
            tempFaktur=faktur.substring(0,faktur.length()-1)+"1";
        }else {
            IdFaktur = idjual.get(c.getCount()-1)+1;
            tempFaktur = faktur.substring(0,faktur.length()-String.valueOf(IdFaktur).length())+String.valueOf(IdFaktur);
        }
        Function.setText(v,R.id.edtNomorFaktur,tempFaktur);
    }

    public void cek(){
        Cursor c = db.sq(Query.selectwhere("qcartjual")+Query.sWhere("status", "0"));
        if (c.getCount()>0){
            c.moveToLast();
            String faktur = Function.getString(c, "fakturjual");
            Function.setText(v, R.id.edtNomorFaktur, faktur);
        } else {
            getFaktur();
        }
    }

    private void setEdit(String barang){
        plgn = temp.getCustom("idpelanggan","") ;

        if (TextUtils.isEmpty(plgn)) {
            temp.setCustom("idpelanggan", "0");
            plgn = "0";
            Function.setText(v, R.id.edtNamaPelanggan, plgn);
        }

        if (!TextUtils.isEmpty(plgn)){
            getPelanggan(Function.intToStr(tIdpelanggan));
        } else {
            Function.setText(v, R.id.edtNamaPelanggan, "");
        }

            Function.setText(v,R.id.edtNamaBarang,barang) ;
    }

    private void btnCari(){
        btnCariPelanggan=(ImageButton)findViewById(R.id.ibtnNamaPelanggan);
        btnCariPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(ActivityPenjualan.this,ActivityPenjualanCari.class);
                i.putExtra("cari","pelanggan");
                startActivityForResult(i,1000);
            }
        });
        btnCariBarang=(ImageButton)findViewById(R.id.ibtnNamaBarang);
        btnCariBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(ActivityPenjualan.this,ActivityPenjualanCari.class);
                i.putExtra("cari","barang");
                startActivityForResult(i,2000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1000){
            tIdpelanggan=data.getIntExtra("idpelanggan",0);
            tnPelanggan=data.getStringExtra("pelanggan");
            tnAlamatPel=data.getStringExtra("alamatpel");
            tnTelpPel=data.getStringExtra("telppel");
            getPelanggan(Function.intToStr(tIdpelanggan));
        }else if (resultCode==2000){
            tIdbarang=data.getIntExtra("idbarang",0);
            tIdkategori=data.getIntExtra("idkategori",0);
            tIdsatuan=data.getIntExtra("idsatuan",0);
            tnBarang=data.getStringExtra("barang");
            tStok=data.getStringExtra("stok");
            getBarang(Function.intToStr(tIdbarang));
        }

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
            Function.setText(v, R.id.edtTglBeli, Function.setDatePickerNormal(thn,bln+1,day)) ;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        tJumlah=0;
        Function.setText(v,R.id.edtJumlah,"0");
        setEdit(tnBarang);
    }

    private void keluar(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Anda yakin ingin keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ActivityPenjualan.this, ActivityTransaksi.class) ;
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

    public void insertTransaksi(View view) {
        String eFaktur = Function.getText(v,R.id.edtNomorFaktur);
        String eTglT = Function.getText(v,R.id.edtTglJual);
        String eNPelanggan = Function.getText(v,R.id.edtNamaPelanggan);
        String eNBarang = Function.getText(v,R.id.edtNamaBarang);
        String eJumlah = Function.getText(v,R.id.edtJumlah);
        if (TextUtils.isEmpty(eNBarang) || TextUtils.isEmpty(eNPelanggan) || Function.strToDouble(eJumlah)==0) {
            Toast.makeText(this, "Masukkan data dengan benar", Toast.LENGTH_SHORT).show();
        }else {
            String idPelanggan=String.valueOf(tIdpelanggan);
            String idBarang=String.valueOf(tIdbarang);
            Integer idjual = Integer.valueOf(eFaktur);
            String qOrderD,qOrder ;
            String[] detail ={
                    idBarang,
                    String.valueOf(idjual),
                    eJumlah
            };
            String[] simpan = {
                    String.valueOf(idjual),
                    eFaktur,
                    convertDate(eTglT),
                    idPelanggan,
                    idBarang
            } ;
            String q = Query.selectwhere("tbljual")+Query.sWhere("fakturjual",eFaktur);
            Cursor c = db.sq(q);
            if (c.getCount()==0){
                qOrder=Query.splitParam("INSERT INTO tbljual (idjual,fakturjual,tgljual,idpelanggan,idbarang) VALUES (?,?,?,?,?)",simpan);
                qOrderD=Query.splitParam("INSERT INTO tbljualdetail (idbarang,idjual,jumlahjual) VALUES (?,?,?)",detail);
            }else {
//                qOrder=Query.splitParam("UPDATE INTO tbljual (idjual,tgljual,idpelanggan,idbarang) VALUES (?,?,?,?)",simpan);
                qOrder="UPDATE tbljual SET " +
                        "tgljual=" +convertDate(eTglT)+","+
                        "idpelanggan=" +idPelanggan+","+
                        "idbarang=" +idBarang+
                        " WHERE idjual=" +String.valueOf(idjual);
                qOrderD=Query.splitParam("INSERT INTO tbljualdetail (idbarang,idjual,jumlahjual) VALUES (?,?,?)",detail);
            }

            Cursor cursor = db.sq(Query.selectwhere("qbarang") + Query.sWhere("idbarang", idBarang));
            cursor.moveToNext();
            String stok = Function.getString(cursor, "stok");
            if(Function.strToDouble(stok) >= Function.strToDouble(Function.getText(v,R.id.edtJumlah))){
                if (db.exc(qOrder) && db.exc(qOrderD)){
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                    loadCart();
                    clearText();
                }else if (db.exc(qOrderD)) {
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                    loadCart();
                    clearText();
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Stok Tidak Cukup untuk Pemesanan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getBarang(String idbarang){
        String q = Query.selectwhere("tblbarang") + Query.sWhere("idbarang", idbarang) ;
        Cursor c = db.sq(q) ;
        c.moveToNext() ;

        Function.setText(v,R.id.edtNamaBarang,Function.getString(c, "barang")) ;
    }

    public void getPelanggan(String idpelanggan){
        String q = Query.selectwhere("tblpelanggan") + Query.sWhere("idpelanggan", idpelanggan) ;
        Cursor c = db.sq(q) ;
        c.moveToNext() ;
        Function.setText(v,R.id.edtNamaPelanggan,Function.getString(c, "pelanggan")) ;
    }

    public void loadCart(){
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.recTransaksi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ArrayList arrayList = new ArrayList();
        RecyclerView.Adapter adapter=new AdapterTransaksi(this,arrayList);
        recyclerView.setAdapter(adapter);

        String tempFaktur=Function.getText(v,R.id.edtNomorFaktur);

        String q=Query.selectwhere("qcartjual")+Query.sWhere("fakturjual",tempFaktur);
        Cursor c = db.sq(q);
        if (c.getCount()>0){
            while (c.moveToNext()){
                String campur=Function.getString(c,"idjualdetail")+"__"+
                        Function.getString(c, "pelanggan") + "__" +
                        Function.getString(c, "barang") + "__" +
                        Function.getString(c, "jumlahjual") + "__" +
                        Function.getString(c, "stok") + "__" +
                        Function.getString(c, "idjual");
                arrayList.add(campur);
            }
        }else{

        }
        adapter.notifyDataSetChanged();

    }
    private void clearText(){
        Function.setText(v,R.id.edtNamaBarang,"");
        Function.setText(v,R.id.edtJumlah,"0");
    }

    public void simpan(View view) {
        String faktur=Function.getText(v,R.id.edtNomorFaktur);
        Cursor c= db.sq("SELECT * FROM tbljual WHERE fakturjual='"+faktur+"'");
        c.moveToNext();
        isikeranjang=c.getCount();
        if (isikeranjang==0){
            Toast.makeText(this, "Keranjang Masih Kosong", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create()
                    .setTitle("Anda Yakin?");
            builder.setMessage("Anda yakin ingin menyimpan pesanan ini ?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String faktur = Function.getText(v, R.id.edtNomorFaktur);
                            int stat = 1;
                            String q = "UPDATE tbljual SET status=" + stat + " WHERE idjual=" +faktur;
                            db.exc(q);
                            open();
                            tambahlimit();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

    }

    public void open() {
        final String faktur = Function.getText(v, R.id.edtNomorFaktur);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Anda ingin untuk cetak Struk ?");
        alertDialogBuilder.setPositiveButton("Cetak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //yes
                        Intent i = new Intent(ActivityPenjualan.this, ActivityCetak2.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("fakturjual", faktur);
                        startActivity(i);
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ActivityPenjualan.this, ActivityMenuUtama.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void ganti(View view) {
        String faktur=Function.getText(v,R.id.edtNomorFaktur);
        Cursor c= db.sq("SELECT * FROM qcartbeli WHERE fakturbeli='"+faktur+"'");
        isikeranjang=c.getCount();
        if (isikeranjang==0){
            Toast.makeText(this, "Keranjang Masih Kosong", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create()
                    .setTitle("Ganti Pesanan?");
            builder.setMessage("Anda yakin ingin menyimpan pesanan ini dan menggantinya dengan pesanan lain?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearText();
                            getFaktur();
                            loadCart();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    private void tambahlimit() {
        boolean status = ActivityTransaksi.status;
        if (!status) {
            int batas = Function.strToInt(config.getCustom("penjualan", "1")) + 1;
            config.setCustom("penjualan", Function.intToStr(batas));
        }
    }
}

class AdapterTransaksi extends RecyclerView.Adapter<AdapterTransaksi.TransaksiViewHolder>{
    private Context ctxAdapter;
    private ArrayList<String> data;

    public AdapterTransaksi(Context ctxAdapter, ArrayList<String> data) {
        this.ctxAdapter = ctxAdapter;
        this.data = data;
    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_penjualan,viewGroup,false);
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int i) {
        final String[] row=data.get(i).split("__");

        holder.barang.setText(row[2]);
        holder.jBarang.setText(Function.removeE(row[3])+" Item");

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Database db=new Database(ctxAdapter);
                AlertDialog.Builder builder=new AlertDialog.Builder(ctxAdapter);
                builder.create();
                builder.setMessage("Anda yakin ingin menghapusnya?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String q = "DELETE FROM tbljualdetail WHERE idjualdetail="+row[0];
                                if (db.exc(q)){
                                    Toast.makeText(ctxAdapter, "Berhasil", Toast.LENGTH_SHORT).show();
                                    ((ActivityPenjualan)ctxAdapter).loadCart();
                                }else {
                                    Toast.makeText(ctxAdapter, "Gagal", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class TransaksiViewHolder extends RecyclerView.ViewHolder{
        TextView jBarang,barang;
        ImageButton hapus;
        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            jBarang=(TextView)itemView.findViewById(R.id.jBarang);
            barang=(TextView)itemView.findViewById(R.id.tvNamaBarang);
            hapus=(ImageButton)itemView.findViewById(R.id.ibtnHapus);
        }
    }
}

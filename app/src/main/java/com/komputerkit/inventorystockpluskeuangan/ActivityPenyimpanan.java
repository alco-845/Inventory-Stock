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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class ActivityPenyimpanan extends AppCompatActivity {

    Toolbar appbar;
    Button btnSimpan;
    Spinner spSatuan;
    ImageButton btnTglBeli,btnCariSupplier,btnCariBarang;
    TextView tvSatuan;
    View v;
    Database db;
    Config config,temp;

    int year, month, day ;
    Calendar calendar ;

    SharedPreferences getPrefs ;
    String faktur="00000000", Satuan, deviceid, supp;
    int tIdsatuan,tIdSupplier,tIdbarang,tIdkategori,tJumlah=0, isikeranjang=0;
    String tnBarang="",tStok="",tKategori="",tSupplier="",tAlamatSupp,tTelpSupp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyimpanan);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Barang Masuk",getSupportActionBar());

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

        String date_n=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Function.setText(v, R.id.edtTglBeli,date_n);
        btnTglBeli = (ImageButton)findViewById(R.id.ibtnTglBeli);
        btnTglBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(1);
            }
        });

        final List<String> getIdSat = db.getIdSatuan();
        spSatuan=(Spinner)findViewById(R.id.spSatuan);
        getSatuanData();
        spSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Satuan = Function.intToStr(spSatuan.getSelectedItemPosition());
                if (tIdbarang==0){
                    Function.setText(v, R.id.edtJumlah,"0");
                }else{
                    if (position==0){

                        Cursor c= db.sq(Query.selectwhere("tblbarang")+"idbarang="+Function.intToStr(tIdbarang));
                        c.moveToNext();
                        String idSatuan = Function.getString(c, "idsatuan");
                        Cursor cc= db.sq(Query.selectwhere("tblsatuan")+"idsatuan="+idSatuan);
                        cc.moveToNext();
                        Function.setText(v, R.id.edtJumlah, Function.getString(cc, "nilai"));

                        setEdit(tnBarang);

                    }else{
                        Function.setText(v, R.id.edtJumlah,"1");
                        setEdit(tnBarang);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        List<Integer> idbeli = new ArrayList<Integer>();
        String q="SELECT idbeli FROM tblbeli";
        Cursor c = db.sq(q);
        if (c.moveToNext()){
            do {
                idbeli.add(c.getInt(0));
            }while (c.moveToNext());
        }
        String tempFaktur="";
        int IdFaktur=0;
        if (c.getCount()==0){
            tempFaktur=faktur.substring(0,faktur.length()-1)+"1";
        }else {
            IdFaktur = idbeli.get(c.getCount()-1)+1;
            tempFaktur = faktur.substring(0,faktur.length()-String.valueOf(IdFaktur).length())+String.valueOf(IdFaktur);
        }
        Function.setText(v,R.id.edtNomorFaktur,tempFaktur);
    }

    public void cek(){
        Cursor c = db.sq(Query.selectwhere("qcartbeli")+Query.sWhere("status", "0"));
        if (c.getCount()>0){
            c.moveToLast();
            String faktur = Function.getString(c, "fakturbeli");
            Function.setText(v, R.id.edtNomorFaktur, faktur);
        } else {
            getFaktur();
        }
    }

    private void setEdit(String barang){
        supp = temp.getCustom("idsupplier","") ;

        if (TextUtils.isEmpty(supp)) {
            temp.setCustom("idsupplier", "0");
            supp = "0";
            Function.setText(v, R.id.edtNamaSupplier, supp);
        }

        if (!TextUtils.isEmpty(supp)){
            getSupplier(Function.intToStr(tIdSupplier));
        } else {
            Function.setText(v, R.id.edtNamaSupplier, "");
        }

        Function.setText(v,R.id.edtNamaBarang,barang);
    }

    private void btnCari(){
        btnCariSupplier=(ImageButton)findViewById(R.id.ibtnNamaSupplier);
        btnCariSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(ActivityPenyimpanan.this,ActivityPenjualanCari.class);
                i.putExtra("cari","supplier");
                startActivityForResult(i,3000);
            }
        });
        btnCariBarang=(ImageButton)findViewById(R.id.ibtnNamaBarang);
        btnCariBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(ActivityPenyimpanan.this,ActivityPenjualanCari.class);
                i.putExtra("cari","barang");
                startActivityForResult(i,2000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==3000){
            tIdSupplier=data.getIntExtra("idsupplier",0);
            tSupplier=data.getStringExtra("supplier");
            tAlamatSupp=data.getStringExtra("alamatsupp");
            tTelpSupp=data.getStringExtra("telpsupp");
            getSupplier(Function.intToStr(tIdSupplier));
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
        getSatuanData();
    }

    private void keluar(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Anda yakin ingin keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ActivityPenyimpanan.this, ActivityTransaksi.class) ;
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
        String eTglT = Function.getText(v,R.id.edtTglBeli);
        String eNDistributor = Function.getText(v,R.id.edtNamaSupplier);
        String eNBarang = Function.getText(v,R.id.edtNamaBarang);
        String eHarga = Function.getText(v,R.id.edtHargaBarang);
        String eJumlah = Function.getText(v,R.id.edtJumlah);
        if (TextUtils.isEmpty(eFaktur) || TextUtils.isEmpty(eNBarang) || TextUtils.isEmpty(eNDistributor) || Function.strToDouble(eHarga)==0 || Function.strToDouble(eJumlah)==0) {
            Toast.makeText(this, "Masukkan data dengan benar", Toast.LENGTH_SHORT).show();
        }else {
            String idSupplier=String.valueOf(tIdSupplier);
            String idBarang=String.valueOf(tIdbarang);
            Integer idbeli = Integer.valueOf(eFaktur);
            String harga="";
            String qOrderD,qOrder ;
            String[] detail ={
                    idBarang,
                    String.valueOf(idbeli),
                    Satuan,
                    eHarga,
                    eJumlah
            };
            String[] simpan = {
                    String.valueOf(idbeli),
                    eFaktur,
                    Function.removeE(harga),
                    convertDate(eTglT),
                    idSupplier,
                    idBarang
            } ;
            String q = Query.selectwhere("tblbeli")+Query.sWhere("fakturbeli",eFaktur);
            Cursor c = db.sq(q);
            if (c.getCount()==0){
                qOrder=Query.splitParam("INSERT INTO tblbeli (idbeli,fakturbeli,total,tglbeli,idsupplier,idbarang) VALUES (?,?,?,?,?,?)",simpan);
                qOrderD=Query.splitParam("INSERT INTO tblbelidetail (idbarang,idbeli,satuanbeli,hargabeli,jumlah) VALUES (?,?,?,?,?)",detail);
            }else {
//                qOrder=Query.splitParam("UPDATE INTO tblbeli (idbeli,fakturbeli,total,tglbeli,idsupplier,idbarang) VALUES (?,?,?,?,?,?)",simpan);
                qOrder="UPDATE tblbeli SET " +
                        "tglbeli=" +convertDate(eTglT)+","+
                        "idsupplier=" +idSupplier+","+
                        "idbarang=" +idBarang+
                        " WHERE idbeli=" +String.valueOf(idbeli);
//                qOrder="UPDATE tblbeli SET " + "tglbeli=" +convertDate(eTglT)+","+ "idsupplier=" +idSupplier+","+ "idbarang=" +idBarang+ " WHERE idbeli=" +String.valueOf(idbeli);
                qOrderD=Query.splitParam("INSERT INTO tblbelidetail (idbarang,idbeli,satuanbeli,hargabeli,jumlah) VALUES (?,?,?,?,?)",detail);
            }

            String faktur = ("SELECT fakturbeli FROM tblbeli WHERE idbarang="+tIdbarang);
            if (Function.strToDouble(eFaktur) <= Function.strToDouble(faktur)){
                Toast.makeText(this, "Faktur", Toast.LENGTH_SHORT).show();
            } else {
            Cursor cursor = db.sq(Query.selectwhere("qbarang") + Query.sWhere("idbarang", idBarang));
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                Spinner sp = findViewById(R.id.spSatuan);
                Double kurang;
                if (sp.getSelectedItemPosition() == 1) {
                    kurang=Function.strToDouble(Function.getText(v, R.id.edtJumlah));
                } else {
                    Double nilai = Function.strToDouble(Function.getString(cursor, "nilai"));
                    kurang = Function.strToDouble(Function.getText(v, R.id.edtJumlah))/nilai;
                }

                String total=Function.doubleToStr(kurang);
                    if (db.exc(qOrder) && db.exc(qOrderD)){
                        Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                        db.exc("UPDATE tblbarang SET stok=stok+"+ total + " WHERE idbarang =" + idBarang);
                        loadCart();
                        clearText();
                    }else if (db.exc(qOrderD)) {
                        Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                        db.exc("UPDATE tblbarang SET stok=stok+"+ total + " WHERE idbarang =" + idBarang);
                        loadCart();
                        clearText();
                    } else {
                        Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void getBarang(String idbarang){
        String q = Query.selectwhere("tblbarang") + Query.sWhere("idbarang", idbarang) ;
        Cursor c = db.sq(q) ;
        c.moveToNext() ;

        Function.setText(v,R.id.edtNamaBarang,Function.getString(c, "barang")) ;
    }

    public void getSupplier(String idsupplier){
        String q = Query.selectwhere("tblsupplier") + Query.sWhere("idsupplier", idsupplier) ;
        Cursor c = db.sq(q) ;
        c.moveToNext() ;
        Function.setText(v,R.id.edtNamaSupplier,Function.getString(c, "supplier")) ;
    }

    private void getSatuanData(){
        Database db = new Database(this);

        ArrayList arrayList = new ArrayList();
        arrayList.add("Satuan");
        List<String> labels=arrayList;
        if (tIdbarang == 0) {
            labels= arrayList;
        }else {
            labels=db.getSatuanBarang(Function.intToStr(tIdbarang));
        }

        ArrayAdapter<String> data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,labels);
        data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSatuan.setAdapter(data);
    }

    public void loadCart(){
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.recPenyimpanan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ArrayList arrayList = new ArrayList();
        RecyclerView.Adapter adapter=new AdapterPenyimpanan(this,arrayList);
        recyclerView.setAdapter(adapter);

        String tempFaktur=Function.getText(v,R.id.edtNomorFaktur);
//        String idbeli = getIntent().getStringExtra("idbeli");

        String q=Query.selectwhere("qcartbeli")+Query.sWhere("fakturbeli",tempFaktur);
        Cursor c = db.sq(q);
        if (c.getCount()>0){
            while (c.moveToNext()){
                String campur=Function.getString(c,"idbelidetail")+"__"+
                        Function.getString(c, "supplier") + "__" +
                        Function.getString(c, "jumlah") + "__" +
                        Function.getString(c, "hargabeli") + "__" +
                        Function.getString(c, "barang") + "__" +
                        Function.getString(c, "satuanbesar") + "__" +
                        Function.getString(c, "nilai") + "__" +
                        Function.getString(c, "stok") + "__" +
                        Function.getString(c, "satuanbeli") + "__" +
                        Function.getString(c, "satuankecil") + "__" +
                        Function.getString(c, "idbarang:1");
                arrayList.add(campur);
            }
        }else{

        }
        adapter.notifyDataSetChanged();

    }
    private void clearText(){
        Function.setText(v,R.id.edtNamaBarang,"");
        Function.setText(v,R.id.edtHargaBarang,"");
        Function.setText(v,R.id.edtJumlah,"0");
    }

    public void simpan(View view) {
        String faktur=Function.getText(v,R.id.edtNomorFaktur);
        Cursor c= db.sq("SELECT * FROM qcartbeli WHERE fakturbeli='"+faktur+"'");
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
                            String q = "UPDATE tblbeli SET status=" + stat + " WHERE idbeli=" +faktur;
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
        alertDialogBuilder.setPositiveButton("Cetak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //yes
                        Intent i = new Intent(ActivityPenyimpanan.this, ActivityCetak.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("fakturbeli", faktur);
                        startActivity(i);
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ActivityPenyimpanan.this, ActivityMenuUtama.class);
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

    private void tambahlimit(){
        boolean status = ActivityTransaksi.status;
        if (!status){
            int batas = Function.strToInt(config.getCustom("penyimpanan", "1"))+1;
            config.setCustom("penyimpanan", Function.intToStr(batas));
        }
    }
}

class AdapterPenyimpanan extends RecyclerView.Adapter<AdapterPenyimpanan.PenyimpananViewHolder>{
    private Context ctxAdapter;
    private ArrayList<String> data;

    public AdapterPenyimpanan(Context ctxAdapter, ArrayList<String> data) {
        this.ctxAdapter = ctxAdapter;
        this.data = data;
    }

    @NonNull
    @Override
    public PenyimpananViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_penyimpanan,viewGroup,false);
        return new PenyimpananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PenyimpananViewHolder holder, int i) {
        final String[] row=data.get(i).split("__");
        String sat;

        holder.sBarang.setText(Function.removeE(row[2])+"  /");
        holder.barang.setText(row[4]);
        holder.harga.setText("x  "+Function.removeE(row[3]));

        if (row[8].equals("1")){
            sat = row[5];
        }else {
            sat = row[9];
        }

        holder.satuan.setText(sat);

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
                                String q = "DELETE FROM tblbelidetail WHERE idbelidetail="+row[0];
                                double kurang,nilai;
                                String total;
                                kurang = Function.strToDouble(row[2]);
                                if (row[8].equals("1")){

                                    total=Function.doubleToStr(kurang);
                                } else {

                                    nilai = Function.strToDouble(row[6]);
                                    kurang/=nilai;
                                    total=Function.doubleToStr(kurang);
                                }
                                String w = ("UPDATE tblbarang SET stok=stok-"+ total + " WHERE idbarang =" + row[10]);
                                if (db.exc(q) && db.exc(w)){
                                    Toast.makeText(ctxAdapter, "Berhasil", Toast.LENGTH_SHORT).show();
                                    ((ActivityPenyimpanan)ctxAdapter).loadCart();
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

    class PenyimpananViewHolder extends RecyclerView.ViewHolder{
        TextView sBarang,barang,harga,satuan;
        ImageButton hapus;
        public PenyimpananViewHolder(@NonNull View itemView) {
            super(itemView);
            sBarang=(TextView)itemView.findViewById(R.id.sBarang);
            barang=(TextView)itemView.findViewById(R.id.tvNamaBarang);
            harga=(TextView)itemView.findViewById(R.id.tvHarga);
            satuan=(TextView)itemView.findViewById(R.id.tvSatuan);
            hapus=(ImageButton)itemView.findViewById(R.id.ibtnHapus);
        }
    }
}

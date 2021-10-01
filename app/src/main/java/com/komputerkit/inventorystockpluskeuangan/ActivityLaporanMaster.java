package com.komputerkit.inventorystockpluskeuangan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityLaporanMaster extends AppCompatActivity {

    Toolbar appbar;
    View v ;
    Config config ;
    Database db ;
    ArrayList arrayList = new ArrayList() ;
    List<ActivityPelanggan.getterPelanggan> DaftarPelanggan;
    RecyclerView.Adapter adapter ;
    String Master,type ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_master);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        v = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config", this.MODE_PRIVATE));
        db = new Database(this);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Master = getIntent().getStringExtra("type");
        String title = "judul";

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        if (Master.equals("barang")) {
            title = ("Laporan Barang");
            adapter = new AdapterBarang(this, arrayList);
            recyclerView.setAdapter(adapter);
            getBarang("");
        } else if (Master.equals("distributor")) {
            title = ("Laporan Supplier");
            adapter = new AdapterSupplier(this, arrayList);
            recyclerView.setAdapter(adapter);
            getSupplier("");
        } else if (Master.equals("pelanggan")) {
            title = ("Laporan Pelanggan");
            adapter = new AdapterPelanggan(this, arrayList);
            recyclerView.setAdapter(adapter);
            getPelanggan("");
        }

        final EditText eCari = (EditText) findViewById(R.id.eCari);
        eCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                arrayList.clear();
                String a = eCari.getText().toString();
                if (Master.equals("barang")) {
                    getBarang(a);
                } else if (Master.equals("distributor")) {
                    getSupplier(a);
                } else if (Master.equals("pelanggan")) {
                    getPelanggan(a);
                }
            }
        });

        Function.btnBack(title, getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getSupplier(String a){
        String hasil = "" ;
        String tabel = "tblsupplier";
        if(TextUtils.isEmpty(a)){
            hasil = "SELECT * FROM tblsupplier WHERE idsupplier>0";
        } else {
            hasil = "SELECT * FROM tblsupplier WHERE idsupplier>0 AND supplier LIKE '%"+a+"%' ORDER BY supplier";
        }
        Cursor c = db.sq(hasil) ;
        if(c.getCount() > 0){
            Function.setText(v,R.id.tTotal,"Jumlah Data : "+Function.intToStr(c.getCount())) ;
            while(c.moveToNext()){
                String nama = Function.getString(c,"supplier");
                String telp = Function.getString(c,"telpsupp");
                String alamat = Function.getString(c,"alamatsupp");

                String campur = nama +"__"+alamat+"__"+telp ;
                arrayList.add(campur);
            }
        } else {
            Function.setText(v,R.id.tTotal,"Jumlah Data : 0") ;
        }
        adapter.notifyDataSetChanged();
    }

    public void getPelanggan(String a){
        String hasil = "" ;
        String tabel = "tblpelanggan";
        if(TextUtils.isEmpty(a)){
            hasil = "SELECT * FROM tblpelanggan WHERE idpelanggan>0";
        } else {
            hasil = "SELECT * FROM tblpelanggan WHERE idpelanggan>0 AND pelanggan LIKE '%"+a+"%' ORDER BY pelanggan";
        }
        Cursor c = db.sq(hasil) ;
        if(c.getCount() > 0){
            Function.setText(v,R.id.tTotal,"Jumlah Data : "+Function.intToStr(c.getCount())) ;
            while(c.moveToNext()){
                String nama = Function.getString(c,"pelanggan");
                String telp = Function.getString(c,"telppel");
                String alamat = Function.getString(c,"alamatpel");

                String campur = nama +"__"+alamat+"__"+telp ;
                arrayList.add(campur);
            }
        } else {
            Function.setText(v,R.id.tTotal,"Jumlah Data : 0") ;
        }
        adapter.notifyDataSetChanged();
    }

    public void getBarang(String a){
        String hasil = "" ;
        String tabel = "qbarang" ;
        if(TextUtils.isEmpty(a)){
            hasil = Query.select(tabel) +Query.sOrderASC("barang")+" LIMIT 30"  ;
        } else {
            hasil = Query.selectwhere(tabel) +Query.sLike("barang",a) +Query.sOrderASC("barang");
        }
        Cursor c = db.sq(hasil);
        if(c.getCount() > 0){
            Function.setText(v,R.id.tTotal,"Jumlah Data : "+Function.intToStr(c.getCount())) ;
            while(c.moveToNext()){
                String barang= Function.getString(c,"barang") ;
                double stok = c.getDouble(c.getColumnIndex("stok")) ;
                String kategori = Function.getString(c, "kategori");
                String satuan = Function.getString(c, "satuanbesar");

                String campur = barang+"__"+Function.removeE(stok)+"__"+satuan+"__"+kategori+"__" ;
                arrayList.add(campur);
            }
        } else {
            Function.setText(v,R.id.tTotal,"Jumlah Data : 0") ;
        }
        adapter.notifyDataSetChanged();
    }

    public void export(View view){
        Intent i = new Intent(this, ActivityExportExcel.class) ;
        i.putExtra("type",Master) ;
        startActivity(i);
    }
}





class AdapterPelanggan extends RecyclerView.Adapter<AdapterPelanggan.ViewHolder> {
    private ArrayList<String> data;
    Context c;

    public AdapterPelanggan(Context a, ArrayList<String> kota) {
        this.data = kota;
        c = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_pelanggan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faktur, nma, jumlah, opt;

        public ViewHolder(View view) {
            super(view);

            nma = (TextView) view.findViewById(R.id.tBarang);
            faktur = (TextView) view.findViewById(R.id.tFaktur);
            jumlah = (TextView) view.findViewById(R.id.tTotal);
            opt = (TextView) view.findViewById(R.id.tvOpt);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final String[] row = data.get(i).split("__");
        final String notelp = row[1].substring(1);

        viewHolder.jumlah.setText("Alamat : "+row[2]);
        viewHolder.nma.setText("Nama : "+row[0]);
        viewHolder.faktur.setText("No. Telepon : "+row[1]);
    }

    class getterPelanggan {
        private int idPelanggan;
        private String pelanggan;
        private String alamat;
        private String notelp;

        public getterPelanggan(int idPelanggan, String pelanggan, String alamat, String notelp) {
            this.idPelanggan = idPelanggan;
            this.pelanggan = pelanggan;
            this.alamat = alamat;
            this.notelp = notelp;
        }

        public int getIdPelanggan() {
            return idPelanggan;
        }

        public String getPelanggan() {
            return pelanggan;
        }

        public String getAlamat() {
            return alamat;
        }

        public String getNoTelp() {
            return notelp;
        }
    }
}

class AdapterSupplier extends RecyclerView.Adapter<AdapterSupplier.ViewHolder> {
    private ArrayList<String> data;
    Context c;

    public AdapterSupplier(Context a, ArrayList<String> kota) {
        this.data = kota;
        c = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_supplier, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faktur, nma, jumlah;

        public ViewHolder(View view) {
            super(view);

            nma = (TextView) view.findViewById(R.id.namaSupplier);
            faktur = (TextView) view.findViewById(R.id.alamats);
            jumlah = (TextView) view.findViewById(R.id.notelp);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final String[] row = data.get(i).split("__");

        viewHolder.jumlah.setText("Alamat : "+row[2]);
        viewHolder.nma.setText("Nama : "+row[0]);
        viewHolder.faktur.setText("No. Telepon : "+row[1]);
    }

    class getterSupplier extends ActivitySupplier.getterSupplier {
        private int idSupplier;
        private String supplier;
        private String alamat;
        private String notelp;

        public getterSupplier(int idSupplier, String supplier, String alamat, String notelp) {
            this.idSupplier = idSupplier;
            this.supplier = supplier;
            this.alamat = alamat;
            this.notelp = notelp;
        }

        public int getIdSupplier() {
            return idSupplier;
        }

        public String getSupplier() {
            return supplier;
        }

        public String getAlamat() {
            return alamat;
        }

        public String getNoTelp() {
            return notelp;
        }
    }
}

class AdapterBarang extends RecyclerView.Adapter<AdapterBarang.ViewHolder> {
    private ArrayList<String> data;
    Context c;

    public AdapterBarang(Context a, ArrayList<String> kota) {
        this.data = kota;
        c = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_barang, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView barang,stok,kategori;

        public ViewHolder(View view) {
            super(view);

            barang = (TextView) view.findViewById(R.id.tBarang);
            stok = (TextView) view.findViewById(R.id.tStok);
            kategori = (TextView) view.findViewById(R.id.teKategori);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String[] row = data.get(i).split("__");

        viewHolder.barang.setText(row[0]);
        viewHolder.stok.setText("Stok : "+row[1]+" "+row[2]);
        viewHolder.kategori.setText("Kategori : "+row[3]);
    }
}

package com.komputerkit.inventorystockpluskeuangan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityLaporanPenjualan extends AppCompatActivity {

    Toolbar appbar;
    View v ;
    Config config ;
    Database db ;
    ArrayList arrayList = new ArrayList() ;
    String dari, ke, type ;
    Calendar calendar ;
    int year,month, day ;
    SharedPreferences getPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_penjualan);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        String title = "judul";

        v = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config",this.MODE_PRIVATE));
        db = new Database(this) ;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        type = getIntent().getStringExtra("type") ;

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        final EditText eCari = (EditText) findViewById(R.id.eCari) ;
        eCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(type.equals("penyimpanan")){
                    arrayList.clear();
                    loadList2(eCari.getText().toString());
                } else if(type.equals("penjualan")){
                    arrayList.clear();
                    loadList(eCari.getText().toString());
                }
            }
        });

        setText();
        if(type.equals("penyimpanan")){
            title = ("Laporan Barang Masuk") ;
            loadList2("");
        } else if(type.equals("penjualan")){
            title = ("Laporan Barang Keluar") ;
            loadList("");
        }
        Function.btnBack(title, getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void export(View view){
        Intent i = new Intent(this, ActivityExportExcel.class);
        i.putExtra("type",type) ;
        startActivity(i);
    }

    public void setText(){
        dari = Function.setDatePicker(year,month+1,day) ;
        ke = Function.setDatePicker(year,month+1,day) ;
        String now = Function.setDatePickerNormal(year,month+1,day) ;
        Function.setText(v,R.id.eKe,now) ;
        Function.setText(v,R.id.eDari,now) ;
    }
    public void loadList(String cari){
        arrayList.clear();
        String bayar = "" ;

        if (TextUtils.isEmpty(cari)){
            bayar = Query.selectwhere("qcartjual") + Query.sBetween("tgljual", dari, ke) + " LIMIT 30";
        } else {
            bayar = Query.selectwhere("qcartjual") + Query.sLike("pelanggan", cari) + " AND " + Query.sBetween("tgljual", dari, ke);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recUtang) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new AdapterLaporanPenjualan(this,arrayList) ;
        recyclerView.setAdapter(adapter);
        Cursor c = db.sq(bayar) ;
        if(c.getCount() > 0){
            Function.setText(v,R.id.tjumlah,"Jumlah Data : "+Function.intToStr(c.getCount())) ;
            while(c.moveToNext()){
                String pelanggan = Function.getString(c,"pelanggan") ;
                String kembali = Function.getString(c,"barang") ;
                String faktur = Function.getString(c,"fakturjual") ;
                String jumlah = Function.getString(c,"jumlahjual") ;
                String idjualdetail = Function.getString(c, "idjualdetail");
                String tgl = Function.getString(c, "tgljual");
                String campur = idjualdetail +"__"+faktur +"__"+pelanggan+"__"+kembali+"__"+Function.removeE(jumlah)+"__" +Function.dateToNormal(tgl);
                arrayList.add(campur);
            }
            Function.setText(v, R.id.tjumlah, "Jumlah Data : " + String.valueOf(c.getCount()));
        } else {
            Function.setText(v,R.id.tjumlah,"Jumlah Data : 0") ;
        }
        adapter.notifyDataSetChanged();
    }

    public void loadList2(String cari){
        arrayList.clear();
        String hasil= "" ;

        if (TextUtils.isEmpty(cari)){
            hasil = Query.selectwhere("qcartbeli") + Query.sBetween("tglbeli", dari, ke) + " LIMIT 30";
        } else {
            hasil = Query.selectwhere("qcartbeli") + Query.sLike("supplier", cari) + " AND " + Query.sBetween("tglbeli", dari, ke);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recUtang) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new AdapterLaporanPenjualan.AdapterLaporanPenyimpanan(this,arrayList) ;
        recyclerView.setAdapter(adapter);
        Cursor c = db.sq(hasil) ;
        if(c.getCount() > 0){
            Function.setText(v,R.id.tjumlah,"Jumlah Data : "+Function.intToStr(c.getCount())) ;
            while(c.moveToNext()){
                String pelanggan = Function.getString(c,"supplier") ;
                String kembali = Function.getString(c,"barang") ;
                String faktur = Function.getString(c,"fakturbeli") ;
                String jumlah = Function.getString(c,"jumlah") ;
                String harga = Function.getString(c,"hargabeli") ;
                String idbelidetail = Function.getString(c, "idbelidetail");
                String idbarang = Function.getString(c, "idbarang:1");
                String nilai = Function.getString(c,"nilai") ;
                String satuanjual = Function.getString(c,"satuanbeli") ;
                double total = Function.strToDouble(harga)*Function.strToDouble(jumlah) ;
                String tgl = Function.getString(c, "tglbeli");

                String campur = satuanjual+"__"+nilai+"__"+idbarang+"__"+idbelidetail+"__"+faktur +"__"+pelanggan+"__"+kembali+"__"+Function.removeE(jumlah)+"__"+Function.removeE(harga)+"__"+Function.removeE(total) +"__" +Function.dateToNormal(tgl);
                arrayList.add(campur);
            }
            Function.setText(v, R.id.tjumlah, "Jumlah Data : " + String.valueOf(c.getCount()));
        } else {
            Function.setText(v,R.id.tjumlah,"Jumlah Data : 0") ;
        }
        adapter.notifyDataSetChanged();
    }

    public void dateDari(View view){
        setDate(1);
    }
    public void dateKe(View view){
        setDate(2);
    }

    public void filtertgl(){
        String a = Function.getText(v, R.id.eCari);
        if(type.equals("penyimpanan")){
            loadList2(a);
        } else if(type.equals("penjualan")){
            loadList(a);
        }
    }

    //start date time picker
    public void setDate(int i) {
        showDialog(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this, edit1, year, month, day);
        } else if (id == 2) {
            return new DatePickerDialog(this, edit2, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener edit1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int thn, int bln, int day) {
            Function.setText(v, R.id.eDari, Function.setDatePickerNormal(thn, bln + 1, day));
            dari = Function.setDatePicker(thn, bln + 1, day);
            filtertgl();
        }
    };

    private DatePickerDialog.OnDateSetListener edit2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int thn, int bln, int day) {
            Function.setText(v, R.id.eKe, Function.setDatePickerNormal(thn, bln + 1, day));
            ke = Function.setDatePicker(thn, bln + 1, day);
            filtertgl();
        }
    };
    //end date time picker
}





class AdapterLaporanPenjualan extends RecyclerView.Adapter<AdapterLaporanPenjualan.ViewHolder> {
    private ArrayList<String> data;
    Context c;

    public AdapterLaporanPenjualan(Context a, ArrayList<String> kota) {
        this.data = kota;
        c = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_penjualan_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faktur, nma, jumlah, tanggal;
        ConstraintLayout print;

        public ViewHolder(View view) {
            super(view);

            nma = (TextView) view.findViewById(R.id.tHitung);
            tanggal = (TextView) view.findViewById(R.id.tTanggal);
            faktur = (TextView) view.findViewById(R.id.tNama);
            jumlah = (TextView) view.findViewById(R.id.tBarang);
            print = (ConstraintLayout) view.findViewById(R.id.wPrinter);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final String[] row = data.get(i).split("__");

        viewHolder.jumlah.setText(row[3] + "\n" + row[4]+" item");
        viewHolder.nma.setText(row[2]);
        viewHolder.tanggal.setText(row[5]);
        viewHolder.faktur.setText(row[1]);
        viewHolder.print.setTag(row[1]);
        viewHolder.print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ActivityCetak2.class);
                intent.putExtra("fakturjual", row[1]);
                c.startActivity(intent);
            }
        });
    }

    static class AdapterLaporanPenyimpanan extends RecyclerView.Adapter<AdapterLaporanPenyimpanan.ViewHolder> {
        private ArrayList<String> data;
        Context c;

        public AdapterLaporanPenyimpanan(Context a, ArrayList<String> kota) {
            this.data = kota;
            c = a;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_penyimpanan_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView faktur, nma, jumlah, tanggal;
            ConstraintLayout print;

            public ViewHolder(View view) {
                super(view);

                nma = (TextView) view.findViewById(R.id.tHitung);
                tanggal = (TextView) view.findViewById(R.id.etTanggal);
                faktur = (TextView) view.findViewById(R.id.tNama);
                jumlah = (TextView) view.findViewById(R.id.tBarang);
                print = (ConstraintLayout) view.findViewById(R.id.wPrinter);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            final String[] row = data.get(i).split("__");

            viewHolder.jumlah.setText(row[7]+" x "+row[8]+" = "+row[9]);
            viewHolder.nma.setText(row[4]);
            viewHolder.tanggal.setText(row[10]);
            viewHolder.faktur.setText(row[5]+"\n"+row[6]);
            viewHolder.print.setTag(row[4]);
            viewHolder.print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, ActivityCetak.class);
                    intent.putExtra("fakturbeli", row[4]);
                    c.startActivity(intent);
                }
            });
        }
    }
}

package com.komputerkit.inventorystockpluskeuangan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityLaporanKeuangan extends AppCompatActivity {

    Toolbar appbar;
    String type;
    View v;
    Config config;
    Database db;
    ArrayList arrayList = new ArrayList();
    String dari, ke;
    Calendar calendar;
    int year, month, day;

    SharedPreferences getPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keuangan);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        String title = "judul";

        v = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config", this.MODE_PRIVATE));
        db = new Database(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        type = getIntent().getStringExtra("type");

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

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
                getKeuangan(a);
            }
        });
        setText();
        List<String> categories = new ArrayList<>();
        if (type.equals("keuangan")) {
            title = ("Laporan Keuangan");
            getKeuangan("");
            categories = new ArrayList<String>();
            categories.add("Semua");
            categories.add("Pemasukan");
            categories.add("Pengeluaran");
        }

        Spinner spinner = (Spinner) findViewById(R.id.spKeuangan) ;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Function.getText(v, R.id.eCari);
                if (type.equals("keuangan")) {
                    getKeuangan(a);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        Function.btnBack(title, getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void export(View view) {
        Intent intent = new Intent(this, ActivityExportExcel.class);
        intent.putExtra("type", "keuangan");
        startActivity(intent);
    }

    public void setText() {
        dari = Function.setDatePicker(year, month + 1, day);
        ke = Function.setDatePicker(year, month + 1, day);
        String now = Function.setDatePickerNormal(year, month + 1, day);
        Function.setText(v, R.id.eKe, now);
        Function.setText(v, R.id.eDari, now);
    }

    public void clearText() {
        String saldo;
        Cursor c = db.sq("SELECT * FROM tbltransaksi");
        c.moveToNext();
        Function.setText(v, R.id.teJumlahData, "Jumlah Data : " + String.valueOf(c.getCount()));

        Cursor cur = db.sq(Query.select("tbltransaksi"));
        cur.moveToLast();

        if (c.getCount()==0){
            Function.setText(v, R.id.teValue, "Rp. 0");
        } else {
            saldo = Function.getString(cur, "saldo");
            String j = Function.removeE(saldo);
            Function.setText(v, R.id.teValue, "Rp. " + j);
        }
    }

    public void getKeuangan(String cari){
        arrayList.clear();
        String item = Function.getSpinnerItem(v,R.id.spKeuangan) ;
        String q="";
        if (item.equals("Semua")){
            q = Query.selectwhere("tbltransaksi") + Query.sLike("fakturtransaksi", cari) + " AND "+ Query.sBetween("tgltransaksi", dari, ke) + Query.sOrderASC("tgltransaksi");
        } else if (item.equals("Pemasukan")){
            q = Query.selectwhere("tbltransaksi") + Query.sWhere("status", "0") +" AND "+ Query.sLike("fakturtransaksi", cari) + " AND " + Query.sBetween("tgltransaksi", dari, ke) + Query.sOrderASC("tgltransaksi");
        } else if (item.equals("Pengeluaran")) {
            q = Query.selectwhere("tbltransaksi") + Query.sWhere("status", "1") +" AND "+ Query.sLike("fakturtransaksi", cari) + " AND " + Query.sBetween("tgltransaksi", dari, ke) + Query.sOrderASC("tgltransaksi");
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recKeuangan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new AdapterKeuangan(this, arrayList);
        recyclerView.setAdapter(adapter);
        Cursor c = db.sq(q);
        if (c.getCount() > 0){
            Function.setText(v,R.id.teJumlahData,"Jumlah Data : "+String.valueOf(c.getCount())) ;
            while (c.moveToNext()) {
                String tgl = Function.getString(c, "tgltransaksi");
                String notrans = Function.getString(c, "notransaksi");
                String faktur = Function.getString(c, "fakturtransaksi");
                String ket = Function.getString(c, "keterangantransaksi");
                String hmasuk = Function.getString(c, "masuk");
                String hkeluar = Function.getString(c, "keluar");
                String stat = Function.getString(c, "status");

                String campur = notrans+"__"+Function.dateToNormal(tgl)+"__"+faktur +"__"+"Harga Masuk : "+Function.removeE(hmasuk)+"__"+"Harga Keluar : "+Function.removeE(hkeluar)+"__"+stat+"__"+ket;
                arrayList.add(campur);
            }
            Cursor cur = db.sq(Query.select("tbltransaksi"));
            cur.moveToLast();
            String saldo = Function.getString(cur, "saldo");
            String j = Function.removeE(saldo);
            Function.setText(v, R.id.teValue, "Rp. "+j);
        } else {
            Function.setText(v, R.id.teValue, "Rp. 0");
            Function.setText(v,R.id.teJumlahData, "Jumlah Data : 0");
        }
        adapter.notifyDataSetChanged();
    }

    public void dateDari(View view) {
        setDate(1);
    }

    public void dateKe(View view) {
        setDate(2);
    }

    public void filtertgl() {
        String a = Function.getText(v, R.id.eCari);
        if (type.equals("keuangan")) {
            getKeuangan(a);
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

class AdapterKeuangan extends RecyclerView.Adapter<AdapterKeuangan.ViewHolder> {
    private ArrayList<String> data;
    Context c;

    public AdapterKeuangan(Context a, ArrayList<String> kota) {
        this.data = kota;
        c = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_laporan_keuangan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tanggal,harga,notrans, faktur, ket;
        ImageView hapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            harga = (TextView) itemView.findViewById(R.id.teHarga);
            tanggal = (TextView) itemView.findViewById(R.id.teTanggal);
            notrans = (TextView) itemView.findViewById(R.id.teNoTrans);
            faktur = (TextView) itemView.findViewById(R.id.teFaktur);
            ket = (TextView) itemView.findViewById(R.id.teKet);
            hapus = (ImageView) itemView.findViewById(R.id.ivDelete);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final String[] row = data.get(i).split("__");
        final Database db = new Database(c);
        String stat;

        viewHolder.notrans.setText("No. Transaksi : "+row[0]);
        viewHolder.tanggal.setText(row[1]);
        viewHolder.faktur.setText(row[2]);
        if (row[5].equals("0")){
            stat = row[3];
        } else {
            stat = row[4];
        }
        viewHolder.harga.setText(stat);
        viewHolder.ket.setText("Keterangan : "+row[6]);
        Cursor cur = db.sq("SELECT * FROM tbltransaksi");
        String last="0";
        if (cur.getCount()>0) {
            cur.moveToLast();
            last = Function.getString(cur, "notransaksi");
            if (row[0].equals(last)){
                viewHolder.hapus.setVisibility(View.VISIBLE);
            }else {
                viewHolder.hapus.setVisibility(View.GONE);
            }
        } else {
            viewHolder.hapus.setVisibility(View.VISIBLE);
        }
        viewHolder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.create();
                builder.setMessage("Apakah Anda Yakin Ingin Menghapusnya?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String q = "DELETE FROM tbltransaksi WHERE notransaksi=" + row[0];
                                if (db.exc(q)) {
                                    Toast.makeText(c, "Berhasil", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    ((ActivityLaporanKeuangan) c).clearText();
                                    data.remove(i);
                                } else {
                                    Toast.makeText(c, "Gagal", Toast.LENGTH_SHORT).show();
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
}

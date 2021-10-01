package com.komputerkit.inventorystockpluskeuangan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
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

public class ActivityPelanggan extends AppCompatActivity {

    Toolbar appbar;
    RecyclerView listpelanggan;
    AdapterListPelanggan adapter;
    List<getterPelanggan> DaftarPelanggan;
    View v;
    ArrayList arrayList = new ArrayList() ;
    String type;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Pelanggan",getSupportActionBar());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new Database(this);
        v = this.findViewById(android.R.id.content);
        type = getIntent().getStringExtra("type") ;

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
                String a = eCari.getText().toString() ;
                getPelanggan(a);
            }
        });
    }

    public void getPelanggan(String keyword){
        DaftarPelanggan = new ArrayList<>();
        listpelanggan = (RecyclerView) findViewById(R.id.listpelanggan);
        listpelanggan.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listpelanggan.setLayoutManager(layoutManager);
        adapter = new AdapterListPelanggan(this,DaftarPelanggan);
        listpelanggan.setAdapter(adapter);
        String cari = new String();
        String q;
        Cursor c;
        if(TextUtils.isEmpty(cari)){
            c = db.sq(Query.select("tblpelanggan")+Query.sOrderASC("pelanggan"));
        } else {
            c = db.sq(Query.selectwhere("tblpelanggan") + Query.sLike("pelanggan",cari)+Query.sOrderASC("pelanggan")) ;
        }
        if(c.getCount() > 0){
            while(c.moveToNext()){
                String pelanggan = Function.getString(c,"pelanggan") ;
                String idpelanggan = Function.getString(c,"idpelanggan") ;
                arrayList.add(idpelanggan+"__"+pelanggan);
            }
        }
        if (TextUtils.isEmpty(keyword)){
            q="SELECT * FROM tblpelanggan WHERE idpelanggan>0";
        }else {
            q="SELECT * FROM tblpelanggan WHERE pelanggan LIKE '%"+keyword+"%' AND idpelanggan>0 ORDER BY pelanggan";
        }
        Cursor cur=db.sq(q);
        while(cur.moveToNext()){
            DaftarPelanggan.add(new getterPelanggan(
                    cur.getInt(cur.getColumnIndex("idpelanggan")),
                    cur.getString(cur.getColumnIndex("pelanggan")),
                    cur.getString(cur.getColumnIndex("alamatpel")),
                    cur.getString(cur.getColumnIndex("telppel"))
            ));
        }
        adapter.notifyDataSetChanged();
    }

    public void tambah(View view){
        Intent intent = new Intent(this, ActivityTambahPelanggan.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPelanggan("");
    }

    class AdapterListPelanggan extends RecyclerView.Adapter<AdapterListPelanggan.PelangganViewHolder>{
        private Context ctxAdapter;
        private List<getterPelanggan> data;

        public AdapterListPelanggan(Context ctx, List<getterPelanggan> viewData) {
            this.ctxAdapter = ctx;
            this.data = viewData;
        }

        @NonNull
        @Override
        public PelangganViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ctxAdapter);
            View view = inflater.inflate(R.layout.list_pelanggan,viewGroup,false);
            return new PelangganViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PelangganViewHolder holder, final int i) {
            final getterPelanggan getter = data.get(i);
            holder.pelanggan.setText(getter.getPelanggan());
            holder.alamat.setText(getter.getAlamat());
            holder.notelp.setText(String.valueOf(getter.getNoTelp()));
            holder.opt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ctxAdapter,holder.opt);
                    popupMenu.inflate(R.menu.option_item);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_update:
                                    Intent intent = new Intent(ctxAdapter,ActivityTambahPelanggan.class);
                                    intent.putExtra("idpelanggan",getter.getIdPelanggan());
                                    intent.putExtra("pelanggan",getter.getPelanggan());
                                    intent.putExtra("alamatpel",getter.getAlamat());
                                    intent.putExtra("telppel",String.valueOf(getter.getNoTelp()));
                                    ctxAdapter.startActivity(intent);
                                    break;

                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctxAdapter);
                                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Database db = new Database(ctxAdapter);
                                            if (db.deletePelanggan(getter.getIdPelanggan())){
                                                data.remove(i);
                                                notifyDataSetChanged();
                                                Toast.makeText(ctxAdapter, "Delete pelanggan "+getter.getPelanggan()+" berhasil", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(ctxAdapter, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.setTitle("Hapus "+getter.getPelanggan())
                                            .setMessage("Anda yakin ingin menghapus "+getter.getPelanggan()+" dari data pelanggan");
                                    builder.show();
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class PelangganViewHolder extends RecyclerView.ViewHolder{
            TextView pelanggan, alamat, notelp, opt;
            public PelangganViewHolder(@NonNull View itemView) {
                super(itemView);
                pelanggan=(TextView)itemView.findViewById(R.id.tvPelanggan);
                alamat=(TextView)itemView.findViewById(R.id.tvAlamat);
                notelp=(TextView)itemView.findViewById(R.id.tvNoTelp);
                opt=(TextView)itemView.findViewById(R.id.tvOpt);
            }
        }
    }
    static class getterPelanggan {
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

        getterPelanggan() {
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

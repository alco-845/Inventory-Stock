package com.komputerkit.inventorystockpluskeuangan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class ActivityKategori extends AppCompatActivity {

    Toolbar appbar;
    RecyclerView listkategori;
    AdapterListKategori adapter;
    List<getterKategori> DaftarKategori;
    View v;
    ArrayList arrayList = new ArrayList() ;
    String type;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Kategori",getSupportActionBar());
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
                getKategori(a);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getKategori(String keyword){
        DaftarKategori = new ArrayList<>();
        listkategori = (RecyclerView) findViewById(R.id.listKategori);
        listkategori.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listkategori.setLayoutManager(layoutManager);
        adapter = new AdapterListKategori(this,DaftarKategori);
        listkategori.setAdapter(adapter);
        String cari = new String();
        String q;
        Cursor c ;
        if(TextUtils.isEmpty(cari)){
            c = db.sq(Query.select("tblkategori")+Query.sOrderASC("kategori"));
        } else {
            c = db.sq(Query.selectwhere("tblkategori") + Query.sLike("kategori",cari)+Query.sOrderASC("kategori")) ;
        }
        if(c.getCount() > 0){
            while(c.moveToNext()){
                String kategori = Function.getString(c,"kategori") ;
                String idkategori = Function.getString(c,"idkategori") ;
                arrayList.add(idkategori+"__"+kategori);
            }
        }
        if (TextUtils.isEmpty(keyword)){
            q="SELECT * FROM tblkategori";
        }else {
            q="SELECT * FROM tblkategori WHERE kategori LIKE '%"+keyword+"%' ORDER BY kategori";
        }
        Cursor cur=db.sq(q);
        while(cur.moveToNext()){
            DaftarKategori.add(new getterKategori(
                    cur.getInt(cur.getColumnIndex("idkategori")),
                    cur.getString(cur.getColumnIndex("kategori"))
            ));
        }
        adapter.notifyDataSetChanged();
    }

    public void tambah(View view){
        Intent intent = new Intent(this, ActivityTambahKategori.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKategori("");
    }

    class AdapterListKategori extends RecyclerView.Adapter<AdapterListKategori.KategoriViewHolder>{
        private Context ctxAdapter;
        private List<getterKategori> data;

        public AdapterListKategori(Context ctx, List<getterKategori> viewData) {
            this.ctxAdapter = ctx;
            this.data = viewData;
        }

        @NonNull
        @Override
        public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ctxAdapter);
            View view = inflater.inflate(R.layout.list_kategori,viewGroup,false);
            return new KategoriViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final KategoriViewHolder holder, final int i) {
            final getterKategori getter = data.get(i);
            holder.kategori.setText(getter.getNamaKategori());
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
                                    Intent intent = new Intent(ctxAdapter,ActivityTambahKategori.class);
                                    intent.putExtra("idkategori",getter.getIdKategori());
                                    intent.putExtra("kategori",getter.getNamaKategori());
                                    ctxAdapter.startActivity(intent);
                                    break;

                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctxAdapter);
                                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Database db = new Database(ctxAdapter);
                                            if (db.deleteKategori(getter.getIdKategori())){
                                                data.remove(i);
                                                notifyDataSetChanged();
                                                Toast.makeText(ctxAdapter, "Delete kategori "+getter.getNamaKategori()+" berhasil", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(ctxAdapter, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.setTitle("Hapus "+getter.getNamaKategori())
                                            .setMessage("Anda yakin ingin menghapus "+getter.getNamaKategori()+" dari data kategori");
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

        class KategoriViewHolder extends RecyclerView.ViewHolder{
            TextView kategori,opt;
            public KategoriViewHolder(@NonNull View itemView) {
                super(itemView);
                kategori=(TextView)itemView.findViewById(R.id.tvKategori);
                opt=(TextView)itemView.findViewById(R.id.tvOpt);
            }
        }
    }
    class getterKategori{
        private int idKategori;
        private String namaKategori;

        public getterKategori(int idKategori, String namaKategori) {
            this.idKategori = idKategori;
            this.namaKategori = namaKategori;
        }

        public int getIdKategori() {
            return idKategori;
        }

        public String getNamaKategori() {
            return namaKategori;
        }
    }
}

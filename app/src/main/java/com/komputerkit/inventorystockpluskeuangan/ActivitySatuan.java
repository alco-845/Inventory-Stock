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

public class ActivitySatuan extends AppCompatActivity {

    Toolbar appbar;
    RecyclerView listsatuan;
    AdapterListSatuan adapter;
    List<getterSatuan> DaftarSatuan;
    View v;
    ArrayList arrayList = new ArrayList() ;
    String type;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satuan);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Satuan",getSupportActionBar());
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
                getSatuan(a);
            }
        });
    }

    public void getSatuan(String keyword){
        DaftarSatuan = new ArrayList<>();
        listsatuan = (RecyclerView) findViewById(R.id.listsatuan);
        listsatuan.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listsatuan.setLayoutManager(layoutManager);
        adapter = new AdapterListSatuan(this,DaftarSatuan);
        listsatuan.setAdapter(adapter);
        String cari = new String();
        String q;
        Cursor c;
        if(TextUtils.isEmpty(cari)){
            c = db.sq(Query.select("tblsatuan")+Query.sOrderASC("idsatuan"));
        } else {
            c = db.sq(Query.selectwhere("tblsatuan") + Query.sLike("satuankecil",cari)+" OR "+ Query.sLike("satuanbesar",cari) + Query.sOrderASC("idsatuan")) ;
        }
        if(c.getCount() > 0){
            while(c.moveToNext()){
                String satuankecil = Function.getString(c,"satuankecil") ;
                String idsatuan = Function.getString(c,"idsatuan") ;
                arrayList.add(idsatuan+"__"+satuankecil);
            }
        }
        if (TextUtils.isEmpty(keyword)){
            q="SELECT * FROM tblsatuan";
        }else {
            q="SELECT * FROM tblsatuan WHERE satuankecil LIKE '%"+keyword+"%' ORDER BY satuankecil";
        }
        Cursor cur=db.sq(q);
        while(cur.moveToNext()){
            DaftarSatuan.add(new getterSatuan(
                    cur.getInt(cur.getColumnIndex("idsatuan")),
                    cur.getString(cur.getColumnIndex("satuankecil")),
                    cur.getString(cur.getColumnIndex("satuanbesar")),
                    cur.getString(cur.getColumnIndex("nilai"))
            ));
        }
        adapter.notifyDataSetChanged();
    }

    public void tambah(View view){
        Intent intent = new Intent(this, ActivityTambahSatuan.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSatuan("");
    }

    class AdapterListSatuan extends RecyclerView.Adapter<AdapterListSatuan.SatuanViewHolder>{
        private Context ctxAdapter;
        private List<getterSatuan> data;

        public AdapterListSatuan(Context ctx, List<getterSatuan> viewData) {
            this.ctxAdapter = ctx;
            this.data = viewData;
        }

        @NonNull
        @Override
        public SatuanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ctxAdapter);
            View view = inflater.inflate(R.layout.list_satuan,viewGroup,false);
            return new SatuanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SatuanViewHolder holder, final int i) {
            final getterSatuan getter = data.get(i);
            holder.satuankecil.setText(getter.getSatuanKecil());
            holder.satuanbesar.setText(getter.getSatuanBesar());
            holder.nilai.setText(Function.removeE(getter.getNilai()));
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
                                    Intent intent = new Intent(ctxAdapter,ActivityTambahSatuan.class);
                                    intent.putExtra("idsatuan",getter.getIdSatuan());
                                    intent.putExtra("satuankecil",getter.getSatuanKecil());
                                    intent.putExtra("satuanbesar",getter.getSatuanBesar());
                                    intent.putExtra("nilai",getter.getNilai());
                                    ctxAdapter.startActivity(intent);
                                    break;

                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctxAdapter);
                                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Database db = new Database(ctxAdapter);
                                            if (db.deleteSatuan(getter.getIdSatuan())){
                                                data.remove(i);
                                                notifyDataSetChanged();
                                                Toast.makeText(ctxAdapter, "Delete satuan "+getter.getSatuanKecil()+" berhasil", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(ctxAdapter, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.setTitle("Hapus "+getter.getSatuanKecil())
                                            .setMessage("Anda yakin ingin menghapus "+getter.getSatuanKecil()+" dari data satuan");
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

        class SatuanViewHolder extends RecyclerView.ViewHolder{
            TextView satuankecil, satuanbesar, nilai, opt;
            public SatuanViewHolder(@NonNull View itemView) {
                super(itemView);
                satuankecil=(TextView)itemView.findViewById(R.id.tvSatKecil);
                satuanbesar=(TextView)itemView.findViewById(R.id.tvSatBesar);
                nilai=(TextView)itemView.findViewById(R.id.tvNilai);
                opt=(TextView)itemView.findViewById(R.id.tvOpt);
            }
        }
    }
    static class getterSatuan {
        private int idSatuan;
        private String satuanKecil;
        private String satuanBesar;
        private String nilai;

        public getterSatuan(int idSatuan, String satuanKecil, String satuanBesar, String nilai) {
            this.idSatuan = idSatuan;
            this.satuanKecil = satuanKecil;
            this.satuanBesar = satuanBesar;
            this.nilai = nilai;
        }

        public int getIdSatuan() {
            return idSatuan;
        }

        public String getSatuanKecil() {
            return satuanKecil;
        }

        public String getSatuanBesar() {
            return satuanBesar;
        }

        public String getNilai() {
            return nilai;
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

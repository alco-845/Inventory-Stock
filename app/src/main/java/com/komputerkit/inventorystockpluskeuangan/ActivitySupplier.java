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

public class ActivitySupplier extends AppCompatActivity {

    Toolbar appbar;
    RecyclerView listSupplier;
    AdapterListSupplier adapter;
    List<getterSupplier> DaftarSupplier;
    View v;
    ArrayList arrayList = new ArrayList() ;
    String type;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Supplier",getSupportActionBar());
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
                getSupplier(a);
            }
        });
    }

    public void getSupplier(String keyword){
        DaftarSupplier = new ArrayList<>();
        listSupplier = (RecyclerView) findViewById(R.id.listsupplier);
        listSupplier.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listSupplier.setLayoutManager(layoutManager);
        adapter = new AdapterListSupplier(this,DaftarSupplier);
        listSupplier.setAdapter(adapter);
        String cari = new String();
        String q;
        Cursor c;
        if(TextUtils.isEmpty(cari)){
            c = db.sq(Query.select("tblsupplier")+Query.sOrderASC("supplier"));
        } else {
            c = db.sq(Query.selectwhere("tblsupplier") + Query.sLike("supplier",cari)+Query.sOrderASC("supplier")) ;
        }
        if(c.getCount() > 0){
            while(c.moveToNext()){
                String supplier = Function.getString(c,"supplier") ;
                String idsupplier = Function.getString(c,"idsupplier") ;
                arrayList.add(idsupplier+"__"+supplier);
            }
        }
        if (TextUtils.isEmpty(keyword)){
            q="SELECT * FROM tblsupplier WHERE idsupplier>0";
        }else {
            q="SELECT * FROM tblsupplier WHERE supplier LIKE '%"+keyword+"%' AND idsupplier>0 ORDER BY supplier";
        }
        Cursor cur=db.sq(q);
        while(cur.moveToNext()){
            DaftarSupplier.add(new getterSupplier(
                    cur.getInt(cur.getColumnIndex("idsupplier")),
                    cur.getString(cur.getColumnIndex("supplier")),
                    cur.getString(cur.getColumnIndex("alamatsupp")),
                    cur.getString(cur.getColumnIndex("telpsupp"))
            ));
        }
        adapter.notifyDataSetChanged();
    }

    public void tambah(View view){
        Intent intent = new Intent(this, ActivityTambahSupplier.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupplier("");
    }

    class AdapterListSupplier extends RecyclerView.Adapter<AdapterListSupplier.SupplierViewHolder>{
        private Context ctxAdapter;
        private List<getterSupplier> data;

        public AdapterListSupplier(Context ctx, List<getterSupplier> viewData) {
            this.ctxAdapter = ctx;
            this.data = viewData;
        }

        @NonNull
        @Override
        public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ctxAdapter);
            View view = inflater.inflate(R.layout.list_supplier,viewGroup,false);
            return new SupplierViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SupplierViewHolder holder, final int i) {
            final getterSupplier getter = data.get(i);
            holder.supplier.setText(getter.getSupplier());
            holder.alamatsupp.setText(getter.getAlamatsupp());
            holder.telpsupp.setText(String.valueOf(getter.getTelpsupp()));
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
                                    Intent intent = new Intent(ctxAdapter,ActivityTambahSupplier.class);
                                    intent.putExtra("idsupplier",getter.getIdsupplier());
                                    intent.putExtra("supplier",getter.getSupplier());
                                    intent.putExtra("alamatsupp",getter.getAlamatsupp());
                                    intent.putExtra("telpsupp",String.valueOf(getter.getTelpsupp()));
                                    ctxAdapter.startActivity(intent);
                                    break;

                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctxAdapter);
                                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Database db = new Database(ctxAdapter);
                                            if (db.deleteSupplier(getter.getIdsupplier())){
                                                data.remove(i);
                                                notifyDataSetChanged();
                                                Toast.makeText(ctxAdapter, "Delete supplier "+getter.getSupplier()+" berhasil", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(ctxAdapter, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.setTitle("Hapus "+getter.getSupplier())
                                            .setMessage("Anda yakin ingin menghapus "+getter.getSupplier()+" dari data supplier");
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

        class SupplierViewHolder extends RecyclerView.ViewHolder{
            TextView supplier, alamatsupp, telpsupp, opt;
            public SupplierViewHolder(@NonNull View itemView) {
                super(itemView);
                supplier=(TextView)itemView.findViewById(R.id.tvSupplier);
                alamatsupp=(TextView)itemView.findViewById(R.id.tvAlamatSup);
                telpsupp=(TextView)itemView.findViewById(R.id.tvNoTelpSup);
                opt=(TextView)itemView.findViewById(R.id.tvOpt);
            }
        }
    }
    static class getterSupplier extends ActivityBarang.getterBarang {
        private int idsupplier;
        private String supplier;
        private String alamatsupp;
        private String telpsupp;

        public getterSupplier(int idsupplier, String supplier, String alamatsupp, String telpsupp) {
            this.idsupplier = idsupplier;
            this.supplier = supplier;
            this.alamatsupp = alamatsupp;
            this.telpsupp = telpsupp;
        }

        getterSupplier() {
        }

        public int getIdsupplier() {
            return idsupplier;
        }

        public String getSupplier() {
            return supplier;
        }

        public String getAlamatsupp() {
            return alamatsupp;
        }

        public String getTelpsupp() {
            return telpsupp;
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

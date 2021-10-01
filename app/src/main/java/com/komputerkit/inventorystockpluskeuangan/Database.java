package com.komputerkit.inventorystockpluskeuangan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    public static final String nama_database="db_stock";
    public static final int versi_database=1;
    SQLiteDatabase db;
    Context a;

    public Database(Context context){
        super(context, nama_database, null, versi_database);
        db = this.getWritableDatabase();
        a = context;
        cektbl();
    }

    public Boolean cektbl(){
        try {
            //create tabel barang
            exc("CREATE TABLE IF NOT EXISTS `tblbarang` (\n" +
                    "\t`idbarang`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`idkategori`\tINTEGER,\n" +
                    "\t`idsatuan`\tINTEGER,\n" +
                    "\t`barang`\tTEXT,\n" +
                    "\t`stok`\tREAL,\n" +
                    "\tFOREIGN KEY(`idsatuan`) REFERENCES `tblsatuan`(`idsatuan`) ON UPDATE CASCADE ON DELETE RESTRICT,\n" +
                    "\tFOREIGN KEY(`idkategori`) REFERENCES `tblkategori`(`idkategori`) ON UPDATE CASCADE ON DELETE RESTRICT\n" +
                    ");");

            //create tabel beli/barang masuk/invetory
            exc("CREATE TABLE IF NOT EXISTS `tblbeli` (\n" +
                    "\t`idbeli`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`idsupplier`\tINTEGER,\n" +
                    "\t`idbarang`\tINTEGER,\n" +
                    "\t`fakturbeli`\tUNIQUE,\n" +
                    "\t`tglbeli`\tINTEGER,\n" +
                    "\t`total` \tREAL,\n" +
                    "\t`status`\tTEXT DEFAULT 0,\n" +
                    "\tFOREIGN KEY(`idbarang`) REFERENCES `tblbarang`(`idbarang`) on update cascade on delete restrict,\n" +
                    "\tFOREIGN KEY(`idsupplier`) REFERENCES `tblsupplier`(`idsupplier`) on update cascade on delete restrict\n" +
                    ");");

            //create tabel belidetail
            exc("CREATE TABLE IF NOT EXISTS `tblbelidetail` (\n" +
                    "\t`idbelidetail`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`idbeli`\tINTEGER,\n" +
                    "\t`idbarang`\tINTEGER,\n" +
                    "\t`satuanbeli`\tREAL,\n" +
                    "\t`hargabeli`\tREAL,\n" +
                    "\t`jumlah`\tREAL,\n" +
                    "\tFOREIGN KEY(`idbeli`) REFERENCES `tblbeli`(`idbeli`) ON UPDATE CASCADE ON DELETE RESTRICT,\n" +
                    "\tFOREIGN KEY(`idbeli`) REFERENCES `tblbarang`(`idbarang`)\n" +
                    ");");

            //create tabel jual/pengeluaran
            exc("CREATE TABLE IF NOT EXISTS `tbljual` (\n" +
                    "\t`idjual`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`idpelanggan`\tINTEGER,\n" +
                    "\t`idbarang`\tINTEGER,\n" +
                    "\t`fakturjual`\tTEXT,\n" +
                    "\t`tgljual`\tINTEGER,\n" +
                    "\t`status`\tTEXT DEFAULT 0,\n" +
                    "\tFOREIGN KEY(`idpelanggan`) REFERENCES `tblpelanggan`(`idpelanggan`) on update cascade on delete restrict,\n" +
                    "\tFOREIGN KEY(`idbarang`) REFERENCES `tblbarang`(`idbarang`) on update cascade on delete restrict\n" +
                    ");");

            //create tabel jualdetail
            exc("CREATE TABLE `tbljualdetail` (\n" +
                    "\t`idjualdetail`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`idjual`\tINTEGER,\n" +
                    "\t`idbarang`\tINTEGER,\n" +
                    "\t`jumlahjual`\tREAL,\n" +
                    "\tFOREIGN KEY(`idjual`) REFERENCES `tbljual`(`idjual`) ON UPDATE CASCADE ON DELETE RESTRICT,\n" +
                    "\tFOREIGN KEY(`idjual`) REFERENCES `tblbarang`(`idbarang`)\n" +
                    ");");

            //create tabel kategori
            exc("CREATE TABLE IF NOT EXISTS `tblkategori` (\n" +
                    "\t`idkategori`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`kategori`\tTEXT\n" +
                    ");");

            //create tabel pelanggan
            exc("CREATE TABLE IF NOT EXISTS `tblpelanggan` (\n" +
                    "\t`idpelanggan`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`pelanggan`\tTEXT,\n" +
                    "\t`alamatpel`\tTEXT,\n" +
                    "\t`telppel`\tTEXT\n" +
                    ");");

            //create tabel satuan
            exc("CREATE TABLE IF NOT EXISTS `tblsatuan` (\n" +
                    "\t`idsatuan`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`satuankecil`\tTEXT,\n" +
                    "\t`satuanbesar`\tTEXT,\n" +
                    "\t`nilai`\tREAL\n" +
                    ");");

            //create tabel supplier/distributor
            exc("CREATE TABLE IF NOT EXISTS `tblsupplier` (\n" +
                    "\t`idsupplier`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`supplier`\tTEXT,\n" +
                    "\t`alamatsupp`\tTEXT,\n" +
                    "\t`telpsupp`\tTEXT\n" +
                    ");");

            //create tabel identitas
            exc("CREATE TABLE IF NOT EXISTS `tblidentitas` (\n" +
                    "\t`ididentitas` \tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`nama` \tTEXT,\n" +
                    "\t`alamat` \tTEXT,\n" +
                    "\t`telp` \tSTRING,\n" +
                    "\t`cap1` \tTEXT,\n" +
                    "\t`cap2` \tTEXT,\n" +
                    "\t`cap3` \tTEXT\n" +
                    ");");

            //create tabel transaksi
            exc("CREATE TABLE IF NOT EXISTS `tbltransaksi` (\n" +
                    "\t`idtransaksi`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`tgltransaksi`\tINTEGER,\n" +
                    "\t`notransaksi`\tINTEGER,\n" +
                    "\t`fakturtransaksi`\tTEXT,\n" +
                    "\t`keterangantransaksi`\tTEXT,\n" +
                    "\t`masuk`\tREAL DEFAULT 0,\n" +
                    "\t`keluar`\tREAL DEFAULT 0,\n" +
                    "\t`saldo`\tREAL DEFAULT 0,\n" +
                    "\t`status` \tTEXT\n" +
                    ");");

            //create view
            exc("CREATE VIEW qbarang AS SELECT tblbarang.idbarang, tblbarang.idkategori, tblbarang.idsatuan, tblkategori.kategori, tblbarang.barang, tblbarang.stok, tblsatuan.satuanbesar, tblsatuan.satuankecil, tblsatuan.nilai FROM (tblbarang INNER JOIN tblkategori ON tblbarang.idkategori = tblkategori.idkategori) INNER JOIN tblsatuan ON tblbarang.idsatuan = tblsatuan.idsatuan");

            exc("CREATE VIEW qbeli AS SELECT tblbeli.idbeli, tblbeli.fakturbeli, tblbeli.tglbeli, tblbeli.total, tblbeli.idsupplier, tblsupplier.supplier, tblsupplier.alamatsupp, tblsupplier.telpsupp FROM tblbeli INNER JOIN tblsupplier ON tblbeli.idsupplier = tblsupplier.idsupplier");

            exc("CREATE VIEW qcartbeli AS SELECT tblbelidetail.idbelidetail, tblbelidetail.idbeli, tblbelidetail.idbarang, tblbelidetail.satuanbeli, tblbelidetail.hargabeli, tblbelidetail.jumlah, tblbeli.fakturbeli, tblbeli.tglbeli, tblbeli.status, tblbeli.total, tblsupplier.supplier, tblbarang.idkategori, tblbarang.idsatuan, tblbarang.barang, tblbarang.stok, tblsatuan.satuankecil, tblsatuan.satuanbesar, tblsatuan.nilai FROM tblsupplier INNER JOIN ((((tblbarang INNER JOIN tblbelidetail ON tblbarang.idbarang = tblbelidetail.idbarang) INNER JOIN tblkategori ON tblbarang.idkategori = tblkategori.idkategori) INNER JOIN tblsatuan ON tblbarang.idsatuan = tblsatuan.idsatuan) INNER JOIN tblbeli ON tblbelidetail.idbeli = tblbeli.idbeli) ON tblsupplier.idsupplier = tblbeli.idsupplier");

            exc("CREATE VIEW qcartjual AS SELECT tbljualdetail.idjualdetail, tbljualdetail.idjual, tbljualdetail.idbarang, tbljualdetail.jumlahjual, tbljual.fakturjual, tbljual.tgljual, tbljual.status, tblpelanggan.pelanggan, tblbarang.idkategori, tblbarang.barang, tblbarang.stok FROM ((tblbarang INNER JOIN tbljualdetail ON tblbarang.idbarang = tbljualdetail.idbarang) INNER JOIN tbljual ON tbljualdetail.idjual = tbljual.idjual) INNER JOIN tblpelanggan ON tbljual.idpelanggan = tblpelanggan.idpelanggan");

            exc("CREATE VIEW qjual AS SELECT tbljual.idjual, tbljual.idpelanggan, tbljual.fakturjual, tbljual.tgljual, tblpelanggan.pelanggan, tblpelanggan.alamatpel, tblpelanggan.telppel FROM tbljual INNER JOIN tblpelanggan ON tbljual.idpelanggan = tblpelanggan.idpelanggan");

            //create trigger kurang_stok
            exc("CREATE TRIGGER kurang_stok AFTER INSERT ON tbljualdetail FOR EACH ROW BEGIN UPDATE tblbarang SET stok=stok - NEW.jumlahjual WHERE idbarang = NEW.idbarang; END");

            //create trigger tambah_stok
            exc("CREATE TRIGGER tambah_stok AFTER DELETE ON tbljualdetail FOR EACH ROW BEGIN UPDATE tblbarang SET stok=stok + OLD.jumlahjual WHERE idbarang = OLD.idbarang; END");

            //create trigger kurang_total
            exc("CREATE TRIGGER kurang_total AFTER DELETE ON tblbelidetail FOR EACH ROW BEGIN UPDATE tblbeli SET total=total - (OLD.hargabeli * OLD.jumlah) WHERE idbeli = OLD.idbeli; END");

            //create trigger tambah_total
            exc("CREATE TRIGGER tambah_total AFTER INSERT ON tblbelidetail FOR EACH ROW BEGIN UPDATE tblbeli SET total= total + (NEW.hargabeli * NEW.jumlah) WHERE idbeli = NEW.idbeli; END");

            //create Identitas
            exc("INSERT INTO tblidentitas VALUES (1, 'KomputerKit.com','Sidoarjo','0838 320 320 77','Terima Kasih','Sudah Berbelanja','Di Toko Kami')");

            //create Supplier
            exc("INSERT INTO tblsupplier VALUES (0, 'Kosong','','')");

            //create Pelanggan
            exc("INSERT INTO tblpelanggan VALUES (0, 'Kosong','','')");

            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +nama_database);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    public boolean exc(String query){
        try {
            db.execSQL(query);
            return true ;
        } catch (Exception e){
            return false ;
        }
    }

    public Cursor sq(String query){
        try {
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
        } catch (Exception e){
            return null ;
        }
    }

    //kategori

    public List<String> getIdKategori(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblkategori");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(0));
            }while (c.moveToNext());
        }
        return labels;
    }

    public boolean insertKategori(String Kategori){
        ContentValues cv= new ContentValues();
        cv.put("kategori", Kategori );
        long result= db.insert("tblkategori", null, cv);
        if (result==-1){
            return false;
        }else {
            return true;
        }
    }

    public List<String> getKategori(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblkategori");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(1));
            }while (c.moveToNext());
        }
        return labels;
    }

    public Boolean deleteKategori(Integer idKategori){
        if (db.delete("tblkategori","idkategori= ?",new String[]{String.valueOf(idKategori)})==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateKategori(Integer idKategori, String kategori) {
        ContentValues cv = new ContentValues();
        cv.put("kategori", kategori);
        long result = db.update("tblkategori", cv, "idkategori=?", new String[]{String.valueOf(idKategori)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //satuan

    public List<String> getIdSatuan(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblsatuan");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(0));
            }while (c.moveToNext());
        }
        return labels;
    }

    public boolean insertSatuan(String satuankecil, String satuanbesar, String nilai){
        ContentValues cv= new ContentValues();
        cv.put("satuankecil", satuankecil );
        cv.put("satuanbesar", satuanbesar );
        cv.put("nilai", nilai );
        long result= db.insert("tblsatuan", null, cv);
        if (result==-1){
            return false;
        }else {
            return true;
        }
    }

    public List<String> getSatuan(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblsatuan");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(2));
            }while (c.moveToNext());
        }
        return labels;
    }

    public List<String> getSatuan2(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblsatuan");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(1)+" - "+c.getString(2));
            }while (c.moveToNext());
        }
        return labels;
    }

    public List<String> getSatuanBarang(String idbarang){
        List<String> labels = new ArrayList<String>();
        String q=Query.selectwhere("tblbarang")+" idbarang="+idbarang;
        Cursor c = db.rawQuery(q,null);
        c.moveToNext();
        String idkategori=c.getString(2);
        String qq=Query.selectwhere("tblsatuan")+" idsatuan="+idkategori;
        Cursor cc = db.rawQuery(qq,null);
        if (cc.moveToNext()){
            do {
                labels.add(cc.getString(1));
                labels.add(cc.getString(2));
            }while (cc.moveToNext());
        }
        return labels;
    }

    public Boolean deleteSatuan(Integer idSatuan){
        if (db.delete("tblsatuan","idsatuan= ?",new String[]{String.valueOf(idSatuan)})==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateSatuan(Integer idSatuan, String satuankecil, String satuanbesar, String nilai){
        ContentValues cv = new ContentValues();
        cv.put("satuankecil", satuankecil);
        cv.put("satuanbesar", satuanbesar);
        cv.put("nilai", nilai);
        long result = db.update("tblsatuan", cv, "idsatuan=?", new String[]{String.valueOf(idSatuan)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //barang

    public List<String> getIdBarang(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblbarang");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(0));
            }while (c.moveToNext());
        }
        return labels;
    }

    public boolean insertBarang(int idkategori, int idsatuan,String barang, String stok){
        ContentValues cv= new ContentValues();
        cv.put("idkategori", idkategori );
        cv.put("idsatuan",idsatuan );
        cv.put("barang", barang );
        cv.put("stok", stok );
        long result= db.insert("tblbarang", null, cv);
        if (result==-1){
            return false;
        }else {
            return true;
        }
    }

    public List<String> getBarang(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblbarang");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(1));
            }while (c.moveToNext());
        }
        return labels;
    }

    public Boolean deleteBarang(Integer idbarang){
        if (db.delete("tblbarang","idbarang= ?",new String[]{String.valueOf(idbarang)})==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateBarang(Integer idbarang, Integer idkategori, int idsatuan, String barang, String stok){
        ContentValues cv = new ContentValues();
        cv.put("idkategori", idkategori);
        cv.put("idsatuan", idsatuan);
        cv.put("barang", barang );
        cv.put("stok", stok );
        long result = db.update("tblbarang", cv, "idbarang=?", new String[]{String.valueOf(idbarang)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //pelanggan

    public List<String> getIdPelanggan(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblpelanggan");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(0));
            }while (c.moveToNext());
        }
        return labels;
    }

    public boolean insertPelanggan(String pelanggan, String alamat, String notelp){
        ContentValues cv= new ContentValues();
        cv.put("pelanggan", pelanggan );
        cv.put("alamatpel", alamat );
        cv.put("telppel", notelp );
        long result= db.insert("tblpelanggan", null, cv);
        if (result==-1){
            return false;
        }else {
            return true;
        }
    }

    public List<String> getPelanggan(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblpelanggan");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(1));
            }while (c.moveToNext());
        }
        return labels;
    }

    public Boolean deletePelanggan(Integer idpelanggan){
        if (db.delete("tblpelanggan","idpelanggan= ?",new String[]{String.valueOf(idpelanggan)})==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updatePelanggan(int idpelanggan, String pelanggan, String alamat, String notelp){
        ContentValues cv = new ContentValues();
        cv.put("pelanggan", pelanggan );
        cv.put("alamatpel", alamat );
        cv.put("telppel", notelp );
        long result = db.update("tblpelanggan", cv, "idpelanggan=?", new String[]{String.valueOf(idpelanggan)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //supplier/distributor

    public List<String> getIdSupplier(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblsupplier");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(0));
            }while (c.moveToNext());
        }
        return labels;
    }

    public boolean insertSupplier(String supplier, String alamat, String notelp){
        ContentValues cv= new ContentValues();
        cv.put("supplier", supplier );
        cv.put("alamatsupp", alamat );
        cv.put("telpsupp", notelp );
        long result= db.insert("tblsupplier", null, cv);
        if (result==-1){
            return false;
        }else {
            return true;
        }
    }

    public List<String> getSupplier(){
        List<String> labels = new ArrayList<String>();
        String q=Query.select("tblsupplier");
        Cursor c = db.rawQuery(q,null);
        if (c.moveToNext()){
            do {
                labels.add(c.getString(1));
            }while (c.moveToNext());
        }
        return labels;
    }

    public Boolean deleteSupplier(Integer idsupplier){
        if (db.delete("tblsupplier","idsupplier= ?",new String[]{String.valueOf(idsupplier)})==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateSupplier(int idsupplier, String supplier, String alamat, String notelp){
        ContentValues cv = new ContentValues();
        cv.put("supplier", supplier );
        cv.put("alamatsupp", alamat );
        cv.put("telpsupp", notelp );
        long result = db.update("tblsupplier", cv, "idsupplier=?", new String[]{String.valueOf(idsupplier)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}

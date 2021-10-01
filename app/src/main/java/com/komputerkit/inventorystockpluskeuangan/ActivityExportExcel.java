package com.komputerkit.inventorystockpluskeuangan;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ActivityExportExcel extends AppCompatActivity {

    Toolbar appbar;

    Config config,temp;
    Database db;
    SharedPreferences getPrefs;
    String nama;
    String path;

    int row = 0;

    private WritableCellFormat times;
    private WritableCellFormat timesBold;
    private WritableCellFormat timesBoldUnderline;
    String type;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_excel);

        getWindow().setSoftInputMode(3);
        this.config = new Config(getSharedPreferences("config", 0));
        this.temp = new Config(getSharedPreferences("temp", 0));
        this.db = new Database(this);
        this.v = findViewById(android.R.id.content);
        this.type = getIntent().getStringExtra("type");
        this.path = Environment.getExternalStorageDirectory().toString() + "/Download/";

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        nama = "Export Excel";

        this.getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        try {
            new File(Environment.getExternalStorageDirectory() + "/InventoryStockPlusKeuangan").mkdirs();
        } catch (Exception e) {
        }
        setText();
        Function.btnBack(nama, getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setText(){
        Function.setText(v,R.id.ePath,"Internal Storage/Download/");
    }


    public void export(View view) throws IOException, WriteException {
        if (type.equals("pelanggan")){
            nama="Laporan Pelanggan";
            exPelanggan();
        }else if(type.equals("barang")){
            nama="Laporan Barang";
            exBarang();
        }else if(type.equals("distributor")){
            nama="Laporan Supplier";
            exSupplier();
        }else if(type.equals("penyimpanan")){
            nama="Laporan Barang Masuk";
            exLaporanPenyimpanan();
        } else if (type.equals("penjualan")){
            nama="Laporan Barang Keluar";
            exLaporanPenjualan();
        } else if (type.equals("keuangan")){
            nama="Laporan Keuangan";
            exLaporanKeuangan();
        }

    }

    private void exLaporanKeuangan() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        setHeader(db,sheet,6);
        excelNextLine(sheet,2) ;

        String[] judul = {"No. Transaksi", "Faktur Transaksi", "Tanggal Transaksi", "Harga Masuk", "Harga Keluar", "Saldo"};
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("tbltransaksi")) ;
        if(c.getCount() > 0){
            while(c.moveToNext()){
                int col = 0 ;
                String notrans = Function.getString(c, "notransaksi");
                String fakturtrans = Function.getString(c, "fakturtransaksi");
                String tgltrans = Function.getString(c, "tgltransaksi");
                String hmasuk = Function.getString(c, "masuk");
                String hkeluar = Function.getString(c, "keluar");
                String saldo = Function.getString(c, "saldo");


                addLabel(sheet,col++, row, notrans);
                addLabel(sheet,col++, row, fakturtrans);
                addLabel(sheet,col++, row, Function.dateToNormal(tgltrans));
                addLabel(sheet,col++, row, Function.removeE(hmasuk));
                addLabel(sheet,col++, row, Function.removeE(hkeluar));
                addLabel(sheet,col++, row, Function.removeE(saldo));

                row++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }

    }

    private void exLaporanPenyimpanan() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        setHeader(db,sheet,7);
        excelNextLine(sheet,2) ;

        String[] judul = {
                "No.",
                "Faktur",
                "Tanggal Beli",
                "Barang",
                "Harga Beli",
                "Jumlah Beli",
                "Nama Supplier",};
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("qcartbeli")) ;
        if(c.getCount() > 0){
            int no = 1 ;
            while(c.moveToNext()){
                int col = 0 ;
                String faktur = Function.getString(c, "fakturbeli");
                String tglorder = Function.getString(c, "tglbeli");
                String barang = Function.getString(c, "barang");
                String harga = Function.getString(c, "hargabeli");
                String jumlah = Function.getString(c, "jumlah");
                String supplier = Function.getString(c, "supplier");

                addLabel(sheet,col++, row, Function.intToStr(no));
                addLabel(sheet,col++, row, faktur);
                addLabel(sheet,col++, row, Function.dateToNormal(tglorder));
                addLabel(sheet,col++, row, barang);
                addLabel(sheet,col++, row, Function.removeE(harga));
                addLabel(sheet,col++, row, jumlah);
                addLabel(sheet,col++, row, supplier);

                row++ ;
                no++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void exLaporanPenjualan() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        setHeader(db,sheet,6);
        excelNextLine(sheet,2) ;

        String[] judul = {
                "No.",
                "Faktur",
                "Tanggal Jual",
                "Barang",
                "Jumlah Jual",
                "Nama Pelanggan",};
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("qcartjual")) ;
        if(c.getCount() > 0){
            int no = 1 ;
            while(c.moveToNext()){
                int col = 0 ;
                String faktur = Function.getString(c, "fakturjual");
                String tglorder = Function.getString(c, "tgljual");
                String barang = Function.getString(c, "barang");
                String jumlahjual = Function.getString(c, "jumlahjual");
                String pelanggan = Function.getString(c, "pelanggan");

                addLabel(sheet,col++, row, Function.intToStr(no));
                addLabel(sheet,col++, row, faktur);
                addLabel(sheet,col++, row, Function.dateToNormal(tglorder));
                addLabel(sheet,col++, row, barang);
                addLabel(sheet,col++, row, jumlahjual);
                addLabel(sheet,col++, row, pelanggan);

                row++ ;
                no++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void exBarang() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        String[] judul = {"No.", "Barang", "Kategori", "Stok", "Satuan Besar", "Satuan Kecil"};
        setHeader(db,sheet,6);
        excelNextLine(sheet,2) ;
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("qbarang")) ;
        if(c.getCount() > 0){
            int no = 1 ;
            while(c.moveToNext()){
                int col = 0 ;
                String barang = Function.getString(c,"barang") ;
                String kategori = Function.getString(c,"kategori") ;
                String stok = Function.getString(c, "stok");
                String sBesar = Function.getString(c,"satuanbesar") ;
                String sKecil = Function.getString(c, "satuankecil");

                addLabel(sheet,col++, row, Function.intToStr(no));
                addLabel(sheet,col++, row, barang);
                addLabel(sheet,col++, row, kategori);
                addLabel(sheet,col++, row, stok);
                addLabel(sheet,col++, row, sBesar);
                addLabel(sheet,col++, row, sKecil);

                row++ ;
                no++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void exSupplier() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        setHeader(db,sheet,4);
        excelNextLine(sheet,2) ;

        String[] judul = {"No.", "Nama Supplier","Alamat Supplier","Nomor Telepon Supplier"} ;
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("tblsupplier"));
        if(c.getCount() > 1){
            int no = 1 ;
            while(c.moveToNext()){
                int col = 0 ;
                String nama = Function.getString(c,"supplier") ;
                String alamat = Function.getString(c,"alamatsupp") ;
                String telp = Function.getString(c,"telpsupp") ;

                addLabel(sheet,col++, row, Function.intToStr(no));
                addLabel(sheet,col++, row, nama);
                addLabel(sheet,col++, row, alamat);
                addLabel(sheet,col++, row, telp);
                row++ ;
                no++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void exPelanggan() throws IOException, WriteException {
        File file = new File(path+nama+" "+Function.getDate("dd-MM-yyyy_HHmmss")+".xls") ;

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet sheet = workbook.getSheet(0);

        createLabel(sheet);
        setHeader(db,sheet,4);
        excelNextLine(sheet,2) ;

        String[] judul = {"No.", "Nama Pelanggan","Alamat","Nomor Telp"} ;
        setJudul(sheet,judul);

        Cursor c = db.sq(Query.select("tblpelanggan"));
        if(c.getCount() > 1){
            int no = 1 ;
            while(c.moveToNext()){
                int col = 0 ;
                String nama = Function.getString(c,"pelanggan") ;
                String alamat = Function.getString(c,"alamatpel") ;
                String telp = Function.getString(c,"telppel") ;

                addLabel(sheet,col++, row, Function.intToStr(no));
                addLabel(sheet,col++, row, nama);
                addLabel(sheet,col++, row, alamat);
                addLabel(sheet,col++, row, telp);
                row++ ;
                no++ ;
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void setHeader(Database db, WritableSheet sheet,int JumlahKolom){
        try {
            Cursor c = db.sq(Query.select("tblidentitas")) ;
            c.moveToNext() ;

            addLabel(sheet, row++,Function.getString(c,"nama"),JumlahKolom);
            addLabel(sheet, row++,Function.getString(c,"alamat"),JumlahKolom);
            addLabel(sheet, row++,Function.getString(c,"telp"),JumlahKolom);
        }catch (Exception e){

        }
    }

    public void setCenter(CSVWriter csvWriter, int JumlahKolom, String value){
        try {
            int baru ;
            if(JumlahKolom%2 == 1){
                baru = JumlahKolom-1 ;
            } else {
                baru = JumlahKolom ;
            }
            int i ;
            String[] a = new String[baru];
            for(i = 0 ; i < baru/2 ; i++){
                a[i] = "" ;
            }
            a[i] = value ;
            csvWriter.writeNext(a);
        } catch (Exception e){

        }
    }

    private void createContent(WritableSheet sheet) throws WriteException,
            RowsExceededException {
        int startSum = row+1;
        // Write a few number
        for (int i = 1; i < 10; i++) {
            // First column
            addNumber(sheet, 0, row , i + 10);
            // Second column
            addNumber(sheet, 1, row++, i * i);
        }

        int endSum = row;
        // Lets calculate the sum of it
        StringBuffer buf = new StringBuffer();
        buf.append("SUM(A"+startSum+":A"+endSum+")");
        Formula f = new Formula(0, row, buf.toString());
        sheet.addCell(f);
        buf = new StringBuffer();
        buf.append("SUM(B"+startSum+":B"+endSum+")");
        f = new Formula(1, row, buf.toString());
        sheet.addCell(f);
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBold);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                           Integer integer) throws WriteException, RowsExceededException {
        jxl.write.Number number;
        number = new jxl.write.Number(column, row, integer, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    private void addLabel(WritableSheet sheet, int row, String s,int JumlahKolom)
            throws WriteException, RowsExceededException {
        Label label;
        JumlahKolom-- ;
        WritableCellFormat newFormat = null;
        newFormat = new WritableCellFormat(timesBold) ;
        newFormat.setAlignment(Alignment.CENTRE) ;
        label = new Label(0, row, s, newFormat) ;
        sheet.addCell(label);
        sheet.mergeCells(0,row,JumlahKolom,row) ; // parameters -> col1,row1,col2,row2
    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automaticall                y wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        WritableFont times10ptBold = new WritableFont(
                WritableFont.TIMES, 12, WritableFont.BOLD, false);
        timesBold = new WritableCellFormat(times10ptBold);
        // Lets automatically wrap the cells
        timesBold.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(timesBold);

//        cv.setAutosize(true);
    }

    public Boolean excelNextLine(WritableSheet sheet, int total){
        try {
            for (int i = 0 ; i < total ; i++){
                addLabel(sheet,0,row++,"");
            }
            return true ;
        }catch (Exception e){
            return false ;
        }
    }

    public void setJudul(WritableSheet sheet, String[] val) throws WriteException {
        int col = 0 ;
        for (int i = 0 ; i < val.length ; i++){
            addCaption(sheet,col++,row,val[i]);
        }
        row++ ;
    }

}

package com.komputerkit.inventorystockpluskeuangan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ActivityCetak extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    Toolbar appbar;
    Config config,temp ;
    Database db ;
    String device,faktur,hasil ;
    View v ;
    int flagready = 0 ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Cetak",getSupportActionBar());

        config = new Config(getSharedPreferences("config",this.MODE_PRIVATE));
        temp = new Config(getSharedPreferences("temp",this.MODE_PRIVATE));
        db = new Database(this) ;
        v = this.findViewById(android.R.id.content);

        device = config.getCustom("Printer","");
        faktur = getIntent().getStringExtra("fakturbeli") ;

        if(TextUtils.isEmpty(faktur)){
            Intent i = new Intent(this, ActivityMenuUtama.class) ;
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            Toast.makeText(this, "Faktur Kosong", Toast.LENGTH_SHORT).show();
            startActivity(i);
        }

        try {
            findBT();
            openBT();
        }catch (Exception e){
            Toast.makeText(this, "Bluetooth Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetConnection() {
        if (mmInputStream != null) {
            try {mmInputStream.close();} catch (Exception e) {}
            mmInputStream = null;
        }

        if (mmOutputStream != null) {
            try {mmOutputStream.close();} catch (Exception e) {}
            mmOutputStream = null;
        }

        if (mmSocket != null) {
            try {mmSocket.close();} catch (Exception e) {}
            mmSocket = null;
        }
    }

    public void preview(View view){
        try {
            ConstraintLayout w = (ConstraintLayout) findViewById(R.id.wTeks) ;
            setPreview() ;
            w.setVisibility(View.VISIBLE);
        } catch (Exception e){
            Toast.makeText(this, "Preview gagal", Toast.LENGTH_SHORT).show();
        }
    }

    public void setPreview(){
        Cursor identitas = db.sq(Query.selectwhere("tblidentitas")+Query.sWhere("ididentitas","1")) ;
        identitas.moveToNext() ;
        Cursor bayar = db.sq(Query.selectwhere("qbeli")+Query.sWhere("fakturbeli",faktur)) ;
        bayar.moveToNext() ;
        Cursor penj = db.sq(Query.selectwhere("qcartbeli")+Query.sWhere("fakturbeli",faktur)) ;

        String toko = Function.getString(identitas,"nama") ;
        String alamat = Function.getString(identitas,"alamat") ;
        String telp = Function.getString(identitas,"telp") ; Function.setText(v,R.id.tHeader,toko+"\n"+alamat+"\n"+telp) ;
        String tfaktur   = "Faktur : "+faktur ; Function.setText(v,R.id.tFaktur,tfaktur) ;
        String supplier = "Supplier : "+Function.getString(bayar,"supplier") ; Function.setText(v,R.id.tSupplier,supplier) ;
//        String pembayaran = "Pembayaran : "+typebayar ; Function.setText(v,R.id.tPembayaran,pembayaran) ;
        String tgl       = "Tanggal : "+Function.getDate("dd-MM-yyyy") ; Function.setText(v,R.id.tTanggal,tgl) ;

        String header = Function.setCenter(toko)+"\n"+
                Function.setCenter(alamat)+"\n"+
                Function.setCenter(telp)+"\n"+
                "\n"+
                tfaktur+"\n"+
                tgl+"\n"+
                supplier+"\n"+
//                pembayaran+"\n"+
                Function.getStrip();

        String body = "" ;
        String view = "" ;
        while(penj.moveToNext()){
            String barang = Function.getString(penj,"barang") ;
            String jumlah = Function.getString(penj,"jumlah") ;
            String satuan = Function.getString(penj, "satuanbeli");
            String harga = Function.getString(penj,"hargabeli") ;
            double total = Function.strToDouble(jumlah)*Function.strToDouble(harga) ;
            String metod="";
            String idsatuan=Function.getString(penj, "idsatuan");
            Cursor san = db.sq(Query.selectwhere("tblsatuan")+" idsatuan="+idsatuan);
            san.moveToNext();
            if (satuan.equals("1")){
                metod = Function.getString(san, "satuanbesar");
            } else {
                metod = Function.getString(san, "satuankecil");
            }

            body+=  barang+"\n"+
                    Function.removeE(jumlah)+" "+metod+" x "+Function.removeE(harga)+"\n"+
                    Function.setRight(Function.removeE(total))+"\n" ;
            view+=  barang+"\n"+
                    Function.removeE(jumlah)+" "+metod+" x "+Function.removeE(harga)+"\n"+
                    setRight(Function.removeE(total))+"\n" ;
        }
        Function.setText(v,R.id.tbarang,view) ;
        body+=Function.getStrip() ;

        String jumlahbayar = "Total : " + Function.removeE(Function.getString(bayar,"total")) ; Function.setText(v,R.id.teks,jumlahbayar) ;
        String caption =  Function.getString(identitas,"cap1") ;
        String caption2 = Function.getString(identitas,"cap2") ;
        String caption3 = Function.getString(identitas,"cap3") ; Function.setText(v,R.id.tCaption,caption+"\n"+caption2+"\n"+caption3) ;

        String footer = Function.setRight(jumlahbayar)+"\n\n"+
                Function.setCenter(caption)+"\n"+
                Function.setCenter(caption2)+"\n"+
                Function.setCenter(caption3) ;


        hasil = header+body+footer ;
    }

    public static String setRight(String item){
        int leng = item.length() ;
        String hasil = "" ;
        for(int i=0 ; i<32-leng;i++){
            if((31-leng) == i){
                hasil += item ;
            } else {
                hasil += "  " ;
            }
        }
        return hasil ;
    }

    public void cari(View view){
        Intent i = new Intent(this,ActivityCetakCari.class) ;
        i.putExtra("fakturbeli",faktur) ;
        startActivity(i);
    }

    public void cetak(View view) throws IOException {
        try {
            if(Function.getText(v,R.id.ePrinter).equals("Tidak Ada Perangkat")){
                Toast.makeText(this, "Tidak ada Printer", Toast.LENGTH_SHORT).show();
            } else if (flagready == 1){
                try {
                    setPreview();
                }catch (Exception e){
                    Toast.makeText(this, "Preview Gagal", Toast.LENGTH_SHORT).show();
                }
                sendData(hasil);

                onBackPressed();

            } else {
                Toast.makeText(this, "Printer belum siap", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Proses Cetak Gagal, Harap periksa Printer atau bluetooth anda", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ActivityMenuUtama.class) ;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        startActivity(i);
    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Tidak ada Bluetooth Adapter", Toast.LENGTH_SHORT).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                Function.setText(v,R.id.ePrinter,"Printer Belum Dipilih") ;
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(this.device)) {
                        mmDevice = device;
                        Function.setText(v,R.id.ePrinter,this.device) ;
                        break;
                    }
                }
            } else {
                Function.setText(v,R.id.ePrinter,"Tidak Ada Perangkat") ;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void openBT() throws IOException {
        try {
            resetConnection();
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            //00001101-0000-1000-8000-00805F9B34FB
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            ConstraintLayout c = (ConstraintLayout) findViewById(R.id.simbol) ;
            final int sdk = Build.VERSION.SDK_INT;
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                c.setBackgroundDrawable( getResources().getDrawable(R.drawable.ovalgreen) );
            } else {
                c.setBackground( getResources().getDrawable(R.drawable.ovalgreen));
            }
            flagready = 1 ;
//            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal tersambung dengan printer", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal tersambung dengan printer", Toast.LENGTH_SHORT).show();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(ActivityCetak.this, data, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendData(String hasil) throws IOException {
        try {
            hasil += "\n\n\n";
            mmOutputStream.write(hasil.getBytes());mBluetoothAdapter.cancelDiscovery() ; mmSocket.close();

            resetConnection();
            Toast.makeText(this, "Print Berhasil", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

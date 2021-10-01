package com.komputerkit.inventorystockpluskeuangan;

import android.support.v7.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import se.simbio.encryption.Encryption;

public class Function {
    static String slct = "SELECT * FROM ";
    static String base64Encode = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgVSRt7nJd2WgUETooU5WM2BlRkDJjm1sOr2D27iLLtY8T0ncGTntrqwoMN0a1lICO/rKrUwf/kqo6T0veXwbOFI+83mrZzmUzlkQx7eFYglTvurDaoYQx92K7Rkf669D3ENDFb8wh+ns7Rg52xML5RtexS1f3MPcnFC8Ludk5sjO7Fp7WDiZeFRrV7tkYH6h3ObySUixAsxM4NrY3WvyWi3f1BlyLZkn6Jy01yv3QiHkm8ynrC6hRzGhbnrFifPharPXF5imOJEPmA9k1ANVrFrWIrecj1VQnhSA9FhezTWVSor+Azw/RW8B0LO5suz1q6Y4suAeCyrcgAyhGHBJywIDAQAB" ;

    public static String addSlashes(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\\n", "\\\\n");
        s = s.replaceAll("\\r", "\\\\r");
        s = s.replaceAll("\\00", "\\\\0");
        s = s.replaceAll("'", "\"");
        return s;
    }

    public static String quote(String w){
        return "\'" + addSlashes(w) + "\'" ;
    }

    public static String intToStr(int a){
        return String.valueOf(a) ;
    }

    public static int strToInt(String v){
        try {
            return Integer.parseInt(v) ;
        } catch (Exception e){
            return 0 ;
        }
    }

    public static double strToDouble(String v){
        try {
            return Double.parseDouble(v) ;
        }catch (Exception e){
            return 0 ;
        }
    }

    public static String doubleToStr(double d){
        try {
            return String.valueOf(d) ;
        }catch (Exception e){
            return "" ;
        }
    }

    public static String getText(View v, int id){ //cara instal //FFunction.getText(this.findViewById(android.R.id.content), R.id.jumlah) ;
        TextView a = (TextView) v.findViewById(id) ;
        return a.getText().toString() ;
    }
    public static Boolean setText(View v, int id,String t){ //cara instal //FFunction.setText(this.findViewById(android.R.id.content), R.id.jumlah,"ini String") ;
        TextView a = (TextView) v.findViewById(id) ;
        try {
            a.setText(t);
            return true ;
        }catch (Exception e){
            return false;
        }
    }
    public static String getSpinnerItem(View v,int idSpinner){
        try {
            Spinner s = (Spinner) v.findViewById(idSpinner) ;
            return s.getSelectedItem().toString() ;
        }catch (Exception e){
            return "" ;
        }
    }

    public static Boolean clearFocus(View v, int id){ //cara instal //FFunction.setText(this.findViewById(android.R.id.content), R.id.jumlah,"ini String") ;
        TextView a = (TextView) v.findViewById(id) ;
        try {
            a.clearFocus();
            return true ;
        }catch (Exception e){
            return false;
        }
    }

    public static String getString(Cursor c, String name){
        return c.getString(c.getColumnIndex(name)) ;
    }

    public static int getInt(Cursor c, String name){
        return c.getInt(c.getColumnIndex(name)) ;
    }

    public static String getDate(String type){ //Random time type : HHmmssddMMyy
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(type);
        String formattedDate = df.format(c.getTime());
        return formattedDate ;
    }

    public static String getPerfectTime(){
        Calendar c = Calendar.getInstance();
        return c.getTime().toString() ;
    }

    public static String setDatePicker(int year , int month, int day) {
        String bln,thn,hri ;
        if(month < 10){
            bln = "0"+ Function.intToStr(month) ;
        } else {
            bln = Function.intToStr(month) ;
        }

        if(day < 10){
            hri = "0"+ Function.intToStr(day) ;
        } else {
            hri = Function.intToStr(day) ;
        }

        return Function.intToStr(year) + bln+hri;
    }

    public static String setDatePickerNormal(int year , int month, int day) {
        String bln,thn,hri ;
        if(month < 10){
            bln = "0"+ Function.intToStr(month) ;
        } else {
            bln = Function.intToStr(month) ;
        }

        if(day < 10){
            hri = "0"+ Function.intToStr(day) ;
        } else {
            hri = Function.intToStr(day) ;
        }

        return hri+"/"+bln+"/"+Function.intToStr(year);
    }

    public static String numberFormat(String number){ // Rp. 1,000,000.00
        try{
            String hasil = "";
            String[] b = number.split("\\.") ;

            if(b.length == 1){
                String[] a = number.split("") ;
                int c=0 ;
                for(int i=a.length-1;i>=0;i--){
                    if(c == 3 && !TextUtils.isEmpty(a[i])){
                        hasil = a[i] + "." + hasil ;
                        c=1;
                    } else {
                        hasil = a[i] + hasil ;
                        c++ ;
                    }
                }
            } else {
                String[] a = b[0].split("") ;
                int c=0 ;
                for(int i=a.length-1;i>=0;i--){
                    if(c == 3 && !TextUtils.isEmpty(a[i])){
                        hasil = a[i] + "." + hasil ;
                        c=1;
                    } else {
                        hasil = a[i] + hasil ;
                        c++ ;
                    }
                }
                hasil+=","+b[1] ;
            }
            return  hasil ;
        }catch (Exception e){
            return  "" ;
        }
    }

    public static String unNumberFormat(String number){
        try {
            String a = number.replace(",","-") ;
            String b = a.replace("\\.","") ;
            String c = b.replace("-",".") ;
            return c ;
        }catch (Exception e){
            return "" ;
        }
    }

    public static String changeComa(String number){
        try {
            String a = number.replace(",",".") ;
            return a ;
        }catch (Exception e){
            return "" ;
        }
    }

    public static String dateToNormal(String date){
        try {
            String b1 = date.substring(4) ;
            String b2 = b1.substring(2) ;

            String m = b1.substring(0,2) ;
            String d = b2.substring(0,2) ;
            String y = date.substring(0,4) ;
            return d+"/"+m+"/"+y ;
        }catch (Exception e){
            return "ini tanggal" ;
        }
    }

    public static String upperCaseFirst(String value) {

        // Convert String to char array.
        char[] array = value.toCharArray();
        // Modify first element in array.
        array[0] = Character.toUpperCase(array[0]);
        // Return string.
        return new String(array);
    }

    public static ActionBar btnBack(String title, ActionBar bar){
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(title);
        return bar;
    }
    public static AlertDialog.Builder alertDialog(Context ctx, String title, String message, String pBtn,
                                                  DialogInterface.OnClickListener positive, String nBtn, DialogInterface.OnClickListener negative){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.create();
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(pBtn,positive)
                .setNegativeButton(nBtn,negative);
        return builder;
    }
    public static AlertDialog.Builder alertDialog(Context ctx, String message, String pBtn,
                                                  DialogInterface.OnClickListener positive,String nBtn, DialogInterface.OnClickListener negative){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.create();
        builder.setPositiveButton(pBtn,positive)
                .setNegativeButton(nBtn,negative);
        return builder;
    }

    public static String getCampur(String satu,String dua){
        return satu+"__"+dua ;
    }
    public static String getCampur(String satu,String dua,String tiga){
        return satu+"__"+dua+"__"+tiga ;
    }
    public static String getCampur(String satu,String dua,String tiga, String empat){
        return satu+"__"+dua+"__"+tiga+"__"+empat ;
    }
    public static String getCampur(String satu,String dua,String tiga, String empat,String lima){
        return satu+"__"+dua+"__"+tiga+"__"+empat+"__"+lima ;
    }
    public static String getCampur(String satu,String dua,String tiga, String empat,String lima,String enam){
        return satu+"__"+dua+"__"+tiga+"__"+empat+"__"+lima+"__"+enam ;
    }
    public static String setCenter(String item){
        int leng = item.length() ;
        String hasil = "" ;
        for(int i=0 ; i<32-leng;i++){
            if((32-leng)/2+1 == i){
                hasil += item ;
            } else {
                hasil += " " ;
            }
        }
        return hasil ;
    }
    public static String getStrip(){
        String a = "" ;
        for(int i = 0 ; i < 32 ; i++){
            a+="-" ;
        }
        return a+"\n" ;
    }
    public static String setRight(String item){
        int leng = item.length() ;
        String hasil = "" ;
        for(int i=0 ; i<32-leng;i++){
            if((31-leng) == i){
                hasil += item ;
            } else {
                hasil += " " ;
            }
        }
        return hasil ;
    }
    public static String setRight(String item,int min){
        int leng = item.length() ;
        int total = 32-min ;
        String hasil = "" ;
        for(int i=0 ; i<total-leng;i++){
            if((total-leng) == i){
                hasil += item ;
            } else {
                hasil += " " ;
            }
        }
        return hasil ;
    }

    public static String getEnter(){
        return "\n" ;
    }

    public static String removeEE(double value){
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(3);
        return Function.numberFormat(df.format(value)) ;
    }

    public static String removeE(double value){
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        return Function.numberFormat(df.format(value)) ;
    }
    public static String removeE(String value){
        double hasil = Function.strToDouble(value) ;
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        return Function.numberFormat(df.format(hasil)) ;
    }

    public static String removeComma(String value){
        try{
            String a=value.replace(",0","");
            return a;
        }catch (Exception e){
            return "";
        }
    }

    public static Boolean copyFile(String pIn, String pOut, String name){
        try{
            File file = new File(pOut);
            if (!file.exists()) {
                file.mkdirs();
            }
            InputStream in = new FileInputStream(pIn + name);
            OutputStream out = new FileOutputStream(pOut + name);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = in.read(buffer);
                if (read != -1) {
                    out.write(buffer, 0, read);
                } else {
                    in.close();
                    out.flush();
                    out.close();
                    return Boolean.valueOf(true);
                }
            }
        } catch (Exception e) {
            return Boolean.valueOf(false);
        }

    }

    public static Boolean deleteFile(String path){
        try {
            new File(path).delete();
            return Boolean.valueOf(true);
        } catch (Exception e) {
            return Boolean.valueOf(false);
        }
    }


    public static Boolean renameFile(String path, String namaLama, String namaBaru){
        try {
            new File(path + namaLama).renameTo(new File(path + namaBaru));
            return Boolean.valueOf(true);
        } catch (Exception e) {
            return Boolean.valueOf(false);
        }

    }

    public static String getEncrypt(String teks) {
        try {
            return Encryption.getDefault("KomputerKit", "KomputerKit", new byte[16]).encryptOrNull(teks);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDecrypt(String teks) {
        try {
            return Encryption.getDefault("KomputerKit", "KomputerKit", new byte[16]).decryptOrNull(teks);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDeviceID(ContentResolver c){
        return Settings.Secure.getString(c, Settings.Secure.ANDROID_ID);
    }

    public static String getBase64Code(){
        return base64Encode ;
    }
}

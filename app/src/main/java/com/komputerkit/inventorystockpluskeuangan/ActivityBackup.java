package com.komputerkit.inventorystockpluskeuangan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

public class ActivityBackup extends AppCompatActivity {

    Toolbar appbar;
    View v;
    Config config;
    String dirOut, dirIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        appbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(appbar);
        Function.btnBack("Backup", getSupportActionBar());

        v = this.findViewById(android.R.id.content);
        config = new Config(getSharedPreferences("config", 0));

        this.dirIn = "/data/data/com.komputerkit.inventorystockpluskeuangan/databases/";
        this.dirOut = Environment.getExternalStorageDirectory().toString() + "/Download/";
        setText();
        File file = new File(this.dirOut);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void backup(View v) {
        String dbName = Database.nama_database;
        String dbOut = dbName + Function.getDate("HH-mm dd-MM-yyyy");
        if (!Function.copyFile(this.dirIn, this.dirOut, dbName).booleanValue()) {
            Toast.makeText(this, "Backup Data Gagal", Toast.LENGTH_SHORT).show();
        } else if (Function.renameFile(this.dirOut, dbName, dbOut).booleanValue()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage((CharSequence) "Backup Data tersimpan di folder Download");
            alert.setPositiveButton((CharSequence) "ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.show();
        } else {
            Toast.makeText(this, "Backup Data Gagal1", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setText() {
        Function.setText(this.v, R.id.ePath, "Internal Storage/Download/");

    }
}

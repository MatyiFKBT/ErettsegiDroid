package pw.matyi.erettsegi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {
    EditText evinput;
    Spinner spinner;
    CheckBox oktober, majus, megoldas;
    Button button, button2;
    String honap,evszak;
    String szint, szintbetu;
    Switch szintkapcs;
    String fileName;
    Boolean mpdf;
    DownloadManager downloadManager;
    long downloadReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        evinput = (EditText) findViewById(R.id.ev);
        permission_check();


    }

    public void download() {
        String ev = evinput.getText().toString();
        spinner = (Spinner)this.findViewById(R.id.spinner);
        String targy = getResources().getStringArray(R.array.targy_values)[spinner.getSelectedItemPosition()];
        if(targy == "null") {
            printtoast("Válassz egy tantárgyat!");
        }
        String link = "http://dload.oktatas.educatio.hu/erettsegi/feladatok_20"+ev+evszak+"_"+szint+"/"+szintbetu+"_"+targy+"_"+ev+honap+"_fl.pdf";
        String[] linksplit = link.split("/");
        fileName = linksplit[linksplit.length-1];
        printtoast(fileName + " letöltése folyamatban...");
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + "Erettsegi" + File.separator + fileName);
        Long reference = downloadManager.enqueue(request);
        //printtoast("Feladatlap letöltve.");
        if (mpdf) {
            String mlink = "http://dload.oktatas.educatio.hu/erettsegi/feladatok_20" + ev + evszak + "_" + szint + "/" + szintbetu + "_" + targy + "_" + ev + honap + "_ut.pdf";
            linksplit = mlink.split("/");
            fileName = linksplit[linksplit.length - 1];
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            uri = Uri.parse(mlink);
            request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + "Erettsegi" + File.separator + fileName);
            reference = downloadManager.enqueue(request);
            printtoast(fileName + " letöltése folyamatban...");
            megoldas.setChecked(false);
            mpdf = Boolean.FALSE;
        } else if (!mpdf) {
          //TODO pdf megnyitas ha nincs megoldas pdf, gomb ertekeket cserelje
            }
    }
    public void printtoast(String szoveg) {
        Toast.makeText(this, szoveg, Toast.LENGTH_SHORT).show();
    }

    private void permission_check() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                return;
            }
        }

        initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initialize();
        } else {
            permission_check();
        }
    }
    public void viewDownloads(View v) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    public void itemClicked_o(View v) {
        oktober = (CheckBox)v;
        if (oktober.isChecked()) {
            //Set evszak,honap
            honap = "okt";
            evszak = "osz";
            printtoast(evszak);
            majus = (CheckBox) findViewById(R.id.majus);
            majus.setChecked(false);
        }
    }

    public void szintkapcs(View v) {
        szintkapcs = (Switch)v;
        if (szintkapcs.isChecked()) {
            szint = "emelt";
            szintbetu = "e";
            printtoast("Emelt kiválasztva.");
        } else {
            szint = "kozep";
            szintbetu = "k";
            printtoast("Közép kiválasztva.");
        }
    }

    public void itemClicked_m(View v) {
        majus = (CheckBox)v;
        if (majus.isChecked()) {
            //Set evszak,honap
            honap = "maj";
            evszak = "tavasz";
            printtoast(evszak);
            oktober = (CheckBox) findViewById(R.id.oktober);
            oktober.setChecked(false);
        }
    }
    public void megoldascheck(View v) {
        megoldas = (CheckBox)v;
        if (megoldas.isChecked()) {
            //Set megoldas;
            mpdf = Boolean.TRUE;
            //button.setText("Letöltés");
        } else if (!megoldas.isChecked()) {
            //set megoldas
            mpdf = Boolean.FALSE;
            printtoast("megoldas uncheck");
            //button.setText("Letöltés és megnyitás");

        }
    }

    public void Update() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Biztosan frissíteni szeretnéd? Lehet hogy nem stabil a legfrisebb változat!\n(A frissítés jelenleg csak wifivel lehetséges.)")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printtoast("Rendben, máris frissítjük az alkalmazást.");
                        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        String apkurl = "https://github.com/MatyiFKBT/ErettsegiDroid/raw/master/app/build/outputs/apk/debug/app-debug.apk";
                        Uri Download_Uri = Uri.parse(apkurl);
                        DownloadManager.Request u_request = new DownloadManager.Request(Download_Uri);
                        u_request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        u_request.setAllowedOverRoaming(false);
                        u_request.setTitle("My Android App Update");
                        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Erettsegi/";
                        File update = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Erettsegi/update.apk");
                        if (update.exists()) {
                            update.delete();
                            printtoast("Előző update.apk sikeresen törölve.");
                        }
                        u_request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + "Erettsegi" + File.separator + "update.apk");
                        downloadReference = downloadManager.enqueue(u_request);
                        Log.d("ADebugtag", "Value:" + downloadReference);
                        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                })
                .setNegativeButton("Nem, majd máskor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printtoast("Oké, akkor majd ha készen állsz, frissítsd");
                    }
                });
        builder.create().show();

    }

    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Erettsegi/";
            String fileName = "update.apk";
            destination += fileName;
            final Uri fileuri = Uri.parse("file://" + destination);
            Intent installIntent = new Intent(Intent.ACTION_VIEW)
                    .setData(fileuri)
                    .setType("application/android.com.app");
            Log.d("ADebugTag", "Value: " + fileuri);
            Log.d("DebugTag", installIntent.getType());
            installIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            printtoast("indulhat a telepítés jejeje");
            startActivity(installIntent);
            unregisterReceiver(this);
            finish();
        }
    };


    public void initialize() {
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        mpdf = Boolean.FALSE;
        szint = "kozep";
        szintbetu = "k";
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

        button2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Update();
                return true;
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent marketpdf = new Intent(Intent.ACTION_VIEW);
                printtoast("Ezt töltsd le, hogy meg tudd nyitni a letöltött pdf fájlt.");
                marketpdf.setData(Uri.parse("market://details?id=com.google.android.apps.pdfviewer"));
                startActivity(marketpdf);
                return true;
            }
        });

    }
}

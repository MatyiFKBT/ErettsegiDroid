package pw.matyi.erettsegi;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {
    EditText evinput;
    Spinner spinner;
    CheckBox oktober, majus, megoldas;
    Button button;
    String honap,evszak;
    Boolean mpdf;
    DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        evinput = (EditText)findViewById(R.id.ev);
        permission_check();


    }

    private void download() {
        String ev = evinput.getText().toString();
        spinner = (Spinner)this.findViewById(R.id.spinner);
        String targy = getResources().getStringArray(R.array.targy_values)[spinner.getSelectedItemPosition()];
        String link = "http://dload.oktatas.educatio.hu/erettsegi/feladatok_20"+ev+evszak+"_emelt/e_"+targy+"_"+ev+honap+"_fl.pdf";
        String[] linksplit = link.split("/");
        String fileName = linksplit[linksplit.length-1];
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + "Erettsegi" + File.separator + fileName);
        Long reference = downloadManager.enqueue(request);
        //Toast.makeText(this, "Tárgy: " + targy + "letöltve.", Toast.LENGTH_SHORT).show();
        if(mpdf){
            String mlink = "http://dload.oktatas.educatio.hu/erettsegi/feladatok_20"+ev+evszak+"_emelt/e_"+targy+"_"+ev+honap+"_ut.pdf";
            linksplit = mlink.split("/");
            fileName = linksplit[linksplit.length - 1];
            downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            uri = Uri.parse(mlink);
            request = new DownloadManager.Request(uri);
            //downloadManager.enqueue(request);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + "Erettsegi" + File.separator + fileName);
            reference = downloadManager.enqueue(request);
            Toast.makeText(this, "Megoldás letöltve.", Toast.LENGTH_SHORT).show();
            megoldas.setChecked(false);
            mpdf = Boolean.FALSE;
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
        }
    }
    public void itemClicked_m(View v) {
        majus = (CheckBox)v;
        if (majus.isChecked()) {
            //Set evszak,honap
            honap = "maj";
            evszak = "tavasz";
            printtoast(evszak);
        }
    }
    public void megoldascheck(View v) {
        megoldas = (CheckBox)v;
        if (megoldas.isChecked()) {
            //Set megoldas;
            mpdf = Boolean.TRUE;
        }
    }
    private void initialize() {
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent marketpdf = new Intent(Intent.ACTION_VIEW);
                marketpdf.setData(Uri.parse("market://details?id=com.google.android.apps.pdfviewer"));
                startActivity(marketpdf);
                return true;
            }
        });

    }
}

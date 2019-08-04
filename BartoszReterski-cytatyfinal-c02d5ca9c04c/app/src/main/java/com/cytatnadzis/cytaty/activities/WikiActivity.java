package com.cytatnadzis.cytaty.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cytatnadzis.cytaty.R;

public class WikiActivity extends AppCompatActivity {

    // boolean do sprawdzanie stanu połączenia z internetem
    private boolean haveNetworkConnection(){
        boolean haveConnectedWifi= false;
        boolean haveConnectedMobile=false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);


        Intent intent = getIntent();
        String str = intent.getStringExtra("WIKIPEDIA_ADRESS");

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.loadUrl(str);

        InternetConnectionCheck();


    }


    // wlasciwe sprawdzanie połączeniz internetem wraz z komentarzami

    public void InternetConnectionCheck() {
        if (haveNetworkConnection()) {
            // Display message in dialog box if you have internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();

        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Brak połączenie z internetem");
            alertDialogBuilder.setMessage("Sprawdź stan swojego połączenia z Internetem");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

}

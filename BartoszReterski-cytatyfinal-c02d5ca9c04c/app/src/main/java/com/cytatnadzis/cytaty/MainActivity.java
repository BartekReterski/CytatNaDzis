package com.cytatnadzis.cytaty;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cytatnadzis.cytaty.activities.About_App_Acitivity;
import com.cytatnadzis.cytaty.activities.Favorite;
import com.cytatnadzis.cytaty.notifications.Notifications;
import com.cytatnadzis.cytaty.activities.WikiActivity;
import com.cytatnadzis.cytaty.database.DatabaseHelper;
import com.cytatnadzis.cytaty.model.QuoteModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private List<QuoteModel> mQuotesModelList;
    private DatabaseHelper mdatabaseHelper;
    private TextView mQuoteTextView;
    private TextView mWikiTextView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesCalendarData;
    private Toolbar mtoolbar;
    private TextToSpeech textToSpeech;
    private ConstraintLayout constraintLayout;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton mShare, mTTS, mFavourite;
    private AdView mAdView;



    //zadeklarowanie losowego tła z listy Intów
    Integer[] mLinearBackgrounds = {
            R.drawable.paperfolded,
            R.drawable.notepad,
            R.drawable.pink,
            R.drawable.burnedpaper,
            R.drawable.burnedpape2,
            R.drawable.leafframe,
            R.drawable.leather,
            R.drawable.spot,
            R.drawable.yellow,
            R.drawable.yellowleafs

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



// inicjacja reklam Google Admob
       MobileAds.initialize(this,"ca-app-pub-7746007065654957~3833214947");

       mAdView=findViewById(R.id.adView);
       AdRequest adRequest= new AdRequest.Builder().build();
       mAdView.loadAd(adRequest);

        mQuoteTextView= findViewById(R.id.quoteTextview);
        mWikiTextView= findViewById(R.id.wikiTextvView);
        materialDesignFAM =  findViewById(R.id.material_design_android_floating_action_menu);
        mShare =  findViewById(R.id.material_design_floating_action_menu_item1);
        mTTS = findViewById(R.id.material_design_floating_action_menu_item2);
        mFavourite=  findViewById(R.id.material_design_floating_action_menu_item3);
        constraintLayout=findViewById(R.id.constraint_layout);

        mtoolbar=  findViewById(R.id.toolbar);
        mtoolbar.setTitleTextColor(getResources().getColor(R.color.bialy));
        setSupportActionBar(mtoolbar);







// deklaracaja polczenia bazy danych z aktywnoscia

        mdatabaseHelper= new DatabaseHelper(this);

        File database= getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if(!database.exists()){
            mdatabaseHelper.getReadableDatabase();

            if(copyDatabase(this)){
//                Toast.makeText(this,"Copy database succes",Toast.LENGTH_SHORT).show();

            }else{
//                Toast.makeText(this,"Copy database error",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ButtonsLogic();
//        SaveQuote();


        // wykonuje kod co 24 h tylko raz
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        sharedPreferences = getSharedPreferences("PREFS", 0);
        int lastDay = sharedPreferences.getInt("day", 0);

        if (lastDay != currentDay) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("day", currentDay);
            editor.apply();
SaveQuote();

        }
//odebranie danych tymczasowych na temat wiki i cytatu
        sharedPreferencesCalendarData= getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String quoteText= sharedPreferencesCalendarData.getString("Text","");
        String quoteWiki= sharedPreferencesCalendarData.getString("Wiki","");
        int quoteLayout= sharedPreferencesCalendarData.getInt("Background",0);
        mQuoteTextView.setText(quoteText);
        mWikiTextView.setText(quoteWiki);
        constraintLayout.setBackgroundResource(mLinearBackgrounds[quoteLayout]);

    }

    private void SaveQuote(){

        mQuotesModelList= mdatabaseHelper.getListQuotes();
        mQuoteTextView.setText(mQuotesModelList.get(0).getQuoteText());
        mWikiTextView.setText(mQuotesModelList.get(0).getQuoteWiki());
//losowe wybieranie tła z tablicy
        Random randomBackground= new Random();
        int randomDrawable= randomBackground.nextInt(mLinearBackgrounds.length);


// wyslanie danych tymczasowych na temat wiki i cytatu
        sharedPreferencesCalendarData= getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorquotes= sharedPreferencesCalendarData.edit();
        editorquotes.putString("Text",mQuoteTextView.getText().toString());
        editorquotes.putString("Wiki",mWikiTextView.getText().toString());
        editorquotes.putInt("Background",randomDrawable);


        editorquotes.apply();


    }



// skopiowanie bazy danych z assets
    private boolean copyDatabase(Context context){

        try{
            InputStream inputStream =context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName= DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream= new FileOutputStream(outFileName);
            byte[] buff= new byte[1024];
            int length;
            while((length= inputStream.read(buff))>0){
                outputStream.write(buff,0,length);
            }
            outputStream.flush();
            outputStream.close();
            Log.v("MainActivity","DB Copied");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.wikipedia) {
            String str = mWikiTextView.getText().toString();
            Intent intent = new Intent(getBaseContext(), WikiActivity.class);
            intent.putExtra("WIKIPEDIA_ADRESS",str);
            startActivity(intent);

            return true;
        }


        if(id==R.id.action_settings){

            Intent intent= new Intent(getBaseContext(), Notifications.class);
            startActivity(intent);

            return true;
        }

        if(id==R.id.informacje_o_aplikacji){

          Intent intent= new Intent(getBaseContext(), About_App_Acitivity.class);
          startActivity(intent);
        }
        if (id == R.id.favorites) {

            Intent intent = new Intent(getBaseContext(), Favorite.class);
            startActivity(intent);

            return true;
        }



        return super.onOptionsItemSelected(item);
    }
// logika przyciskow share oraz wiki i favorite
    private void  ButtonsLogic() {


               mShare.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String s = mQuoteTextView.getText().toString();
                       Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                       sharingIntent.setType("text/plain");
                       sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Temat");
                       sharingIntent.putExtra(Intent.EXTRA_TEXT, s);
                       startActivity(Intent.createChooser(sharingIntent, "Wyślij cytat poprzez:"));



                   }
               });





              mTTS.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      final String convertTextToSpeech = mQuoteTextView.getText().toString();

                      textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                          @Override
                          public void onInit(int status) {
                              if (status != TextToSpeech.ERROR) {
                                  textToSpeech.setLanguage(new Locale("pl", "PL"));
                                  textToSpeech.speak(convertTextToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
                              } else
                                  Toast.makeText(getBaseContext(), "Do prawidłowego działania tej funkcji konieczne jest jednorazowe połączenie z internetem", Toast.LENGTH_LONG).show();
                          }
                      });

    }
});

              mFavourite.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mdatabaseHelper.FavoriteSQL();


                      Intent intent= new Intent(getBaseContext(), Favorite.class);
                      startActivity(intent);
                  }
              });


            }


    }






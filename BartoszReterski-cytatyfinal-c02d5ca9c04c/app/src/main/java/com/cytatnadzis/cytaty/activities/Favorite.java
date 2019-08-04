package com.cytatnadzis.cytaty.activities;

import android.graphics.Rect;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.cytatnadzis.cytaty.dataadapter.FavoriteAdapter;
import com.cytatnadzis.cytaty.R;
import com.cytatnadzis.cytaty.database.DatabaseHelper;
import com.cytatnadzis.cytaty.model.QuoteModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class Favorite extends AppCompatActivity {


private List<QuoteModel> quoteModelList= new ArrayList<>();
private FavoriteAdapter favoriteAdapter;
private RecyclerView recyclerView;
private DatabaseHelper mDatabaseHelper;
private Toolbar toolbar;
private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        toolbar=  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.bialy));
        toolbar.setTitle("Ulubione Cytaty");
        setSupportActionBar(toolbar);

        MobileAds.initialize(this,"ca-app-pub-7746007065654957~3833214947");

        mAdView=findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        recyclerView= findViewById(R.id.recyclerView_favorite);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.fab_actions_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseHelper= new DatabaseHelper(this);
       
//sprawdzanie czy lista jest pusta
        quoteModelList= mDatabaseHelper.getListFavorite();
        if (quoteModelList != null && quoteModelList.isEmpty()){
            Toast.makeText(this,"Lista ulubionych cytatów jest pusta",Toast.LENGTH_LONG).show();
        }

//deklaracja zawartosci adaptera w postaci listy ulubionych z bazy danych
        favoriteAdapter= new FavoriteAdapter(this,quoteModelList);

        recyclerView.setAdapter(favoriteAdapter);



    }
// dodatkowe miejsce pomiędzy elementami RecyclerView
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;


            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }



}

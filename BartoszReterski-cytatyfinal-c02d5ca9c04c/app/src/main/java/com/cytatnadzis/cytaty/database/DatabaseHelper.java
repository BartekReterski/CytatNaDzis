package com.cytatnadzis.cytaty.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.cytatnadzis.cytaty.model.QuoteModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {



    public static final String DBNAME = "Cytaty.db";

    @SuppressLint("SdCardPath")

    public static final String DBLOCATION = "/data/data/com.cytatnadzis.cytaty/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;





    public DatabaseHelper(Context context) {

        super(context, DBNAME, null, 1);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void openDatabase() {

        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {

        if (mDatabase != null) {
            mDatabase.close();
        }

    }

    public List<QuoteModel> getListQuotes() {
        QuoteModel quotesModel = null;
        List<QuoteModel> quotesModelList = new ArrayList<>();
        openDatabase();


        Cursor cursor = mDatabase.rawQuery("SELECT * FROM quotes", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            quotesModel = new QuoteModel(cursor.getString(0),cursor.getString(1),cursor.getInt(2));
            quotesModelList.add(quotesModel);
            cursor.moveToNext();
        }

        cursor.close();

        Quotes();
        closeDatabase();

        return quotesModelList;
    }


//lista odpowiedzialna za przechowywanie ulubionych cytatow
    public List<QuoteModel> getListFavorite() {
        QuoteModel quotesModel = null;
        List<QuoteModel> quotesModelListFavorite = new ArrayList<>();
        openDatabase();


        Cursor cursor = mDatabase.rawQuery("SELECT * FROM favorite", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            quotesModel = new QuoteModel(cursor.getString(0),cursor.getString(1),cursor.getInt(2));

            quotesModelListFavorite.add(quotesModel);
            cursor.moveToNext();
        }

        cursor.close();


        closeDatabase();

        return quotesModelListFavorite;
    }



    private void Quotes(){


       openDatabase();


        String query1;
        String query2;
        String query3;
        String query4;


// dodaj quotes do quotes2 pojedynczo
        query1= "INSERT INTO quotes2(quoteText, quoteWiki)\n" +
                "SELECT quoteText, quoteWiki FROM quotes LIMIT 1;";

//usun z quotes element kotry zostal wlozony do quotes2
        query2="DELETE from quotes\n" +
                "WHERE quoteText= (\n" +
                "  SELECT quoteText from quotes2\n" +
                "  WHERE rowid = (SELECT MAX(rowid) FROM quotes2)\n" +
                ")\n";


// skopiuj dane z quotes 2 do quotes (jesli quotes jest puste- ma zero rekordow

        query3="INSERT INTO quotes(quoteText, quoteWiki)\n" +
                "SELECT quoteText, quoteWiki FROM quotes2\n" +
                "WHERE (SELECT COUNT(*) FROM quotes) = 0";


//Usun z tabeli quotes2 rowy jesli jest ich 10
/////ZMIEN NA LICZBE WIERSZY USTALONA WCZESNIEJ
        query4="DELETE FROM quotes2 \n" +
                " WHERE (SELECT COUNT(*) FROM quotes2) = 450";



        mDatabase.execSQL(query1);
        mDatabase.execSQL(query2);
        mDatabase.execSQL(query3);
        mDatabase.execSQL(query4);


        closeDatabase();

    }

    public void FavoriteSQL(){


        openDatabase();

        String query1;
        String query2;


// dodanie ostanio dodanego wiersza z quotes2 do favorite

        query1="\n" +
                "INSERT INTO favorite(quoteText, quoteWiki) SELECT quoteText, quoteWiki FROM quotes2\n" +
                "WHERE ROWID = (SELECT MAX(ROWID) from quotes2);";

//usuniecie potencjalnych duplikatow  w favorites

        query2="delete from favorite\n" +
                "where rowid not in (select min(rowid)\n" +
                "                    from favorite\n" +
                "                    group by quoteText)";


        // sprawdzanie czy został dodany nowy cytat jeśli tak to Toast
        mDatabase.beginTransaction();
        try{
            mDatabase.execSQL(query1);
            Toast.makeText(mContext,"Dodano ulubiony cytat",Toast.LENGTH_LONG).show();
            mDatabase.setTransactionSuccessful();
        }finally {
            mDatabase.endTransaction();
        }

        //usuwanie duplikatów
        mDatabase.execSQL(query2);


        closeDatabase();

    }





}


package com.cytatnadzis.cytaty.dataadapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cytatnadzis.cytaty.R;
import com.cytatnadzis.cytaty.activities.WikiActivity;
import com.cytatnadzis.cytaty.database.DatabaseHelper;
import com.cytatnadzis.cytaty.model.QuoteModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {



    private Context mCtx;
    private DatabaseHelper mDatabaseHolder;
    SQLiteDatabase sqLiteDatabase;



    private List<QuoteModel> quoteModelList;


    public FavoriteAdapter(Context mCtx, List<QuoteModel> quoteModelList) {
        this.mCtx = mCtx;
        this.quoteModelList = quoteModelList;


    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.favorite_items, null);
        return new FavoriteViewHolder(view);
    }



    // deklaracja danych z modelu i ustaleni ich logiki w OnClickListener

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {

        final QuoteModel quoteModel= quoteModelList.get(position);


        holder.quoteText.setText(quoteModel.getQuoteText());
        holder.quoteWiki.setText(quoteModel.getQuoteWiki());






        holder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Temat");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, quoteModel.getQuoteText());
               v.getContext().startActivity(Intent.createChooser(sharingIntent, "Wyślij cytat poprzez:"));
            }
        });



        holder.mWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = quoteModel.getQuoteWiki();
                Intent intent = new Intent(mCtx, WikiActivity.class);
                intent.putExtra("WIKIPEDIA_ADRESS",str);
               v.getContext().startActivity(intent);
            }
        });




        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// implementacja dialogu, który zapyta czy aby na pewno usunąc cytat

                final AlertDialog.Builder builder= new AlertDialog.Builder(mCtx,R.style.AlertDialogStyle);
                builder.setTitle("Usuwanie cytatu");
                builder.setMessage("Czy naprawdę chcesz usunąć ten cytat z listy ulubionych?");
                DialogInterface.OnClickListener dialogClickListener= new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                //usuniecie z listy  z recyclerview
                                quoteModelList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,quoteModelList.size());


                               //usuniecie z sqlite
                                mDatabaseHolder= new DatabaseHelper(mCtx);
                                int ID= quoteModel.getID();

                                mDatabaseHolder.openDatabase();
                                String table = "favorite";
                                String whereClause = "ID=?";
                                String[] whereArgs = new String[] {String.valueOf(ID)};
                                SQLiteDatabase sqLiteDatabase = mDatabaseHolder.getWritableDatabase();
                                sqLiteDatabase.delete(table, whereClause, whereArgs);
                                mDatabaseHolder.closeDatabase();
                                Toast.makeText(mCtx,"Usunięto cytat",Toast.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEUTRAL:

                                break;



                        }


                    }
                };

                builder.setPositiveButton("Tak",dialogClickListener);

                builder.setNeutralButton("Cofnij",dialogClickListener);

                AlertDialog dialog=builder.create();

                dialog.show();




            }
        });


    }





    @Override
    public int getItemCount() {
        return quoteModelList.size();
    }


    class FavoriteViewHolder extends RecyclerView.ViewHolder {

        TextView quoteText, quoteWiki;
        FloatingActionMenu materialDesignFAM;
        FloatingActionButton mShare, mWiki, mDelete;


        public FavoriteViewHolder(View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quoteTextview);
            quoteWiki = itemView.findViewById(R.id.wikiTextvView);


            materialDesignFAM= itemView.findViewById(R.id.material_design_android_floating_action_menu);
            mShare=itemView.findViewById(R.id.material_design_floating_action_menu_item1);
            mWiki=itemView.findViewById(R.id.material_design_floating_action_menu_item3);
            mDelete=itemView.findViewById(R.id.material_design_floating_action_menu_item4);

        }


    }


}

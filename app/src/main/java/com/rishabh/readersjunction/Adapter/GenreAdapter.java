/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:31 AM
 *  * Last modified 7/26/19 9:25 AM
 *
 */

package com.rishabh.readersjunction.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.rishabh.readersjunction.Activities.HomeActivity;
import com.rishabh.readersjunction.DataModels.GenreDataModel;
import com.rishabh.readersjunction.Fragments.BookListFragment;
import com.rishabh.readersjunction.R;
import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

  private ArrayList<GenreDataModel> dataSet;
  private Context context;

  public GenreAdapter(ArrayList<GenreDataModel> data, Context context) {
    this.dataSet = data;
    this.context = context;
  }


  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.genre_tile, parent, false);
    return new ViewHolder(view);
  }


  @Override
  public long getItemId(int position) {
    return position;
  }


  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, final int listPosition) {
    final GenreDataModel dataModel = dataSet.get(listPosition);
    TextView title = holder.title;
    ImageView icon = holder.icon;
    title.setText(dataModel.getBookGenre());
    Glide.with(context).load(dataModel.getBookCover()).into(icon);
    holder.cardView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //opening booklist page as per genre
        Fragment fragment = new BookListFragment();
        Bundle bundle = new Bundle();
        if (dataModel.getBookGenre().equals("Others")) {
          bundle.putString("genre", "");
        } else {
          bundle.putString("genre", dataModel.getBookGenre());
        }
        fragment.setArguments(bundle);
        loadFragment(fragment);
      }
    });
  }


  @Override
  public int getItemCount() {
    return dataSet.size();
  }


  private void loadFragment(android.support.v4.app.Fragment fragment) {
    HomeActivity homeScreen = (HomeActivity) context;
    FragmentTransaction transaction = homeScreen.getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView title;
    ImageView icon;

    ViewHolder(View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.title);
      icon = itemView.findViewById(R.id.icon);
      cardView = itemView.findViewById(R.id.card);
    }

  }


}


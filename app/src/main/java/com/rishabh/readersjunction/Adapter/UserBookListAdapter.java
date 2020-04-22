/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 9:57 PM
 *
 */

package com.rishabh.readersjunction.Adapter;

import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_AUTHOR;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_COVER;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_CREDIT;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_DESC;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_EXCHANGE_COUNT;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_GENRE;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_ID;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_NAME;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_STATUS;
import static com.rishabh.readersjunction.Fragments.BookCatalagFragment.BOOK_USER_NAME;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.rishabh.readersjunction.Activities.HomeActivity;
import com.rishabh.readersjunction.DataModels.BookDataModel;
import com.rishabh.readersjunction.Fragments.BookCatalagFragment;
import com.rishabh.readersjunction.R;
import java.util.ArrayList;

public class UserBookListAdapter extends RecyclerView.Adapter<UserBookListAdapter.ViewHolder> {

  private ArrayList<BookDataModel> dataSet;
  private Context context;

  public UserBookListAdapter(ArrayList<BookDataModel> data, Context context) {
    this.dataSet = data;
    this.context = context;
  }


  @NonNull
  @Override
  public UserBookListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.book_list_item, parent, false);
    return new UserBookListAdapter.ViewHolder(view);
  }


  @Override
  public long getItemId(int position) {
    return position;
  }


  @Override
  public void onBindViewHolder(@NonNull final UserBookListAdapter.ViewHolder holder,
      final int listPosition) {
    final BookDataModel dataModel = dataSet.get(listPosition);
    holder.title.setText(dataModel.getBookName());
    holder.status.setText(dataModel.getBookStatus());
    holder.author.setText(dataModel.getBookAuthor());
    holder.desc.setText(dataModel.getBookDescription());
    Glide.with(context)
        .load(dataModel.getBookCover())
        .into(holder.imageView);
    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //open book catalog.
        BookCatalagFragment fragment = new BookCatalagFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BOOK_ID, dataModel.getBookId());
        bundle.putInt(BOOK_CREDIT, dataModel.getCredits());
        bundle.putInt(BOOK_EXCHANGE_COUNT, dataModel.getBookExchangeCount());
        bundle.putString(BOOK_NAME, dataModel.getBookName());
        bundle.putString(BOOK_AUTHOR, dataModel.getBookAuthor());
        bundle.putString(BOOK_DESC, dataModel.getBookDescription());
        bundle.putString(BOOK_STATUS, dataModel.getBookStatus());
        bundle.putString(BOOK_USER_NAME, dataModel.getUserName());
        bundle.putString(BOOK_GENRE, dataModel.getBookGenre());
        bundle.putString(BOOK_COVER, dataModel.getBookCover());
        fragment.setArguments(bundle);
        loadFragment(fragment);
      }
    });
  }


  @Override
  public int getItemCount() {
    return dataSet.size();
  }

  private void loadFragment(Fragment fragment) {
    HomeActivity homeScreen = (HomeActivity) context;
    FragmentTransaction transaction = homeScreen.getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView title, author, desc, status;

    ViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.image);
      title = itemView.findViewById(R.id.book_title);
      author = itemView.findViewById(R.id.book_author);
      desc = itemView.findViewById(R.id.book_desc);
      status = itemView.findViewById(R.id.book_status);
    }

  }


}



/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 9:57 PM
 *
 */

package com.rishabh.readersjunction.Fragments;


import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.rishabh.readersjunction.Adapter.BookListAdapter;
import com.rishabh.readersjunction.DataModels.BookDataModel;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFragment extends Fragment {


  ArrayList<BookDataModel> dataModels;
  RecyclerView recyclerView;
  Context context;
  BookListAdapter bookListAdapter;

  public BookListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_book_list, container, false);
    context = getContext();
    dataModels = new ArrayList<>();
    recyclerView = view.findViewById(R.id.recyclerView);
    LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
        false);
    recyclerView.setLayoutManager(layoutManager);
    Bundle bundle = getArguments();
    if (bundle != null) {
      getListOfBooks(bundle.getString("genre"));
    } else {
      Toast.makeText(context, "bundle null", Toast.LENGTH_SHORT).show();
    }
    return view;
  }


  private void getListOfBooks(String genre) {
    Toast.makeText(context, genre, Toast.LENGTH_SHORT).show();
    Call<List<BookDataModel>> call = api.getBooksByGenre(genre);
    call.enqueue(new Callback<List<BookDataModel>>() {
      @Override
      public void onResponse(Call<List<BookDataModel>> call,
          Response<List<BookDataModel>> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            dataModels.addAll(response.body());
          }
          bookListAdapter = new BookListAdapter(dataModels, context);
          recyclerView.setAdapter(bookListAdapter);
        } else {
          showError("Something Went Wrong");
        }
      }

      @Override
      public void onFailure(Call<List<BookDataModel>> call, Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void showError(String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }


}

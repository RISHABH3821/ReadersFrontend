/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/26/19 9:25 AM
 *
 */

package com.rishabh.readersjunction.Fragments;


import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.rishabh.readersjunction.Activities.LoginActivity;
import com.rishabh.readersjunction.Adapter.GenreAdapter;
import com.rishabh.readersjunction.DataModels.GenreDataModel;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreListFragment extends Fragment {


  private Context context;
  private ArrayList<GenreDataModel> dataModelList;
  private GenreAdapter genreAdapter;

  public GenreListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_genre_list, container, false);
    context = getContext();
    dataModelList = new ArrayList<>();
    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
    LayoutManager layoutManager = new GridLayoutManager(context, 2);
    recyclerView.setLayoutManager(layoutManager);
    genreAdapter = new GenreAdapter(dataModelList, context);
    recyclerView.setAdapter(genreAdapter);
    getGenres();
    return view;
  }


  private void getGenres() {
    Call<List<GenreDataModel>> call = api.getGenres();
    call.enqueue(new Callback<List<GenreDataModel>>() {
      @Override
      public void onResponse(@NotNull Call<List<GenreDataModel>> call,
          @NotNull Response<List<GenreDataModel>> response) {
        if(response.isSuccessful()){
          if (response.body() != null) {
            dataModelList.addAll(response.body());
            genreAdapter.notifyDataSetChanged();
          }else{
            showMessage("No genre found");
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<List<GenreDataModel>> call, @NotNull Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showMessage("Not Connected to Internet");
        } else {
          showMessage("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void showMessage(String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }


}

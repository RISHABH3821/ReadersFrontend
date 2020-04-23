/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 9:57 PM
 *
 */

package com.rishabh.readersjunction.Fragments;

import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rishabh.readersjunction.Activities.LoginActivity;
import com.rishabh.readersjunction.Activities.UploadBookActivity;
import com.rishabh.readersjunction.Adapter.UserBookListAdapter;
import com.rishabh.readersjunction.DataModels.BookDataModel;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


  ImageView logoutbtn;
  FloatingActionButton uploadBookButton;
  UserBookListAdapter bookListAdapter;
  private ArrayList<BookDataModel> dataModels;
  private RecyclerView recyclerView;
  private String user_name;

  public ProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.activity_profile, container, false);
    dataModels = new ArrayList<>();
    user_name = PreferenceManager
        .getDefaultSharedPreferences(getContext()).getString(USER_NAME, "null");
    TextView userName = view.findViewById(R.id.user_name);
    userName.setText(user_name);
    uploadBookButton = view.findViewById(R.id.upload_book_button);
    uploadBookButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getContext(), UploadBookActivity.class));
      }
    });
    logoutbtn = view.findViewById(R.id.logoutbtn);
    logoutbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
            .putString(USER_NAME, null).apply();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
            .putBoolean("signed", false).apply();
        Intent in = new Intent(getContext(), LoginActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        Objects.requireNonNull(getActivity()).finish();
      }
    });
    recyclerView = view.findViewById(R.id.recyclerView);
    recyclerView.setNestedScrollingEnabled(false);
    LayoutManager layoutManager = new LinearLayoutManager(getContext(),
        LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    getBooksFromUserName();
    return view;
  }


  private void getBooksFromUserName() {
    Call<List<BookDataModel>> call = api.getBooksByUser(user_name);
    call.enqueue(new Callback<List<BookDataModel>>() {
      @Override
      public void onResponse(Call<List<BookDataModel>> call,
          Response<List<BookDataModel>> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            dataModels.addAll(response.body());
          }
          bookListAdapter = new UserBookListAdapter(dataModels, getContext());
          recyclerView.setAdapter(bookListAdapter);
        } else {
          showError("Something Went Wrong");
        }
      }

      @Override
      public void onFailure(Call<List<BookDataModel>> call, Throwable t) {
        if (!new InternetService(getContext()).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void showError(String msg) {
    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
  }


}

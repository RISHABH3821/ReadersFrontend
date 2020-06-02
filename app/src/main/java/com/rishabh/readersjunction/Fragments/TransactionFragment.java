/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 11:19 PM
 *
 */

package com.rishabh.readersjunction.Fragments;

import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.rishabh.readersjunction.Adapter.TransactionAdapter;
import com.rishabh.readersjunction.DataModels.TransactionDataModel;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {

  private RecyclerView recyclerView;
  private TransactionAdapter adapter;
  private String user_name;
  private ArrayList<TransactionDataModel> dataModels;
  private FrameLayout frameLayout;

  public TransactionFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_transaction, container, false);
    frameLayout = view.findViewById(R.id.imageLayout);
    recyclerView = view.findViewById(R.id.recyclerView);
    dataModels = new ArrayList<>();
    LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,
        false);
    recyclerView.setLayoutManager(layoutManager);
    user_name = PreferenceManager
        .getDefaultSharedPreferences(getContext()).getString(USER_NAME, "null");
    getAllTransactions();
    adapter = new TransactionAdapter(dataModels, getContext());
    recyclerView.setAdapter(adapter);
    adapter.registerAdapterDataObserver(new AdapterDataObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        if(dataModels.isEmpty()){
          frameLayout.setVisibility(View.VISIBLE);
        }else{
          frameLayout.setVisibility(View.GONE);
        }
      }
    });
    return view;
  }


  private void getAllTransactions() {
    Call<List<TransactionDataModel>> call = api.getAllTransactions(user_name);
    call.enqueue(new Callback<List<TransactionDataModel>>() {
      @Override
      public void onResponse(Call<List<TransactionDataModel>> call,
          Response<List<TransactionDataModel>> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            dataModels.addAll(response.body());
            adapter.notifyDataSetChanged();
          }

        }
      }

      @Override
      public void onFailure(Call<List<TransactionDataModel>> call, Throwable t) {
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

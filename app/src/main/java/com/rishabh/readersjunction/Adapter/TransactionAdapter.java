/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:30 AM
 *  * Last modified 7/26/19 9:56 AM
 *
 */

package com.rishabh.readersjunction.Adapter;

import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.rishabh.readersjunction.Activities.HomeActivity;
import com.rishabh.readersjunction.DataModels.TransactionDataModel;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

  private ArrayList<TransactionDataModel> dataSet;
  private Context context;
  private String user_name;

  public TransactionAdapter(ArrayList<TransactionDataModel> data, Context context) {
    this.dataSet = data;
    this.context = context;
  }


  @NonNull
  @Override
  public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.transaction_item, parent, false);
    user_name = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_NAME, "");
    return new TransactionAdapter.ViewHolder(view);
  }


  @Override
  public long getItemId(int position) {
    return position;
  }


  @Override
  public void onBindViewHolder(@NonNull final TransactionAdapter.ViewHolder holder,
      final int listPosition) {
    final TransactionDataModel dataModel = dataSet.get(listPosition);
    //get book titile and update holder.bookTitle
    //get message too from server.
    if (dataModel.getStatusTransaction().equals("requested") && dataModel.getUserNameBuyer()
        .equals(user_name)) {
      holder.message.setText(
          new StringBuilder().append("Dear ").append(user_name).append(" kindly wait for ")
              .append(dataModel.getUserNameSeller()).append(" to accept.").toString());
      holder.leftButton.setText("Cancel");
      holder.rightButton.setVisibility(View.INVISIBLE);
    } else if (dataModel.getStatusTransaction().equals("requested") && dataModel
        .getUserNameSeller().equals(user_name)) {
      holder.message.setText(new StringBuilder().append("Dear ").append(user_name).append(" ")
          .append(dataModel.getUserNameBuyer())
          .append(" wants to get your book, accept request to share your book.").toString());
      holder.rightButton.setVisibility(View.VISIBLE);
      holder.rightButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          //accept request of someone.
          acceptRequest(dataModel.getTransId(), listPosition);

        }
      });
      holder.rightButton.setText("Accept");
      holder.leftButton.setText("Reject");
    } else if (dataModel.getStatusTransaction().equals("pending") && dataModel.getUserNameBuyer()
        .equals(user_name)) {
      holder.message.setText("Contact Details of Seller");
      holder.leftButton.setText("Cancel");
      holder.rightButton.setVisibility(View.INVISIBLE);
    } else if (dataModel.getStatusTransaction().equals("pending") && dataModel
        .getUserNameSeller().equals(user_name)) {

      holder.message.setText("Contact Details of Buyer");
      holder.rightButton.setVisibility(View.VISIBLE);
      holder.rightButton.setText("Complete");
      holder.leftButton.setText("Discard");
      holder.rightButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          //mark this transaction as complete.
          finalizeTransaction(dataModel.getTransId(), listPosition);
        }
      });
    }

    holder.leftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        cancelTransaction(dataModel.getTransId(), listPosition);
      }
    });

    getBookNameById(dataModel.getBookId(), holder.bookTitle);


  }


  private void updateCreditsInHomeScreen(){
    HomeActivity homeScreen = (HomeActivity) context;
    homeScreen.getCreditsUpdated().onCreditsUpdated();
  }


  private void getBookNameById(int book_id, final TextView textView) {//getting book name from it's id.
    Call<String> call = api.getBookNameByID(book_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          textView.setText(response.body());
        } else {
          showError("Failed to fetch book name");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  //the owner of book will call this function to complete the transaction.
  private void finalizeTransaction(final int trans_id, final int index) {
    Call<String> call = api.completeTransaction(trans_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
          cancelTransaction(trans_id, index);
          updateCreditsInHomeScreen();
        } else {
          updateCreditsInHomeScreen();
          showError("Failed");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        updateCreditsInHomeScreen();
        if (!new InternetService(context).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  //both buyer and seller can call this function to remove transaction
  private void cancelTransaction(int trans_id, final int index) {
    Toast.makeText(context, "Calling", Toast.LENGTH_SHORT).show();
    Call<String> call = api.cancelRequest(trans_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        updateCreditsInHomeScreen();
        if (response.isSuccessful()) {
          Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
          dataSet.remove(index);
          notifyDataSetChanged();
        } else {
          showError("Failed");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        updateCreditsInHomeScreen();
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


  private void acceptRequest(int trans_id, final int index){
    Toast.makeText(context, "Calling", Toast.LENGTH_SHORT).show();
    Call<String> call = api.acceptRequest(trans_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        updateCreditsInHomeScreen();
        if (response.isSuccessful()) {
          Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
          dataSet.get(index).setStatusTransaction("pending");
          notifyDataSetChanged();
        } else {
          showError("Failed");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        updateCreditsInHomeScreen();
        if (!new InternetService(context).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
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

    Button leftButton, rightButton;
    TextView bookTitle, message;

    ViewHolder(View itemView) {
      super(itemView);
      leftButton = itemView.findViewById(R.id.left_button);
      rightButton = itemView.findViewById(R.id.right_button);
      bookTitle = itemView.findViewById(R.id.book_title);
      message = itemView.findViewById(R.id.msg);
    }

  }


}



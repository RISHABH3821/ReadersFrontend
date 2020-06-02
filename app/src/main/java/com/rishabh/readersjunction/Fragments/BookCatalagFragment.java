/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 11:10 PM
 *
 */

package com.rishabh.readersjunction.Fragments;


import static com.rishabh.readersjunction.Activities.HomeActivity.USER_ID;
import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.rishabh.readersjunction.Activities.HomeActivity.CreditsUpdated;
import com.rishabh.readersjunction.Activities.SplashScreen;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookCatalagFragment extends Fragment {

  public static final String BOOK_ID = "book_id";
  public static final String BOOK_CREDIT = "book_credit";
  public static final String BOOK_EXCHANGE_COUNT = "book_exchange_count";
  public static final String BOOK_NAME = "book_name";
  public static final String BOOK_AUTHOR = "book_author";
  public static final String BOOK_DESC = "book_desc";
  public static final String BOOK_STATUS = "book_status";
  public static final String BOOK_USER_NAME = "book_user_name";
  public static final String BOOK_GENRE = "book_genre";
  public static final String BOOK_COVER = "book_cover";
  private int book_id = 0;
  private Context context;
  private String user_name;
  private Button requestButton;
  private int creditCount = 0;
  private int book_credit = 10;
  private RatingBar ratingBar;
  private Button ratingButton;
  private RatingBar overallRatingBar;
  private LinearLayout ratingLayout;
  private CreditsUpdated creditsUpdated;

  public BookCatalagFragment() {
    // Required empty public constructor
  }


  public void setCreditsUpdated(
      CreditsUpdated creditsUpdated) {
    this.creditsUpdated = creditsUpdated;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    // and intitializing textviews and image
    View view = inflater.inflate(R.layout.fragment_book_catalag, container, false);
    context = getContext();
    user_name = PreferenceManager
        .getDefaultSharedPreferences(context).getString(USER_NAME, "null");
    Bundle bundle = getArguments();
    book_id = bundle.getInt(BOOK_ID, 0);
    book_credit = bundle.getInt(BOOK_CREDIT, 0);
    int exchange_count = bundle.getInt(BOOK_EXCHANGE_COUNT, 0);
    String book_name = bundle.getString(BOOK_NAME);
    String book_author = bundle.getString(BOOK_AUTHOR);
    String book_desc = bundle.getString(BOOK_DESC);
    String book_status = bundle.getString(BOOK_STATUS);
    String book_user_name = bundle.getString(BOOK_USER_NAME);
    final String book_cover = bundle.getString(BOOK_COVER);
    TextView bookName, bookCredit, bookExchangeCount, bookAuthor, bookDesc, bookStatus, bookUserName;
    bookUserName = view.findViewById(R.id.book_owner);
    bookUserName.setText("From " + book_user_name);
    bookCredit = view.findViewById(R.id.book_credit);
    bookName = view.findViewById(R.id.book_title);
    bookExchangeCount = view.findViewById(R.id.book_exchange_count);
    bookAuthor = view.findViewById(R.id.book_author);
    bookDesc = view.findViewById(R.id.book_desc);
    bookStatus = view.findViewById(R.id.book_status);
    bookCredit.setText(String.valueOf(book_credit));
    bookName.setText(book_name);
    bookExchangeCount.setText("Have been with " + String.valueOf(exchange_count) + " others.");
    bookAuthor.setText(book_author);
    bookDesc.setText(book_desc);
    bookStatus.setText("Currently " + book_status);
    final ImageView bookCover = view.findViewById(R.id.book_cover);
    ratingBar = view.findViewById(R.id.ratingBar);
    ratingButton = view.findViewById(R.id.rateButton);
    ratingLayout = view.findViewById(R.id.ratingLayout);
    if (URLUtil.isValidUrl(book_cover)) {
      Glide.with(context)
          .load(book_cover)
          .into(bookCover).onLoadFailed(context.getDrawable(R.drawable.no_image_found));
    } else {
      Call<String> call = SplashScreen.api.getImageFromS3(book_cover);
      call.enqueue(new Callback<String>() {
        @Override
        public void onResponse(Call<String> call, Response<String> response) {
          if (response.isSuccessful()) {
            Glide.with(context)
                .load(response.body())
                .into(bookCover).onLoadFailed(context.getDrawable(R.drawable.no_image_found));
          }
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
          Glide.with(context).load(R.drawable.no_image_found).into(bookCover);
        }
      });
    }
    requestButton = view.findViewById(R.id.request_button);
    overallRatingBar = view.findViewById(R.id.overall_rating);
    ratingButton = view.findViewById(R.id.rateButton);
    if (user_name.equals(book_user_name)) {
      //book belongs to current user
      requestButton.setVisibility(View.GONE);
      ratingLayout.setVisibility(View.VISIBLE);
      int user_id = Integer
          .parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(USER_ID, "0"));
      getUserRating(book_id, user_id);
    } else {
      requestButton.setVisibility(View.VISIBLE);
      ratingLayout.setVisibility(View.GONE);
    }
    requestButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        requestButton.setEnabled(false);
        getCredit(user_name);
      }
    });
    getTotalRating(book_id);
    ratingButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          if (ratingBar.getRating() > 0) {
            int user_id = Integer.parseInt(
                PreferenceManager.getDefaultSharedPreferences(context).getString(USER_ID, "0"));
            postRating(book_id, user_id, ratingBar.getRating());
          } else {
            showMessage("Select rating!");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    getTotalRating(book_id);

    return view;
  }


  private void postRating(int book_id, int user_id, Float rating) {
    Call<String> call = api.postRating(book_id, user_id, rating);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(@NotNull Call<String> call,
          @NotNull Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showMessage("Not Connected to Internet");
        } else {
          showMessage("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void getTotalRating(int book_id) {
    Call<String> call = api.getTotalRating(book_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(@NotNull Call<String> call,
          @NotNull Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            try {
              overallRatingBar.setRating(Float.parseFloat(response.body()));
            } catch (Exception e) {
              e.printStackTrace();
            }
            overallRatingBar.setIsIndicator(true);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showMessage("Not Connected to Internet");
        } else {
//          showMessage("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void getUserRating(int book_id, int user_id) {
    Call<String> call = api.getRating(book_id, user_id);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(@NotNull Call<String> call,
          @NotNull Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            ratingBar.setRating(Float.parseFloat(response.body()));
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
        if (!new InternetService(context).haveNetworkConnection()) {
          showMessage("Not Connected to Internet");
        } else {
//          showMessage("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void showMessage(String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }


  //getting user credit.
  private void getCredit(final String user_name) {
    Call<String> call = api.getCurrentCredit(user_name);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            creditCount = Integer.parseInt(response.body());
            if (book_credit <= creditCount) {
              sendRequest(user_name, book_id);
            } else {
              showError("You don't have enough credits");
            }
          }
        } else {
          showError("Failed");
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


  private void sendRequest(String user_name, int book_id) {
    Call<String> call = api.sendRequest(String.valueOf(book_id), user_name);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
          requestButton.setEnabled(false);
          creditsUpdated.onCreditsUpdated();
        } else {
          requestButton.setEnabled(true);
          Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        requestButton.setEnabled(true);
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

/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:29 AM
 *  * Last modified 7/26/19 10:18 AM
 *
 */

package com.rishabh.readersjunction.Activities;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.rishabh.readersjunction.Fragments.GenreListFragment;
import com.rishabh.readersjunction.Fragments.ProfileFragment;
import com.rishabh.readersjunction.Fragments.TransactionFragment;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

  private static final String FRAGMENT_OTHER = "fragment_other";
  private static final String HOME_FRAGMENT = "fragment_home";
  private BottomNavigationView bnve;
  private String user_name;
  private TextView creditScore;
  public static final String USER_ID = "user_id";

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
      = new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      android.support.v4.app.Fragment fragment;
      switch (item.getItemId()) {
        case R.id.bottombaritem_home:
          //making sure app exits when back key is pressed on home.
          FragmentManager fragmentManager = getSupportFragmentManager();
          Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
          if (currentFragment instanceof GenreListFragment) {
            Log.d("dev", "Genre Instance");
          } else {
            fragment = new GenreListFragment();
            viewFragment(fragment, HOME_FRAGMENT);
          }
          return true;
        case R.id.bottombaritem_exchange:
          Fragment fragment1 = new TransactionFragment();
          viewFragment(fragment1, FRAGMENT_OTHER);
          return true;
        case R.id.bottombaritem_profile:
          Fragment fragment2 = new ProfileFragment();
          viewFragment(fragment2, FRAGMENT_OTHER);
          return true;
      }
      return false;
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    user_name = PreferenceManager
        .getDefaultSharedPreferences(this).getString(USER_NAME, "null");
    bnve = findViewById(R.id.bnve);
    creditScore = findViewById(R.id.coin_amount);
    bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    viewFragment(new GenreListFragment(), HOME_FRAGMENT);
    getCredit(user_name);
    getUserId(user_name);
  }

  private void getUserId(String user_name) {
    Call<String> call = api.getUserId(user_name);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit()
              .putString(USER_ID, response.body()).apply();
        } else {
          showError("Failed");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(HomeActivity.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  //getting user credit.
  private void getCredit(String user_name) {
    Call<String> call = api.getCurrentCredit(user_name);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          creditScore.setText(response.body());
        } else {
          showError("Failed");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(HomeActivity.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  //this function loads fragments to home screen also decides if they should be added to back stack
  private void viewFragment(Fragment fragment, String name) {
    final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
        .beginTransaction();
    fragmentTransaction.replace(R.id.container, fragment);
    final int count = fragmentManager.getBackStackEntryCount();
    if (name.equals(FRAGMENT_OTHER)) {
      fragmentTransaction.addToBackStack(name);
    }
    fragmentTransaction.commit();
    fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
      @Override
      public void onBackStackChanged() {
        if (fragmentManager.getBackStackEntryCount() <= count) {
          fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
          fragmentManager.removeOnBackStackChangedListener(this);
          // set the home button selected
          bnve.getMenu().getItem(0).setChecked(true);
        }
      }
    });
  }


  private void showError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }


}

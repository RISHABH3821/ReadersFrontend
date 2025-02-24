package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.LoginActivity.USERPASSWORD;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import com.rishabh.readersjunction.Utils.API;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SplashScreen extends AppCompatActivity {

  private static final String url = "http://ec2-18-223-190-95.us-east-2.compute.amazonaws.com:3000/"; //base url
  public static API api = null; //single api for whole app

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    final Gson gson = new GsonBuilder()
        .setLenient()
        .create();
    //to set timeOut time of a call.
    OkHttpClient.Builder client = new Builder();
    client.connectTimeout(30, TimeUnit.SECONDS);
    client.readTimeout(5, TimeUnit.MINUTES);
    client.writeTimeout(5, TimeUnit.MINUTES);
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(url)
        .client(client.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    api = retrofit.create(API.class); // initializing api with retrofit
    ImageView imageView = findViewById(R.id.image);
    Glide.with(this).load(R.drawable.giphy).into(imageView);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        String username = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this)
            .getString(USER_NAME, "null");
        String user_pass = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this)
            .getString(USERPASSWORD, "null");
        if (!username.equals("null")) {
          login(username, user_pass);
        } else {
          startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
      }
    }, 2000);
  }


  //checking if user exists in db and if exists open home screen.
  private void login(final String username, final String password) {
    Call<String> call = api.performLogin(username, password);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            if (response.body().equals("Login Successful")) { // login is successful open Home
              PreferenceManager.getDefaultSharedPreferences(SplashScreen.this).edit()
                  .putString(USER_NAME, username).apply();
              PreferenceManager.getDefaultSharedPreferences(SplashScreen.this).edit()
                  .putString(USERPASSWORD, password).apply();
              startActivity(new Intent(SplashScreen.this, HomeActivity.class));
              SplashScreen.this.finish();
            } else { // login check failed open Login Screen
              PreferenceManager.getDefaultSharedPreferences(SplashScreen.this).edit()
                  .putString(USER_NAME, null).apply();
              PreferenceManager.getDefaultSharedPreferences(SplashScreen.this).edit()
                  .putString(USERPASSWORD, null).apply();
              startActivity(new Intent(SplashScreen.this, LoginActivity.class));
              SplashScreen.this.finish();
              showError(response.body());
            }
          }
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) { // on failure open Login page.
        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        SplashScreen.this.finish();
        if (!new InternetService(SplashScreen.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


  private void showError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }


}

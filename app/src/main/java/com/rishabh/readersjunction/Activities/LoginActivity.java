package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends Activity {

  //Creating variables
  public static final String USER_NAME = "user_name";
  public static final String USERPASSWORD = "user_password";
  private Button regbtn;
  private Button loginbtn;
  private EditText user, pass;


  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    getUserLoginDetails();
    // when we click on register button then move to register activity
    regbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
      }
    });
    // when we click on login button then move to login activity
    loginbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //change this activity destination
        if (confirmInput()) {
          String username = user.getText().toString();
          String password = pass.getText().toString();
          login(username, password);
        }
      }
    });
  }


  private void login(final String username, final String password) {
    Call<String> call = api.performLogin(username, password);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            if (response.body().equals("Login Successful")) {
              PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                  .putString(USER_NAME, username).apply();
              PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                  .putString(USERPASSWORD, password).apply();
              startActivity(new Intent(LoginActivity.this, HomeActivity.class));
              LoginActivity.this.finish();
            } else {
              showError(response.body());
            }
          }
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(LoginActivity.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });
  }


// toast if error occur like user id not match
  private void showError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }


// function for checking username or password correct or not
  private boolean confirmInput() {
    return !(!validateUsername() | !validatePassword());
  }


  // Validation for id is not equal to null
  private boolean validateUsername() {
    String usernameInput = user.getText().toString().trim();
    if (usernameInput.isEmpty()) {
      user.setError("Field can't be empty");
      return false;
    } else {
      user.setError(null);
      return true;
    }
  }

  // Validation for id is not equal to null

  private boolean validatePassword() {
    String passwordInput = pass.getText().toString().trim();
    if (passwordInput.isEmpty()) {
      pass.setError("Field can't be empty");
      return false;
    } else {
      pass.setError(null);
      return true;
    }
  }


  // function which helps to get all the data from xml file using find view by id
  private void getUserLoginDetails() {
    regbtn = findViewById(R.id.registerBtn);
    loginbtn = findViewById(R.id.loginBtn);
    user = findViewById(R.id.username);
    pass = findViewById(R.id.password);
  }
}


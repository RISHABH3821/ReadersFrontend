package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.SplashScreen.api;
import static com.rishabh.readersjunction.Activities.SplashScreen.userPool;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends Activity {

  //Creating variables
  public static final String USER_NAME = "useremail";
  public static final String USERPASSWORD = "user_password";
  private Button regbtn;
  private Button loginbtn;
  private EditText user, pass;
  private CognitoUserSession userSession;
  private CognitoDevice newDevice;
  private String password;
  private String useremail;

  AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
    @Override
    public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
      userSession = cognitoUserSession;
      newDevice = device;
      launchUser();
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation,
        String username) {
      Locale.setDefault(Locale.US);
      getUserAuthentication(authenticationContinuation, username);
    }

    @Override
    public void getMFACode(
        MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

    }

    @Override
    public void onFailure(Exception e) {
      loginbtn.setEnabled(true);

      AlertDialog.Builder builder;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
      } else {
        builder = new AlertDialog.Builder(LoginActivity.this);
      }
      builder.setTitle("Authentication Failure").setMessage("Incorrect Email or Password")
          .setIcon(android.R.drawable.ic_dialog_alert)
          .show();
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation continuation) {

    }
  };


  private void launchUser() {
    Call<String> call = api.getUserName(SplashScreen.user.getUserId());
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if(response.isSuccessful()){
          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(USER_NAME, response.body()).apply();
          startActivity(new Intent(LoginActivity.this, HomeActivity.class));
          LoginActivity.this.finish();
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        SplashScreen.user.signOut();
        loginbtn.setEnabled(true);
        Toast.makeText(LoginActivity.this, "Sign In Failed.", Toast.LENGTH_SHORT).show();
      }
    });

  }


  private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
    if (username != null) {
       this.useremail = username;
    }
    if (this.password == null) {
      user.setText(username);
      password = pass.getText().toString();
    }
    AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.useremail,
        password,
        null);
    continuation.setAuthenticationDetails(authenticationDetails);
    continuation.continueTask();
  }


  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    getUserLoginDetails();
    useremail = SplashScreen.user.getUserId();
    if (useremail != null && !pass.getText().toString().isEmpty()) {
      SplashScreen.user.getSessionInBackground(authenticationHandler);
    }
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
          loginbtn.setEnabled(false);
          useremail = user.getText().toString();
          password = pass.getText().toString();
          if(Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            userPool.getUser(useremail).getSessionInBackground(authenticationHandler);
          }else{
            Call<String> call = api.getUserEmail(useremail);
            call.enqueue(new Callback<String>() {
              @Override
              public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                  userPool.getUser(response.body()).getSessionInBackground(authenticationHandler);
                }else{
                  Toast.makeText(LoginActivity.this, "Username does not exist.", Toast.LENGTH_SHORT).show();
                  loginbtn.setEnabled(true);
                }
              }

              @Override
              public void onFailure(Call<String> call, Throwable t) {
                SplashScreen.user.signOut();
                Toast.makeText(LoginActivity.this, "Sign In Failed.", Toast.LENGTH_SHORT).show();
              }
            });
          }
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


// function for checking useremail or password correct or not
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


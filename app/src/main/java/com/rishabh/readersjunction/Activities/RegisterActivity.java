package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.LoginActivity.USERPASSWORD;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {


  // validation summary for password which contain alphabets , speacial character and mush be greater than 4 digit
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^" +
          //"(?=.*[0-9])" +         //at least 1 digit
          //"(?=.*[a-z])" +         //at least 1 lower case letter
          //"(?=.*[A-Z])" +         //at least 1 upper case letter
          "(?=.*[a-zA-Z])" +      //any letter
          "(?=.*[@#$%^&+=])" +    //at least 1 special character
          "(?=\\S+$)" +           //no white spaces
          ".{4,}" +               //at least 4 characters
          "$");

  private EditText name, uname, pass, email, mob, add;
  private Button regbtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    getUserLoginDetails();


    //  register on click listener and pass parameters to register function
    regbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (confirmInput()) {
          String full_name = name.getText().toString();
          String user_name = uname.getText().toString();
          String user_pass = pass.getText().toString();
          String user_email = email.getText().toString();
          String user_mob = mob.getText().toString();
          String user_add = add.getText().toString();
          register(full_name, user_name, user_pass, user_email, user_mob, user_add);
        }
      }
    });
  }


  private void register(String full_name, final String user_name, final String user_pass,
      String user_email,
      String user_mob, String user_add) {
    Call<String> call = api
        .performRegisteration(user_name, full_name, user_pass, user_email, user_mob, user_add);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit()
              .putString(USER_NAME, user_name).apply();
          PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit()
              .putString(USERPASSWORD, user_pass).apply();
          startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
          RegisterActivity.this.finish();
        } else {
          Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(RegisterActivity.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });


  }

// // toast if error occur like user id not match
  private void showError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }



  // function which check all the parameters of input type
  private boolean confirmInput() {
    if (!validateUsername() | !validateFullname() | !validatePassword() | !validateEmail()
        | !validateMobile() | !validateAddress()) {
      return false;
    }
    return true;
  }

  // validation for username ie. can't be empty and less than 55 characters
  private boolean validateFullname() {
    String fullnameInput = name.getText().toString().trim();

    if (fullnameInput.isEmpty()) {
      name.setError("Field can't be empty");
      return false;
    } else if (fullnameInput.length() > 55) {
      name.setError("Fullname too long");
      return false;
    } else {
      name.setError(null);
      return true;
    }
  }

  // validation for username ie. can't be empty and less than 15 characters
  private boolean validateUsername() {
    String usernameInput = uname.getText().toString().trim();

    if (usernameInput.isEmpty()) {
      uname.setError("Field can't be empty");
      return false;
    } else if (usernameInput.length() > 15) {
      uname.setError("Username too long");
      return false;
    } else {
      uname.setError(null);
      return true;
    }
  }

  // validation for password which cant be null and call password pattern
  private boolean validatePassword() {
    String passwordInput = pass.getText().toString().trim();

    if (passwordInput.isEmpty()) {
      pass.setError("Field can't be empty");
      return false;
    } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
      pass.setError("Password too weak");
      return false;
    } else {
      pass.setError(null);
      return true;
    }
  }


  // validation  for email that is for @ and .com
  private boolean validateEmail() {
    String emailInput = email.getText().toString().trim();

    if (emailInput.isEmpty()) {
      email.setError("Field can't be empty");
      return false;
    } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
      email.setError("Please enter a valid email address");
      return false;
    } else {
      email.setError(null);
      return true;
    }
  }


  // validation for mobile number must be of 10 digit
  private boolean validateMobile() {
    String mobileInput = mob.getText().toString().trim();

    if (mobileInput.isEmpty()) {
      mob.setError("Field can't be empty");
      return false;
    } else if (mobileInput.length() != 10) {
      mob.setError("Password Should be of 10 digit");
      return false;
    } else {
      mob.setError(null);
      return true;
    }
  }


//validation for address can't be NULL
  private boolean validateAddress() {
    String addressInput = add.getText().toString().trim();

    if (addressInput.isEmpty()) {
      add.setError("Field can't be empty");
      return false;
    } else {
      add.setError(null);
      return true;
    }
  }


  // get user function which get id details from xml
  private void getUserLoginDetails() {
    regbtn = findViewById(R.id.registerBtn);
    name = findViewById(R.id.fullname);
    uname = findViewById(R.id.username);
    pass = findViewById(R.id.password);
    email = findViewById(R.id.email);
    mob = findViewById(R.id.mob);
    add = findViewById(R.id.address);
  }
}

/*
 * *
 *  * Created by Rishabh Shrivastava on 23/4/20 11:21 AM
 *  * Last modified 23/4/20 11:21 AM
 *
 */

package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.LoginActivity.USERPASSWORD;
import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import static com.rishabh.readersjunction.Activities.SplashScreen.api;
import static com.rishabh.readersjunction.Activities.SplashScreen.user;
import static com.rishabh.readersjunction.Activities.SplashScreen.userPool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpConfirmActivity extends AppCompatActivity {


  VerificationHandler resendConfCodeHandler = new VerificationHandler() {
    @Override
    public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
      Toast.makeText(SignUpConfirmActivity.this, "Verification successful", Toast.LENGTH_SHORT)
          .show();
    }

    @Override
    public void onFailure(Exception exception) {
      Toast.makeText(SignUpConfirmActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
    }
  };


  private EditText confirmCode;
  private Button confirmButton;
  private TextView reqCode;
  private String full_name;
  private String user_name;
  private String user_email;
  private String user_mob;
  private String user_add;


  GenericHandler confHandler = new GenericHandler() {
    @Override
    public void onSuccess() {
      Call<String> call = api
          .performRegisteration(user_name, full_name, "", user_email, user_mob, user_add);
      call.enqueue(new Callback<String>() {
        @Override
        public void onResponse(Call<String> call, Response<String> response) {
          if (response.isSuccessful()) {
            PreferenceManager.getDefaultSharedPreferences(SignUpConfirmActivity.this).edit()
                .putString(USER_NAME, user_name).apply();
            launchUser();
          } else {
            Toast.makeText(SignUpConfirmActivity.this, response.body(), Toast.LENGTH_SHORT).show();
          }
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
          if (!new InternetService(SignUpConfirmActivity.this).haveNetworkConnection()) {
            showMessage("Not Connected to Internet");
          } else {
            showMessage("Something went wrong, Please Try again later.");
            Log.d("Error", t.toString());
          }
        }
      });
      //showDialogMessage("Success!",userName+" has been confirmed!", true);
    }


    private void showMessage(String msg) {
      Toast.makeText(SignUpConfirmActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Exception exception) {
      if (!new InternetService(SignUpConfirmActivity.this).haveNetworkConnection()) {
        Toast.makeText(SignUpConfirmActivity.this, "Not Connected to Internet", Toast.LENGTH_SHORT)
            .show();
      } else {
        Toast.makeText(SignUpConfirmActivity.this, "Something went wrong, Please Try again later.",
            Toast.LENGTH_SHORT).show();
        Log.d("Error", exception.toString());
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up_confirm);
    confirmCode = findViewById(R.id.editTextConfirmcode);
    confirmButton = findViewById(R.id.confirm_button);
    confirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendConfirmCode();
      }
    });

    reqCode = findViewById(R.id.resend_confirm_req);
    reqCode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        reqConfirmCode();
      }
    });

    confirmCode.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        TextView label = findViewById(R.id.textViewConfirmCodeMessage);
        label.setText("");
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      full_name = extras.getString("full_name");
      user_name = extras.getString("user_name");
      user_email = extras.getString("user_email");
      user_mob = extras.getString("user_mob");
      user_add = extras.getString("user_add");
    }

  }


  private void sendConfirmCode() {
    String confCode = confirmCode.getText().toString();
    if (confirmCode.length() < 1) {
      TextView label = findViewById(R.id.textViewConfirmCodeMessage);
      label.setText(confirmCode.getHint() + " cannot be empty");
    }
    SplashScreen.userPool.getUser(user_email).confirmSignUpInBackground(confCode, true, confHandler);
  }


  private void reqConfirmCode() {
    SplashScreen.userPool.getUser(user_email)
        .resendConfirmationCodeInBackground(resendConfCodeHandler);
  }


  private void launchUser() {
    PreferenceManager.getDefaultSharedPreferences(SignUpConfirmActivity.this).edit()
        .putBoolean("signed", true).apply();
    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firstTime", true)
        .apply();
    Intent userActivity = new Intent(this, HomeActivity.class);
    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user_name", user_name)
        .apply();
    startActivity(userActivity);
    this.finish();
    finish();
  }


  @Override
  public void onBackPressed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpConfirmActivity.this);
    builder.setTitle("Your account isn't verified yet");
    builder.setMessage("Are you sure?");

    builder.setNegativeButton("Yes", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        // Do nothing
        dialogInterface.dismiss();
        userPool.getUser(user_email).deleteUser(new GenericHandler() {
          @Override
          public void onSuccess() {
            SignUpConfirmActivity.super.onBackPressed();
          }

          @Override
          public void onFailure(Exception exception) {
            SignUpConfirmActivity.super.onBackPressed();
          }
        });
      }
    });

    builder.setPositiveButton("No", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
      }
    });

    AlertDialog alert = builder.create();
    alert.show();
  }
}

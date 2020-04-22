/*
 * *
package com.rishabh.readersjunction.Activities;
 *  * Created by Rishabh Shrivastava on 7/26/19 10:30 AM
 *  * Last modified 7/26/19 9:58 AM
 *  * This activity is for uploading books to db, on upload user gets some credits
 */
package com.rishabh.readersjunction.Activities;

import static com.rishabh.readersjunction.Activities.SplashScreen.api;
import static com.rishabh.readersjunction.Activities.LoginActivity.USER_NAME;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.rishabh.readersjunction.R;
import com.rishabh.readersjunction.Services.InternetService;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadBookActivity extends AppCompatActivity {

  private static final int PICK_IMAGE = 200;
  EditText etBookName, etBookAuthor, etBookDesc;
  AutoCompleteTextView etBookGenre;
  FrameLayout frameLayout;
  ImageView bookCover;
  LinearLayout addImageLayout;
  String base64String = null;
  private String user_name;
  //predefined genres for books
  private String[] genres = {"Biography", "Detective", "Dystopian", "Fantasy", "Horror",
      "Literature", "Romance", "Science Fiction", "Thriller"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_book);
    user_name = PreferenceManager
        .getDefaultSharedPreferences(this).getString(USER_NAME, "null");
    frameLayout = findViewById(R.id.frame);
    bookCover = findViewById(R.id.imageView_upload);
    addImageLayout = findViewById(R.id.add_image_layout);
    frameLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        permission();
      }
    });
    etBookName = findViewById(R.id.input_name);
    etBookAuthor = findViewById(R.id.input_author);
    etBookDesc = findViewById(R.id.book_desc);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this, android.R.layout.select_dialog_item, genres);
    etBookGenre = findViewById(R.id.autocomplete_genre);
    etBookGenre.setAdapter(adapter);
    etBookGenre.setThreshold(1);
    Button upload_button = findViewById(R.id.upload_button);
    upload_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isFormOk()) {
          String book_name = etBookName.getText().toString();
          String book_author = etBookAuthor.getText().toString();
          String book_desc = etBookDesc.getText().toString();
          String book_genre = etBookGenre.getText().toString();
          initiateUpload(book_name, book_author, book_desc, book_genre);
        }
      }
    });
  }


  //checking if we have permission to pick images from photo library
  private void permission() {
    if (ContextCompat.checkSelfPermission(this,
        permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{permission.READ_EXTERNAL_STORAGE},
          1);
    } else {
      selectImage();
    }
  }


  private void selectImage() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
//    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
      if (data.getData() != null) {
        try {
          InputStream inputStream = getContentResolver().openInputStream(data.getData());
          Bitmap bmp = BitmapFactory.decodeStream(inputStream);
          Glide.with(this).load(bmp).into(bookCover);
          addImageLayout.setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }

      }
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
      @NotNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        selectImage();
      } else {
        Toast.makeText(this, "permission Denied", Toast.LENGTH_SHORT).show();
      }
    }
  }


  private void initiateUpload(String book_name, String book_author, String book_desc,
      String book_genre) {
    //uploading a static url for image
    base64String = "https://s3.amazonaws.com/crowdspring3-assets/marketing/landing-page/crowdspring-book-cover-design-lifetime-RedOne-1120.jpg";
    Call<String> call = api
        .uploadBook(book_name, book_author, book_desc, book_genre, base64String, user_name);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          Toast.makeText(UploadBookActivity.this, "Done", Toast.LENGTH_SHORT).show();
          UploadBookActivity.this.finish();
        } else {
          showError("Failed to upload");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        if (!new InternetService(UploadBookActivity.this).haveNetworkConnection()) {
          showError("Not Connected to Internet");
        } else {
          showError("Something went wrong, Please Try again later.");
          Log.d("Error", t.toString());
        }
      }
    });


  }


  private boolean isFormOk() {
    String book_name = etBookName.getText().toString();
    String book_author = etBookAuthor.getText().toString();
    String book_desc = etBookDesc.getText().toString();
    String book_genre = etBookGenre.getText().toString();
    if (book_name.isEmpty()) {
      showError("Empty Book Name");
      return false;
    } else if (book_author.isEmpty()) {
      showError("Empty Book Author");
      return false;
    } else if (book_desc.isEmpty()) {
      showError("Empty Book Author");
      return false;
    } else if (book_genre.isEmpty()) {
      showError("Empty Book Genre");
      return false;
    } else if (base64String == null || base64String.isEmpty()) {
      showError("Empty Image");
    }
    return true;
  }


  private void showError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }


  public void goBack(View view) {
    super.onBackPressed();
  }


}

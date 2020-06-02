/*
 * *
 *  * Created by Rishabh Shrivastava on 26/4/20 11:41 AM
 *  * Last modified 26/4/20 11:41 AM
 *
 */

package com.rishabh.readersjunction.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rishabh.readersjunction.R;

public class CustomProgressDialog extends DialogFragment {


  public CustomProgressDialog(){
    setProgressChangeListener(new ProgressChangeListener() {
      @Override
      public void progressChanged(int progress, String message) {

      }
    });
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return super.onCreateDialog(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.intermediate_dialog_layout, container, false);
  }


  private TextView messageText;
  private ProgressBar progressBar;


  public interface ProgressChangeListener{
    void progressChanged(int progress, String message);
  }


  private ProgressChangeListener progressChangeListener;

  public void setProgressChangeListener(
      ProgressChangeListener progressChangeListener) {
    this.progressChangeListener = progressChangeListener;
  }

  public ProgressChangeListener getProgressChangeListener() {
    return progressChangeListener;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    messageText = view.findViewById(R.id.message);
    progressBar = view.findViewById(R.id.progressBar);
    setProgressChangeListener(new ProgressChangeListener() {
      @Override
      public void progressChanged(int progress, String message) {
        progressBar.setProgress(progress);
        messageText.setText(message);
      }
    });
  }
}

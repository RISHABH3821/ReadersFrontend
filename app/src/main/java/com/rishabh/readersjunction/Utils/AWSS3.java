/*
 * *
 *  * Created by Rishabh Shrivastava on 22/4/20 5:14 PM
 *  * Last modified 22/4/20 5:14 PM
 *
 */

package com.rishabh.readersjunction.Utils;

import android.content.Context;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;

import java.util.concurrent.CountDownLatch;


public class AWSS3 {
  private AmazonS3Client sS3Client;
  private AWSCredentialsProvider sMobileClient;
  private TransferUtility sTransferUtility;

  private AWSCredentialsProvider getCredProvider(Context context) {
    if (sMobileClient == null) {
      final CountDownLatch latch = new CountDownLatch(1);
      AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
        @Override
        public void onResult(UserStateDetails result) {
          latch.countDown();
        }

        @Override
        public void onError(Exception e) {
//                    Log.e(TAG, "onError: ", e);
          latch.countDown();
        }
      });
      try {
        latch.await();
        sMobileClient = AWSMobileClient.getInstance();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return sMobileClient;
  }

  public AmazonS3Client getS3Client(Context context) {
    if (sS3Client == null) {
      sS3Client = new AmazonS3Client(getCredProvider(context));
      try {
        String regionString = new AWSConfiguration(context)
            .optJsonObject("S3TransferUtility")
            .getString("Region");
        sS3Client.setRegion(Region.getRegion(regionString));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return sS3Client;
  }

  public TransferUtility getTransferUtility(Context context) {
    if (sTransferUtility == null) {
      sTransferUtility = TransferUtility.builder()
          .context(context)
          .s3Client(getS3Client(context))
          .awsConfiguration(new AWSConfiguration(context))
          .build();
    }

    return sTransferUtility;
  }
}
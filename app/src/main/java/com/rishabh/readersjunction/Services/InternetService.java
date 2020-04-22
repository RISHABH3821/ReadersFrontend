/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 11:22 AM
 *
 */

package com.rishabh.readersjunction.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetService {
  private Context context;

  public InternetService(Context context) {
    this.context = context;
  }


  public boolean haveNetworkConnection() {
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;

    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null) {
      // connected to the internet
      switch (netInfo.getType()) {
        case ConnectivityManager.TYPE_WIFI:
          haveConnectedWifi = true;
          break;
        case ConnectivityManager.TYPE_MOBILE:
          haveConnectedMobile = true;
          break;
        default:
          break;
      }
    }
    return haveConnectedWifi || haveConnectedMobile;
  }


}

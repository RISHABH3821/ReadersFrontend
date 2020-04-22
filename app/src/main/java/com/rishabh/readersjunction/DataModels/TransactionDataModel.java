/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 6:08 AM
 *
 */

package com.rishabh.readersjunction.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDataModel {

  @SerializedName("trans_id")
  @Expose
  private Integer transId;
  @SerializedName("book_id")
  @Expose
  private Integer bookId;
  @SerializedName("user_name_seller")
  @Expose
  private String userNameSeller;
  @SerializedName("user_name_buyer")
  @Expose
  private String userNameBuyer;
  @SerializedName("credits")
  @Expose
  private Integer credits;
  @SerializedName("status_transaction")
  @Expose
  private String statusTransaction;

  public Integer getTransId() {
    return transId;
  }

  public void setTransId(Integer transId) {
    this.transId = transId;
  }

  public Integer getBookId() {
    return bookId;
  }

  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public String getUserNameSeller() {
    return userNameSeller;
  }

  public void setUserNameSeller(String userNameSeller) {
    this.userNameSeller = userNameSeller;
  }

  public String getUserNameBuyer() {
    return userNameBuyer;
  }

  public void setUserNameBuyer(String userNameBuyer) {
    this.userNameBuyer = userNameBuyer;
  }

  public Integer getCredits() {
    return credits;
  }

  public void setCredits(Integer credits) {
    this.credits = credits;
  }

  public String getStatusTransaction() {
    return statusTransaction;
  }

  public void setStatusTransaction(String statusTransaction) {
    this.statusTransaction = statusTransaction;
  }

}
/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 9:57 PM
 *
 */

package com.rishabh.readersjunction.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookDataModel {

  @SerializedName("credits")
  @Expose
  private Integer credits;
  @SerializedName("book_id")
  @Expose
  private Integer bookId;
  @SerializedName("book_name")
  @Expose
  private String bookName;
  @SerializedName("book_author")
  @Expose
  private String bookAuthor;
  @SerializedName("book_description")
  @Expose
  private String bookDescription;
  @SerializedName("book_status")
  @Expose
  private String bookStatus;
  @SerializedName("book_exchange_count")
  @Expose
  private Integer bookExchangeCount;
  @SerializedName("user_name")
  @Expose
  private String userName;
  @SerializedName("book_genre")
  @Expose
  private String bookGenre;
  @SerializedName("book_cover")
  @Expose
  private String bookCover;

  public Integer getCredits() {
    return credits;
  }

  public void setCredits(Integer credits) {
    this.credits = credits;
  }

  public Integer getBookId() {
    return bookId;
  }

  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public String getBookName() {
    return bookName;
  }

  public void setBookName(String bookName) {
    this.bookName = bookName;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public void setBookAuthor(String bookAuthor) {
    this.bookAuthor = bookAuthor;
  }

  public String getBookDescription() {
    return bookDescription;
  }

  public void setBookDescription(String bookDescription) {
    this.bookDescription = bookDescription;
  }

  public String getBookStatus() {
    return bookStatus;
  }

  public void setBookStatus(String bookStatus) {
    this.bookStatus = bookStatus;
  }

  public Integer getBookExchangeCount() {
    return bookExchangeCount;
  }

  public void setBookExchangeCount(Integer bookExchangeCount) {
    this.bookExchangeCount = bookExchangeCount;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getBookGenre() {
    return bookGenre;
  }

  public void setBookGenre(String bookGenre) {
    this.bookGenre = bookGenre;
  }

  public String getBookCover() {
    return bookCover;
  }

  public void setBookCover(String bookCover) {
    this.bookCover = bookCover;
  }

}
/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:32 AM
 *  * Last modified 7/25/19 6:08 AM
 *
 */

package com.rishabh.readersjunction.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenreDataModel {

  @SerializedName("book_genre")
  @Expose
  private String bookGenre;
  @SerializedName("book_cover")
  @Expose
  private String bookCover;

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
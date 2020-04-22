/*
 * *
 *  * Created by Rishabh Shrivastava on 7/26/19 10:30 AM
 *  * Last modified 7/26/19 10:23 AM
 *
 */

package com.rishabh.readersjunction.Utils;

import com.rishabh.readersjunction.DataModels.BookDataModel;
import com.rishabh.readersjunction.DataModels.GenreDataModel;
import com.rishabh.readersjunction.DataModels.TransactionDataModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

  @FormUrlEncoded
  @POST("/login/")
  Call<String> performLogin(@Field("user_name") String user_name,
      @Field("password") String password);

  @FormUrlEncoded
  @POST("/signup/")
  Call<String> performRegisteration(@Field("user_name") String user_name,
      @Field("full_name") String fullname,
      @Field("password") String password,
      @Field("email") String email,
      @Field("contact_number") String contact_number,
      @Field("address") String address);

  @FormUrlEncoded
  @POST("/uploadBook/")
  Call<String> uploadBook(@Field("book_name") String book_name,
      @Field("book_author") String book_author,
      @Field("book_desc") String book_desc,
      @Field("book_genre") String book_genre,
      @Field("book_cover") String book_cover,
      @Field("user_name") String user_name);

  @GET("/GetBookByGenre/")
  Call<List<BookDataModel>> getBooksByGenre(@Query("genre") String genre);

  @GET("/GetBookByUserName/")
  Call<List<BookDataModel>> getBooksByUser(@Query("user_name") String user_name);

  @GET("/GetUserFullName/")
  Call<String> getFullName(@Query("user_name") String user_name);

  @GET("/creditCount/")
  Call<String> getCurrentCredit(@Query("user_name") String user_name);

  @FormUrlEncoded
  @POST("/DeleteMyBook/")
  Call<String> deleteBook(@Field("book_id") int book_id);

  //get transaction from transaction table if user_name is either buyer or seller.
  @GET("/GetAllTransactions/")
  Call<List<TransactionDataModel>> getAllTransactions(@Query("user_name") String user_name);

  //requested
  @FormUrlEncoded
  @POST("/AcceptRequest/")
  Call<String> acceptRequest(@Field("transaction_id") int transaction_id);

  @FormUrlEncoded
  @POST("/RejectRequest/")
  Call<String> rejectRequest(@Field("transaction_id") int transaction_id);

  @FormUrlEncoded
  @POST("/SendRequest/")
  Call<String> sendRequest(@Field("book_id") String book_id,
      @Field("buyer_name") String buyer_name);

  //buyer can call on any stage.
  @FormUrlEncoded
  @POST("/CancelRequest/")
  Call<String> cancelRequest(@Field("transaction_id") int transaction_id);

  //pending or
  @FormUrlEncoded
  @POST("/DiscardTransaction/")
  Call<String> discardTransaction(@Field("transaction_id") int transaction_id);

  @FormUrlEncoded
  @POST("/FinalizeTransaction/")
  Call<String> completeTransaction(@Field("transaction_id") int transaction_id);

  @GET("/GetBookNameByID/")
  Call<String> getBookNameByID(@Query("book_id") int book_id);

  @GET("/getGenre/")
  Call<List<GenreDataModel>> getGenres();

  @GET("/getRating/")
  Call<String> getRating(@Query("book_id") int book_id, @Query("user_id") int user_id);

  @GET("/getTotalRating/")
  Call<String> getTotalRating(@Query("book_id") int book_id);

  @FormUrlEncoded
  @POST("/postRating/")
  Call<String> postRating(@Field("book_id") int book_id, @Field("user_id") int user_id,
      @Field("rating") Float rating);


  @GET("/getUserId/")
  Call<String > getUserId(@Query("user_name") String user_name);

}

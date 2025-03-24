package com.example.greenlens.api;

import com.example.greenlens.model.User;
import com.example.greenlens.model.request.LoginRequest;
import com.example.greenlens.model.request.SignupRequest;
import com.example.greenlens.model.response.LoginResponse;
import com.example.greenlens.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("users/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @GET("users/profile")
    Call<User> getUserProfile(@Header("Authorization") String token);

    @PUT("users/profile")
    Call<User> updateUserProfile(@Header("Authorization") String token, @Body User user);

    @DELETE("users/{userId}")
    Call<Void> deleteAccount(@Header("Authorization") String token, @Path("userId") int userId);

    @POST("users/logout")
    Call<Void> logout(@Header("Authorization") String token);
}
package com.example.greenlens.api;

import com.example.greenlens.model.User;
import com.example.greenlens.model.request.LoginRequest;
import com.example.greenlens.model.request.SignupRequest;
import com.example.greenlens.model.response.LoginResponse;
import com.example.greenlens.model.response.SignupResponse;
import com.example.greenlens.model.response.AnalyzeResponse;
import com.example.greenlens.model.response.AnalysisResultResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

import java.util.Map;
import okhttp3.MultipartBody;

public interface ApiService {
    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("users/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @GET("users/profile")
    Call<User> getUserProfile(@Header("Authorization") String token);

    @PUT("users/{userId}")
    Call<User> updateUserProfile(@Header("Authorization") String token, @Path("userId") Long userId, @Body User user);

    @DELETE("users/{userId}")
    Call<Void> deleteAccount(@Header("Authorization") String token, @Path("userId") Long userId);

    @POST("users/logout")
    Call<Void> logout(@Header("Authorization") String token);

    // 이미지 분석 요청 API
    @Multipart
    @POST("recycle/analyze")
    Call<AnalyzeResponse> analyzeImage(
            @Header("Authorization") String token,
            @Part MultipartBody.Part image);

    // 분석 결과 조회 API
    @GET("recycle/result/{analysis_id}")
    Call<AnalysisResultResponse> getAnalysisResult(
            @Header("Authorization") String token,
            @Path("analysis_id") Long analysisId);
}
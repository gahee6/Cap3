package com.example.register;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @POST("create/")
    Call<MemberDTO> createMember(@Body MemberDTO memberDTO);

    @GET("check/student/")
    Call<Boolean> checkStudentNum(@Query("studentNum") String studentNum);
}

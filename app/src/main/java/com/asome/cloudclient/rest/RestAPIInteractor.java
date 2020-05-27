package com.asome.cloudclient.rest;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPIInteractor {

    @POST("api/rest")
    Call<ResponseBody> coordinates(@Body ArrayList<Coordinate> req);
}

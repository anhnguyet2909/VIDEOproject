package com.example.video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetHotVideoAPI {
    @GET("getHotVideos")
    Call<List<HotVideos>> getHotVideo();

    @GET("getCategoryOne")
    Call<List<HotVideos>> getCategories1();

    @GET("getCategoryTwo")
    Call<List<HotVideos>> getCategories2();

    @GET("getRelatedVideos")
    Call<List<HotVideos>> getRelatedVideo();
}

package com.example.video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetCategoriesAPI {
    @GET("getCategories")
    Call<List<Categories>> getCategories();
}

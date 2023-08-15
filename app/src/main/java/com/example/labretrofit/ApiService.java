package com.example.labretrofit;

import com.example.labretrofit.model.ListComics;
import com.example.labretrofit.model.ResComic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiService.class);

    @GET("comic")
    Call<ListComics> getComics();
    @Multipart
    @POST("comic/add")
    Call<ResComic> addComic(@Part("name")RequestBody name,
                            @Part("nametg")RequestBody nametg,
                            @Part("des")RequestBody des,
                            @Part("yearxb")RequestBody yearxb,
                            @Part MultipartBody.Part img,
                            @Part List<MultipartBody.Part> imgnd
                            );

    @DELETE("comic/delete/{idcomic}")
    Call<ResComic> deleteComic(@Path("idcomic") String idcomic);

    @Multipart
    @PUT("comic/edit/{idcomic}")
    Call<ResComic> updateComic(@Path("idcomic") String idcomic,
                                @Part("name")RequestBody name,
                                @Part("nametg")RequestBody nametg,
                                @Part("des")RequestBody des,
                                @Part("yearxb")RequestBody yearxb,
                                @Part MultipartBody.Part img,
                                @Part List<MultipartBody.Part> imgnd
    );


    /////hung commit




    /////kkkkkk
    ///// nhanh master
}

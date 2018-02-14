package com.mouaincorporate.matt.MapConnect.MapsUtilities;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
/*   @GET("api/gateways")
    public Call<DataResponse> getAllData();
    @GET("api/gateways")
    public Call<DataResponse> getListData(@Query("api_key") String api_key);*/

    @GET("/maps/api/distancematrix/json")
    public Call<Data> getDirection(@Query("origin") String origin, @Query("destination") String destination);


    @GET("/maps/api/directions/json")
    public Call<DirectionResultsModel> getDistanceAndDuration(@Query("origin") String origin, @Query("destination") String destination, @Query("sensor") String sensor, @Query("mode") String mode, @Query("alternatives") String alternatives);

}
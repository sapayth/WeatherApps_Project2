package org.bitm.nullpointers.weatherapps.Utils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherForecastApi {

    @GET
    Call<ForecastWeather> getWeatherData(@Url String urlString);
}

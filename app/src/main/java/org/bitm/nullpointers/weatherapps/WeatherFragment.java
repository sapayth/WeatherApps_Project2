package org.bitm.nullpointers.weatherapps;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bitm.nullpointers.weatherapps.Utils.CurrentWeather;
import org.bitm.nullpointers.weatherapps.Utils.WeatherCurrentApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    private Retrofit retrofit;
    private WeatherCurrentApi weatherCurrentApi;
    private String urlString;

    private String units = "metric"; //imperial
    private static final String DAY_BASE_URL = "https://api.openweathermap.org/data/2.5/";

    TextView temperatureTV;
    TextView dateTV;
    TextView dayTV;
    TextView locationTV;
    TextView weatherDescTV;
    TextView maxTV;
    TextView minTV;
    TextView sunriseTV;
    TextView sunsetTV;
    TextView humidityTV;
    TextView pressureTV;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        temperatureTV = view.findViewById(R.id.temperatureTextView);
        dateTV = view.findViewById(R.id.dateTextView);
        dayTV = view.findViewById(R.id.dayTextView);
        locationTV = view.findViewById(R.id.locationTextView);
        weatherDescTV = view.findViewById(R.id.weatherDescTextView);
        maxTV = view.findViewById(R.id.maxTempTextView);
        minTV = view.findViewById(R.id.minTempTextView);
        sunriseTV = view.findViewById(R.id.sunriseTextView);
        sunsetTV = view.findViewById(R.id.sunsetTextView);
        humidityTV = view.findViewById(R.id.humidityTextView);
        pressureTV = view.findViewById(R.id.pressureTextView);

        retrofit = new Retrofit.Builder()
                .baseUrl(DAY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherCurrentApi = retrofit.create(WeatherCurrentApi.class);

        urlString = String.format("weather?lat=%f&lon=%f&cnt=7&units=%s&appid=%s",
                34.966671,
                138.933334,
                units,
                getString(R.string.weather_api_key));

        Call<CurrentWeather> responseCall = weatherCurrentApi.getCurrentWeatherData(urlString);

        responseCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather currentWeather = response.body();
                    CurrentWeather.Main main = currentWeather.getMain();
                    CurrentWeather.Sys sys = currentWeather.getSys();

//                    List<CurrentWeather.Weather> weatherList = currentWeather.getWeatherList();
//                    CurrentWeather.Weather weather = weatherList.get(0);
//                    weatherDescTV.setText(weather.getDescription() + "");

                    double temperature = main.getTemp();
                    temperatureTV.setText(temperature + " \u00B0C");
                    dateTV.setText(unixToDate(currentWeather.getDt()));
                    dayTV.setText(unixToDay(currentWeather.getDt()));
                    maxTV.setText(main.getTempMax() + " \u00B0C");
                    minTV.setText(main.getTempMin() + " \u00B0C");
                    sunriseTV.setText(getTimeFromUnix(sys.getSunrise()));
                    sunsetTV.setText(getTimeFromUnix(sys.getSunset()));
                    humidityTV.setText(main.getHumidity() + "%");
                    pressureTV.setText(main.getPressure() + "hPa");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String unixToDay(long timeStamp) {
        java.util.Date dateTime = new java.util.Date((long)timeStamp*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int dayInt = cal.get(Calendar.DAY_OF_WEEK);

        String dayStr = "";

        switch (dayInt) {
            case 0:
                dayStr = "Saturday";
                break;
            case 1:
                dayStr = "Sunday";
                break;
            case 2:
                dayStr = "Monday";
                break;
            case 3:
                dayStr = "Tuesday";
                break;
            case 4:
                dayStr = "Wednesday";
                break;
            case 5:
                dayStr = "Thursday";
                break;
            case 6:
                dayStr = "Friday";
                break;
        }

        return dayStr;
    }

    private String unixToDate(long timestamp) {
        // convert seconds to milliseconds
        Date date = new java.util.Date(timestamp*1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    private String getTimeFromUnix(long timestamp) {
        java.util.Date dateTime = new java.util.Date(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);

        int hour = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        String formattedTime = "";

        if (hour >= 12) {
            hour -= 12;
            formattedTime = hour + ":" + minutes + ":" + seconds + "AM";
        } else {
            formattedTime = hour + ":" + minutes + ":" + seconds + "AM";
        }

        return formattedTime;
    }
}

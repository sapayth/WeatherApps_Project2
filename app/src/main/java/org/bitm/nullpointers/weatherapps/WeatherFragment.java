package org.bitm.nullpointers.weatherapps;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

    TextView failedTV;

    ImageView weatherIconIV;

    String imageIdStr;

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
        weatherIconIV = view.findViewById(R.id.weatherIconImageView);

        failedTV = view.findViewById(R.id.failedTextView);

        retrofit = new Retrofit.Builder()
                .baseUrl(DAY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherCurrentApi = retrofit.create(WeatherCurrentApi.class);

        urlString = String.format("weather?lat=%f&lon=%f&cnt=7&units=%s&appid=%s",
                23.777176,
                90.399452,
                units,
                getString(R.string.weather_api_key));

        Call<CurrentWeather> responseCall = weatherCurrentApi.getCurrentWeatherData(urlString);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // show it
        progressDialog.show();

        responseCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    CurrentWeather currentWeather = response.body();
                    CurrentWeather.Main main = currentWeather.getMain();
                    CurrentWeather.Sys sys = currentWeather.getSys();

                    List<CurrentWeather.Weather> weatherList = currentWeather.getWeatherList();
                    CurrentWeather.Weather weather = weatherList.get(0);
                    weatherDescTV.setText(weather.getDescription() + "");

                    double temperature = main.getTemp();
                    temperatureTV.setText(temperature + " \u00B0C");
                    dateTV.setText(unixToDate(currentWeather.getDt()));
                    dayTV.setText(unixToDay(currentWeather.getDt()));
                    maxTV.setText(main.getTempMax() + " \u00B0C");
                    minTV.setText(main.getTempMin() + " \u00B0C");

                    imageIdStr = weatherList.get(0).getIcon();
                    long sunrise = sys.getSunrise();
                    long sunset = sys.getSunset();

                    sunriseTV.setText(getTimeFromUnix(sunrise));
                    sunsetTV.setText(getTimeFromUnix(sunset));
                    humidityTV.setText(main.getHumidity() + "%");
                    pressureTV.setText(main.getPressure() + "hPa");

                    Picasso.get().load("https://openweathermap.org/img/w/" + imageIdStr + ".png").into(weatherIconIV);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                progressDialog.dismiss();
                failedTV.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String unixToDay(long timeStamp) {
        java.util.Date dateTime = new java.util.Date((long)timeStamp*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int dayInt = cal.get(Calendar.DAY_OF_WEEK);

        String dayStr = "Saturday";

        switch (dayInt) {
            case Calendar.SATURDAY:
                dayStr = "Saturday";
                break;
            case Calendar.SUNDAY:
                dayStr = "Sunday";
                break;
            case Calendar.MONDAY:
                dayStr = "Monday";
                break;
            case Calendar.TUESDAY:
                dayStr = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayStr = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayStr = "Thursday";
                break;
            case Calendar.FRIDAY:
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000L);

        Date d = calendar.getTime();
        String timeStr = new SimpleDateFormat("hh:mm a").format(d);

        return timeStr;
    }
}
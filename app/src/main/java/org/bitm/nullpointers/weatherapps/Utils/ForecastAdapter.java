package org.bitm.nullpointers.weatherapps.Utils;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitm.nullpointers.weatherapps.R;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastWeather.WeatherList> forecastWeatherList;
    private Context context;

    public ForecastAdapter(List<ForecastWeather.WeatherList> forecastWeatherList, Context context) {
        this.forecastWeatherList = forecastWeatherList;
        this.context = context;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_item, parent, false);

        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastWeather.WeatherList currentForecastWeather = forecastWeatherList.get(position);
        ForecastWeather.Temp temperature = currentForecastWeather.getTemp();

        holder.dayTV.setText(currentForecastWeather.getDt() + "");
        holder.maxTV.setText(temperature.getMax() + "\u00B0 C");

        holder.dateTV.setText(currentForecastWeather.getDt() + "");
        holder.minTV.setText(temperature.getMin() + "\u00B0 C");

    }

    @Override
    public int getItemCount() {
        return forecastWeatherList.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView dayTV;
        TextView minTV;
        ImageView weatherIV;
        TextView dateTV;
        TextView maxTV;

        public ForecastViewHolder(View itemView) {
            super(itemView);

            dayTV = itemView.findViewById(R.id.dayTextView);
            minTV = itemView.findViewById(R.id.minTempTextView);
            weatherIV = itemView.findViewById(R.id.weatherIconImageView);
            dateTV = itemView.findViewById(R.id.dateTextView);
            maxTV = itemView.findViewById(R.id.maxTempTextView);
        }
    }
}

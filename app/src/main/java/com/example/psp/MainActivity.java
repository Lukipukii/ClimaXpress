package com.example.psp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText etCiudad;
    private TextView tvClima;
    //Esto no se debe hacer en produccion, es muy poco seguro insertar la clave API tal cual en el código
    private final String API_KEY = "insert your API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCiudad = findViewById(R.id.etCiudad);
        tvClima = findViewById(R.id.tvDatos);
    }

    //función para realizar la llamada a la API
    private void fetchWeather(String city) {
        //declaración clase Retrofit para construir la llamada HTTP
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()) //Empleo de la libreria Gson para la conversion de los datos recibidos
                .build();

        //declaracion de la interfaz WeatherService, donde se pasan los parametros para la llamada
        WeatherService service = retrofit.create(WeatherService.class);

        //declaracion de la llamada con los parametros especificados en la interfaz
        Call<WeatherResponse> respuesta = service.getWeather(city, API_KEY, "metric", "es");

        //ejecucion de la llamada de forma asincrona
        respuesta.enqueue(new Callback<WeatherResponse>() {
            @Override
            //si hay respuesta por parte de la API, se procesa
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                //si se reciben datos se procesan para mostrarselos al usuario
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse respuesta = response.body();
                    String datosTiempo = "🌡 Temperatura: " + respuesta.main.temp + "°C\n" +
                            "🌤 Estado: " + respuesta.weather[0].description;
                    tvClima.setText(datosTiempo);
                //si hay respuesta pero no hay datos, se procesa el codigo de error;
                } else {
                    Log.e("API_ERROR", "Código de respuesta: " + response.code());
                    Log.e("API_ERROR", "Mensaje: " + response.message());
                    tvClima.setText("Ciudad no encontrada.");
                }
            }

            //si no hay respuesta, se ejecuta esta parte
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                tvClima.setText("Error al obtener datos.");
            }
        });
    }


    //metodo onClick para ejecutar la llamada a la API
    public void clickConsulta(View v) {
        String city = etCiudad.getText().toString();
        if (!city.isEmpty()) {
            fetchWeather(city);
        } else {
            tvClima.setText("Por favor ingresa una ciudad.");
        }
    }
}

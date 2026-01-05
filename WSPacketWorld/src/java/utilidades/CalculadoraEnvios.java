package utilidades;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalculadoraEnvios {

    private static final String URL_API = "http://sublimas.com.mx:8080/calculadora/api/envios/distancia/";

    public static Double obtenerDistancia(String cpOrigen, String cpDestino) {
        Double distancia = null;
        try {
            String urlCadena = URL_API + cpOrigen + "," + cpDestino;
            
            URL url = new URL(urlCadena);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output = br.readLine();
                conn.disconnect();

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(output, JsonObject.class);
                
                if (!json.get("error").getAsBoolean()) {
                    distancia = json.get("distanciaKM").getAsDouble();
                    System.out.println("--- API EXTERNA: Distancia calculada: " + distancia + " km ---");
                } else {
                    System.err.println("--- API EXTERNA ERROR: " + json.get("mensaje").getAsString());
                }
            } else {
                System.err.println("--- API EXTERNA ERROR HTTP: " + conn.getResponseCode());
            }

        } catch (Exception ex) {
            System.err.println("Error conectando a API de distancia: " + ex.getMessage());
             distancia = 50.0; 
        }
        return distancia;
    }

    public static float calcularCosto(Double distanciaKm, int numPaquetes) {
        if (distanciaKm == null || distanciaKm < 0) return 0;

        float costoBase = 0;
        if (distanciaKm <= 200) {
            costoBase = (float) (distanciaKm * 4.00);
        } else if (distanciaKm <= 500) {
            costoBase = (float) (distanciaKm * 3.00);
        } else if (distanciaKm <= 1000) {
            costoBase = (float) (distanciaKm * 2.00);
        } else if (distanciaKm <= 2000) {
            costoBase = (float) (distanciaKm * 1.00);
        } else {
            costoBase = (float) (distanciaKm * 0.50);
        }

        float costoExtra = 0;
        if (numPaquetes < 2) {
            costoExtra = 0;
        } else if (numPaquetes == 2) {
            costoExtra = 50;
        } else if (numPaquetes == 3) {
            costoExtra = 80;
        } else if (numPaquetes == 4) {
            costoExtra = 110;
        } else {
            costoExtra = 150;
        }

        return costoBase + costoExtra;
    }
}
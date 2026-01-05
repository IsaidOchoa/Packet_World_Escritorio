package utilidades;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import pojo.RespuestaDistancia;

public class CalculadoraEnvios {

    public static Double obtenerDistancia(String cpOrigen, String cpDestino) {
        Double distancia = null;
        
       

        try {
            if (cpOrigen == null || cpDestino == null) {
           System.out.println(">> Error: CP Origen o Destino es NULO");
           return null; 
       }

       if (cpOrigen.length() < 5) {
           cpOrigen = String.format("%05d", Integer.parseInt(cpOrigen));
       }
       if (cpDestino.length() < 5) {
           cpDestino = String.format("%05d", Integer.parseInt(cpDestino));
       }

            // 3. Consumo de API
            String urlApi = "http://sublimas.com.mx:8080/calculadora/api/envios/distancia/" + cpOrigen + "," + cpDestino;
            URL url = new URL(urlApi);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder jsonStr = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    jsonStr.append(output);
                }
                conn.disconnect();

                Gson gson = new Gson();
                RespuestaDistancia respuesta = gson.fromJson(jsonStr.toString(), RespuestaDistancia.class);

                if (!respuesta.isError()) {
                    distancia = respuesta.getDistanciaKM();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // No retornamos 150 aquí, dejamos que sea null para manejarlo arriba
        }
        return distancia;
    }

    public static float calcularCosto(Double distancia, int numPaquetes) {
        if (distancia == null || distancia < 0) return 0;

        // COSTO POR KILÓMETRO
        float costoPorKm;
        if (distancia <= 200) {
            costoPorKm = 4.00f;
        } else if (distancia <= 500) {
            costoPorKm = 3.00f;
        } else if (distancia <= 1000) {
            costoPorKm = 2.00f;
        } else if (distancia <= 2000) {
            costoPorKm = 1.00f;
        } else {
            costoPorKm = 0.50f;
        }

        float subtotalDistancia = (float) (distancia * costoPorKm);

        // COSTO EXTRA POR PAQUETES 
        float costoExtra = 0.00f;
        if (numPaquetes <= 1) {
            costoExtra = 0.00f;
        } else if (numPaquetes == 2) {
            costoExtra = 50.00f;
        } else if (numPaquetes == 3) {
            costoExtra = 80.00f;
        } else if (numPaquetes == 4) {
            costoExtra = 110.00f;
        } else {
            costoExtra = 150.00f;
        }

        return subtotalDistancia + costoExtra;
    }
}
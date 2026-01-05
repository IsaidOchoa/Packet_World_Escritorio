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

        // 1. VALIDACIÓN DE SEGURIDAD: Evitar NullPointerException
        if (cpOrigen == null || cpDestino == null) {
            System.out.println(">> [Calculadora] Error: CP Origen o Destino es NULO. No se puede calcular.");
            return null;
        }

        // 2. LIMPIEZA DE DATOS: Quitar espacios en blanco al inicio o final
        cpOrigen = cpOrigen.trim();
        cpDestino = cpDestino.trim();

        // Si están vacíos después del trim, tampoco continuamos
        if (cpOrigen.isEmpty() || cpDestino.isEmpty()) {
            System.out.println(">> [Calculadora] Error: CP Origen o Destino están vacíos.");
            return null;
        }

        try {
            // 3. FORMATEO DE CÓDIGOS POSTALES (Rellenar con ceros a la izquierda si faltan)
            // La API externa requiere 5 dígitos. Ej: "9100" -> "09100"
            try {
                if (cpOrigen.matches("\\d+") && cpOrigen.length() < 5) {
                    cpOrigen = String.format("%05d", Integer.parseInt(cpOrigen));
                }
                if (cpDestino.matches("\\d+") && cpDestino.length() < 5) {
                    cpDestino = String.format("%05d", Integer.parseInt(cpDestino));
                }
            } catch (NumberFormatException nfe) {
                System.out.println(">> [Calculadora] Advertencia: El CP tiene letras, se enviará tal cual: " + nfe.getMessage());
            }

            // 4. CONSTRUCCIÓN DE LA URL Y CONSULTA
            String urlApi = "http://sublimas.com.mx:8080/calculadora/api/envios/distancia/" + cpOrigen + "," + cpDestino;
            System.out.println(">> [Calculadora] Consultando API: " + urlApi);

            URL url = new URL(urlApi);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // 5. LECTURA DE RESPUESTA
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder jsonStr = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    jsonStr.append(output);
                }
                conn.disconnect();

                // Imprimir respuesta cruda para depuración
                System.out.println(">> [Calculadora] Respuesta API: " + jsonStr.toString());

                // 6. PARSEO DEL JSON
                Gson gson = new Gson();
                RespuestaDistancia respuesta = gson.fromJson(jsonStr.toString(), RespuestaDistancia.class);

                if (respuesta != null && !respuesta.isError()) {
                    distancia = respuesta.getDistanciaKM();
                } else {
                    String mensajeError = (respuesta != null) ? respuesta.getMensaje() : "Respuesta nula";
                    System.out.println(">> [Calculadora] La API retornó error lógico: " + mensajeError);
                }
            } else {
                System.out.println(">> [Calculadora] Error HTTP de conexión: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">> [Calculadora] Excepción general: " + e.getMessage());
        }
        
        return distancia;
    }

    public static float calcularCosto(Double distancia, int numPaquetes) {
        // Si la distancia es nula o negativa (error en API), el costo base es 0
        if (distancia == null || distancia < 0) return 0.0f;

        // 1. COSTO POR KILÓMETRO (Según reglas de negocio)
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

        // 2. COSTO EXTRA POR CANTIDAD DE PAQUETES
        float costoExtra = 0.00f;
        // Nota: "numPaquetes <= 1" cubre 0 o 1 paquete (costo 0)
        if (numPaquetes < 2) { 
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
package utilidades;

public class TestRapido {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE CONEXIÓN ---");
        
        // 1. Prueba con datos reales
        String origen = "91000";
        String destino = "01000";
        
        System.out.println("Consultando API con: " + origen + " -> " + destino);
        Double dist = CalculadoraEnvios.obtenerDistancia(origen, destino);
        
        System.out.println("Resultado Distancia: " + dist);
        
        float costo = CalculadoraEnvios.calcularCosto(dist, 1);
        System.out.println("Costo calculado: $" + costo);
    }
}
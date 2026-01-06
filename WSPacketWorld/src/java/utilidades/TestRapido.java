package utilidades;

public class TestRapido {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   TEST RÁPIDO DE SERVICIOS Y CÁLCULOS   ");
        System.out.println("=========================================");

        // -------------------------------------------------
        // CASO 1: PRUEBA NORMAL (CONEXIÓN EXITOSA)
        // -------------------------------------------------
        System.out.println("\n--- [CASO 1] Prueba con CPs Reales (Xalapa 91000 -> Veracruz 91700) ---");
        String origen = "91000";
        String destino = "91700";
        
        System.out.println("Consultando API...");
        Double dist = CalculadoraEnvios.obtenerDistancia(origen, destino);
        System.out.println("Distancia obtenida: " + dist + " km");
        
        if(dist != null) {
            float costo = CalculadoraEnvios.calcularCosto(dist, 1);
            System.out.println("Costo (1 paquete): $" + costo);
        } else {
            System.out.println("FALLÓ LA API EN CASO 1 (Verificar internet)");
        }

        // -------------------------------------------------
        // CASO 2: SIMULACIÓN DE FALLO (SIN INTERNET O CP MALO)
        // -------------------------------------------------
        System.out.println("\n--- [CASO 2] Simulación de Fallo (CPs Inválidos) ---");
        // Usamos CPs que sabemos que no existen o la API no procesará para forzar el NULL
        Double distFallo = CalculadoraEnvios.obtenerDistancia("00000", "99999");
        
        System.out.println("Distancia obtenida (debe ser null): " + distFallo);
        
        // Simular la lógica que pusimos en EnvioImp: Si es null, usamos 0.0
        if(distFallo == null){
            System.out.println(">> Aplicando lógica de Fallback (Forzando distancia = 0.0)");
            distFallo = 0.0;
        }

        // -------------------------------------------------
        // CASO 3: VERIFICAR COBRO DE PAQUETES EXTRAS SIN DISTANCIA
        // -------------------------------------------------
        System.out.println("\n--- [CASO 3] Prueba de Fuego: 8 Paquetes con Distancia 0 ---");
        // Este es el error que tenías: antes daba $0. Ahora debe dar $>0
        int cantidadPaquetes = 8;
        
        float costoFallback = CalculadoraEnvios.calcularCosto(distFallo, cantidadPaquetes);
        
        System.out.println("Input -> Distancia: " + distFallo + " | Paquetes: " + cantidadPaquetes);
        System.out.println("Costo Calculado: $" + costoFallback);
        
        // Validación visual para ti
        if(costoFallback > 0) {
            System.out.println(">>> RESULTADO: ¡EXITOSO! El sistema cobró los paquetes aunque no hubo mapa.");
        } else {
            System.err.println(">>> RESULTADO: FALLIDO. El costo sigue siendo $0. Revisa CalculadoraEnvios.java");
        }
        
        System.out.println("\n=========================================");
    }
}
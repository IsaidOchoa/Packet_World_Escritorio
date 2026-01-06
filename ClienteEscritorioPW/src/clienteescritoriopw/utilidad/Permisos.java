package clienteescritoriopw.utilidad;

import javafx.scene.control.Button;

/**
 *
 * @author HOME
 */
public class Permisos {
    public static void aplicarPermisoAdmin(int idRol, Button... botones) {
        if (idRol != 1) { // no es admin
            for (Button btn : botones) {
                if (btn != null) {
                    btn.setVisible(false);
                    // o: btn.setDisable(true);
                }
            }
        }
    }

    // Para conductor: solo lectura
    public static boolean esConductor(int idRol) {
        return idRol == 3;
    }

    public static boolean esEjecutivo(int idRol) {
        return idRol == 2;
    }
    public static boolean esAdmin(int idRol) {
        return idRol == 1;
    }

    // Ejemplo: ocultar módulos no permitidos en pantalla principal
    public static void ocultarModulosConductor(
            int idRol,
            Button btnColaboradores,
            Button btnSucursales,
            Button btnClientes) {
        if (esConductor(idRol)) {
            if (btnColaboradores != null) btnColaboradores.setVisible(false);
            if (btnSucursales != null) btnSucursales.setVisible(false);
            if (btnClientes != null) btnClientes.setVisible(false);
        }
    }
    
}

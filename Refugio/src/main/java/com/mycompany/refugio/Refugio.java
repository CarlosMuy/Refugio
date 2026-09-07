
package com.mycompany.refugio;

import javax.swing.SwingUtilities;

public class Refugio {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControlSistema sistema = new ControlSistema();
            VentanaLogin login = new VentanaLogin(sistema);
            login.setVisible(true);
        });
    }
}

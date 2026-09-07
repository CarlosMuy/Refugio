
package com.mycompany.refugio;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
 
    private final ControlSistema sistema;
    private JPanel panelMatriz;
    
    public VentanaPrincipal(ControlSistema sistema) {
        this.sistema = sistema;
        
        setTitle("Sisitema de Gestión - Centro de Rescate Animal");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblUsuario = new JLabel("Usuario Activo: " + sistema.getUsuarioLogueado() + " | Rol: " + sistema.getRolLogueado());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        panelHeader.add(lblUsuario, BorderLayout.WEST);
        
        JTabbedPane pestañas = new JTabbedPane();
        
        pestañas.addTab("Matriz del Refugio (4x5)", crearPanelMatriz());
        pestañas.addTab("Registro de Animales", crearPanelRegistro());
        pestañas.addTab("Bitácoras y Reportes", crearPanelBitacora());
        
        add(panelHeader, BorderLayout.NORTH);
        add(pestañas, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelMatriz() {
     JPanel panel = new JPanel(new BorderLayout(10, 10));
     panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
     
     panelMatriz = new JPanel(new GridLayout(4, 5, 5, 5));
     actualizarMatrizVisual();
     
     panel.add(new JLabel("Distribución de Zonas (4 filas) y Jaulas (5 columnas):", SwingConstants.CENTER), BorderLayout.NORTH);
     panel.add(panelMatriz, BorderLayout.CENTER);
     
     return panel;
    }
    
    private void actualizarMatrizVisual() {
        panelMatriz.removeAll();
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btnJaula = new JButton("Z" + (i + 1) + "_J" + (j + 1));
                btnJaula.setBackground(new Color(220, 240, 220));
                panelMatriz.add(btnJaula);
            }
        }
        panelMatriz.revalidate();
        panelMatriz.repaint();
    }
    
    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel (new FlowLayout());
        panel.add(new JLabel("Formulario para registrar animales en el refugio."));
        return panel;
    }
    
    private JPanel crearPanelBitacora() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Visor de Bitácoras y registros de persistencia."));
        return panel;
    }
}

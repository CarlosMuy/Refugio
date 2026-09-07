
package com.mycompany.refugio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JLabel lblMensaje;
    
    private int intentosFallidos = 0;
    private ControlSistema sistema;
    
    public VentanaLogin(ControlSistema sistema) {
        this.sistema = sistema;
        
        setTitle("Centro de Rescate Animal - Autenticación");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblUsuario = new JLabel("Usuario:");
        txtUsuario = new JTextField();
        
        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField();
        
        btnIngresar = new JButton("Iniciar Sesión");
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        
        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(new JLabel());
        panel.add(btnIngresar);
        
        add(panel, BorderLayout.CENTER);
        add(lblMensaje, BorderLayout.SOUTH);
        
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarIngreso();
            }
            
        });
    }
    
    private void validarIngreso() {
        String usr = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword()).trim();
        
        if (usr.isEmpty() || usr.contains(" ") || usr.length() < 4 || usr.length() > 15) {
          lblMensaje.setText("Contraseña debe tener minimo 6 caracteres.");
          sistema.registrarError("AUTENTICACION", "VALIDACION", "Contraseña corta");
          return;
        }
        if (sistema.autenticar(usr, pass)) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usr + " (" + sistema.getRolLogueado() + ")");
            this.dispose();
            SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema).setVisible(true));
        } else {
            intentosFallidos++;
            sistema.registrarError("AUTENTICACION", "LOGIN_FALLIDO",
                    "Contraseña incorrecta (intento " + intentosFallidos + " de 3)");
            
            if (intentosFallidos >= 3) {
                btnIngresar.setEnabled(false);
                txtUsuario.setEnabled(false);
                txtContrasena.setEnabled(false);
                lblMensaje.setText("Sesioón bloqueada, reinicie la aplicación");
            } else {
                lblMensaje.setText("Credenciales incorrectas. Intentos: " + intentosFallidos + "/3");
                
            }
        }          
      }
    }
    

   
   

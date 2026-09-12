
package com.mycompany.refugio;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class VentanaPrincipal extends JFrame {
 
    private final ControlSistema sistema;
    private JPanel panelMatriz;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cbEspecie;
    private JComboBox<String> comboEstadoClinico;
    private JSpinner spEdad;
    private JComboBox<Integer> cbFila;
    private JComboBox<Integer> cbColumna;
    
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
        // Pestañas
        JTabbedPane pestañas = new JTabbedPane();
        
        pestañas.addTab("Matriz del Refugio (4x5)", crearPanelMatriz());
        pestañas.addTab("Registro de Animales", crearPanelRegistro());
        pestañas.addTab("Bitácoras y Reportes", crearPanelBitacora());
        pestañas.addTab("Adopciones y Bajas", crearPanelAdopciones());
        pestañas.addTab("Adoptantes", crearPanelAdoptantes());
        pestañas.addTab("Solicitudes", crearPanelSolicitudes());
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
        if (panelMatriz == null) return;
        panelMatriz.removeAll();
        
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btnJaula = new JButton("Z" + (i + 1) + "_J" + (j + 1));
                
                Animal a = sistema.obtenerAnimalEnMatriz(i, j);
                if ( a != null) {
                btnJaula.setText("<html><center>Z" + (i + 1) + "_J" + (j + 1) + "<br><b>" + a.getCodigo() + "</b></center><html>");
                btnJaula.setBackground(new Color(255, 180, 180));
            } else {
                btnJaula.setText("<html><center>Z" + (i + 1) + "_J" + (j + 1) + "<br>[VACIO]</center></html>");
                btnJaula.setBackground(new Color(220, 240, 220));    
            }
            int fila = i;
            int col = j;
            btnJaula.addActionListener(e -> mostrarInfoJaula(fila, col));
            panelMatriz.add(btnJaula);
            }
        }
        panelMatriz.revalidate();
        panelMatriz.repaint();
    }
    
    
    private void mostrarInfoJaula(int fila, int col) {
        Animal a = sistema.obtenerAnimalEnMatriz(fila, col);
        if (a != null) {
            JOptionPane.showMessageDialog(this, "Jaula[" + (fila + 1) + "][" + (col + 1)+ "]\n" + "Nombre: " + a.getNombre() + "\n" + "Especie: " + a.getEspecie() + "\n" + "Edad: " + a.getEdadEstimada() + " Años", "Información de la Jaula", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "La jaula [" + (fila + 1) + "][" + (col + 1)+ "] se encuentra libre.", "Jaula Disponible", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JPanel crearPanelSolicitudes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Módulo de Solicitudes de Adopción"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtCodSolicitud = new JTextField(12);
        JTextField txtCodAnimal = new JTextField(12);
        JTextField txtCodAdoptante = new JTextField(12);
        JTextField txtFecha = new JTextField(12);
        
        JButton btnCrear = new JButton("Crear Solicitud");
        JButton btnAprobar = new JButton("Aprobar Solicitud");
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Código Solicitud:"));
        gbc.gridx = 1; panel.add(txtCodSolicitud, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Código Animal:"), gbc);
        gbc.gridx = 1; panel.add(txtCodAnimal, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Código Adoptante:"),  gbc);
        gbc.gridx = 1; panel.add(txtCodAdoptante, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; panel.add(txtFecha, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; panel.add(btnCrear, gbc);
        gbc.gridx = 1; panel.add(btnAprobar, gbc);
        
        btnCrear.addActionListener(e -> {
            boolean ok = sistema.crearSolicitudAdopcion(
                txtCodSolicitud.getText(), txtCodAnimal.getText(),
                txtCodAdoptante.getText(), txtFecha.getText()
            );
            if (ok) {
                JOptionPane.showMessageDialog(panel, "Solicitud creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAprobar.addActionListener(e -> {
            boolean ok = sistema.aprobarSolicitud(txtCodSolicitud.getText());
            if (ok) {
                JOptionPane.showMessageDialog(panel, "Solicitud aprobada y matriz actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarMatrizVisual();
            } else {
                JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }
    
    private JPanel crearPanelRegistro() {
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createTitledBorder("Formulario de Ingreso de Animales "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtCodigo = new JTextField(15);
        txtNombre = new JTextField(15);
        cbEspecie = new JComboBox<>(new String[]{"Perro", "Gato"});
        comboEstadoClinico = new JComboBox<>(new String[]{"EN_OBSERVACION", "EN_TRATAMIENTO", "APTO"});
        spEdad = new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));
        cbFila = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        cbColumna = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; panelMain.add(txtCodigo, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelMain.add(txtNombre, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Especie:"), gbc);
        gbc.gridx = 1; panelMain.add(cbEspecie, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Estado Clínico:"), gbc);
        gbc.gridx = 1; panelMain.add(comboEstadoClinico, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1; panelMain.add(spEdad, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Zona:"), gbc);
        gbc.gridx = 1; panelMain.add(cbFila, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Jaula:"), gbc);
        gbc.gridx = 1; panelMain.add(cbColumna, gbc); y++;
        
        JButton btnRegistrar = new JButton("Registrar Animal");
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRegistrar.setBackground(new Color(100, 180, 100));
        btnRegistrar.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panelMain.add(btnRegistrar, gbc);
        
        btnRegistrar.addActionListener(e -> registrarAnimal());
        
        JPanel panelContenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelContenedor.add(panelMain);
        return panelContenedor;
        
    }
    
    private JPanel crearPanelAdopciones() {
        JPanel panel = new JPanel(new  GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Modulo de Adopciones y Bajas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblCodigo = new JLabel("Código Animal:");
        JTextField txtBuscarCodigo = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");
        
        JLabel lblInfoAnimal = new JLabel("Animal seleccionado: Ninguno");
        JLabel lblEstadoClinico = new JLabel("Estado Clinico:");
        JComboBox<String> cbEstadoClinico = new JComboBox<>(new String[]{"EN_OBSERVACION", "EN_TRATAMIENTO", "APTO"});
        JButton btnActualizarClinico = new JButton("Actualizar Estado Clínico");
        JLabel lblEstadoAdopcion = new JLabel("Acción de Adopción:");
        JComboBox<String> cbEstadoAdopcion = new JComboBox<>(new String[]{"ADOPTADO", "ELIMINADO"});
        JButton btnProcesarAdopcion = new JButton("Procesar Adopcion / Baja");
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCodigo, gbc);
        gbc.gridx = 1; panel.add(txtBuscarCodigo, gbc);
        gbc.gridx = 2; panel.add(btnBuscar, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        panel.add(lblInfoAnimal, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblEstadoClinico, gbc);
        gbc.gridx = 1; panel.add(cbEstadoClinico, gbc);
        gbc.gridx = 2; panel.add(btnActualizarClinico, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblEstadoAdopcion, gbc);
        gbc.gridx = 1; panel.add(cbEstadoAdopcion, gbc);
        gbc.gridx = 2; panel.add(btnProcesarAdopcion, gbc);
        
        btnBuscar.addActionListener(e -> {
            String cod = txtBuscarCodigo.getText().trim();
            Animal a = sistema.buscarAnimalPorCodigo(cod);
            if (a != null) {
                if ("ELIMINADO".equals(a.getEstadoAdopcion())) {
                    JOptionPane.showMessageDialog(panel, "El animal ingresado se encuentra ELIMINADO.", "Atencion", JOptionPane.WARNING_MESSAGE);
                    lblInfoAnimal.setText("Animal seleccionado: " + a.getEstadoClinico());
                } else {
                    lblInfoAnimal.setText("Animal: " + a.getNombre() + " | Especie: " + a.getEspecie() + " | Clínico: " + a.getEstadoClinico() + "| Adopción: " + a.getEstadoAdopcion());
                    cbEstadoClinico.setSelectedItem(a.getEstadoClinico());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "No se encontró ningún animal activo con el código " + cod, "No Encontrado", JOptionPane.ERROR_MESSAGE);
                lblInfoAnimal.setText("Animal seleccionado: Ninguno");
            }
        });
        
        btnActualizarClinico.addActionListener(e -> {
        String cod = txtBuscarCodigo.getText().trim();
        String nuevoEstadoClinico = (String) cbEstadoClinico.getSelectedItem();
        boolean ok = sistema.actualizarEstadoClinico(cod, nuevoEstadoClinico);
        if (ok) {
            JOptionPane.showMessageDialog(panel, "Estado clínico actualizado correctamente a: " + nuevoEstadoClinico, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizarMatrizVisual();
        } else {
            JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
        
      btnProcesarAdopcion.addActionListener(e -> {
        String cod = txtBuscarCodigo.getText().trim();
        String nuevoEstadoAdopcion = (String) cbEstadoAdopcion.getSelectedItem();
        
        int resp = JOptionPane.showConfirmDialog(panel, "¿Desea confirmar el estado '" + nuevoEstadoAdopcion + "' para el animal " + cod + "?\nEsta acción liberará la jaula si estaba asignada.", "Confirmar Acción", JOptionPane.YES_NO_OPTION);
        
        if (resp == JOptionPane.YES_OPTION) {
            boolean ok = sistema.procesarBajaOLiberacion(cod, nuevoEstadoAdopcion);
            if (ok) {
                JOptionPane.showMessageDialog(panel, "Proceso completado. La jaula ha sido liberada y la acción registrada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarMatrizVisual();
                lblInfoAnimal.setText("Animal seleccionado: Ninguno");
                txtBuscarCodigo.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
      return panel;
    }
    
    private void registrarAnimal(){
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String especie = (String) cbEspecie.getSelectedItem();
        String estadoClinico = (String) comboEstadoClinico.getSelectedItem();
        int edad = (int) spEdad.getValue();
        int fila = (int) cbFila.getSelectedItem() - 1;
        int col = (int) cbColumna.getSelectedItem() - 1;
        
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Código del animal.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Nombre del animal.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean exito = sistema.registrarAnimalConValidacion(codigo, nombre, especie, edad, estadoClinico, fila, col);
        
        if(exito) {
            JOptionPane.showMessageDialog(this, "Animal registrado exitosamenten en la ubicación" + (fila + 1) + "-J" + (col + 1), "Exito", JOptionPane.INFORMATION_MESSAGE);
            txtCodigo.setText("");
            txtNombre.setText("");
            spEdad.setValue(1);
            actualizarMatrizVisual();
        } else {
            
            String ultimoError = sistema.getUltimoError();
            
            if (ultimoError == null || ultimoError.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el animal. Verifique los datos.", "Error de Asignación", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, ultimoError, "Error de validación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel crearPanelAdoptantes() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Gestión y Registro de Adoptantes"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel lblCodigo = new JLabel("Código (ej. AD-007):");
    JTextField txtCodigo = new JTextField(15);

    JLabel lblNombre = new JLabel("Nombre Completo:");
    JTextField txtNombre = new JTextField(15);

    JLabel lblDpi = new JLabel("DPI (13 dígitos):");
    JTextField txtDpi = new JTextField(15);

    JLabel lblTelefono = new JLabel("Teléfono (8 dígitos):");
    JTextField txtTelefono = new JTextField(15);

    JButton btnRegistrar = new JButton("Registrar Adoptante");
    JButton btnEliminar = new JButton("Eliminar por DPI");

    gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCodigo, gbc);
    gbc.gridx = 1; panel.add(txtCodigo, gbc);

    gbc.gridx = 0; gbc.gridy = 1; panel.add(lblNombre, gbc);
    gbc.gridx = 1; panel.add(txtNombre, gbc);

    gbc.gridx = 0; gbc.gridy = 2; panel.add(lblDpi, gbc);
    gbc.gridx = 1; panel.add(txtDpi, gbc);

    gbc.gridx = 0; gbc.gridy = 3; panel.add(lblTelefono, gbc);
    gbc.gridx = 1; panel.add(txtTelefono, gbc);

    gbc.gridx = 0; gbc.gridy = 4; panel.add(btnRegistrar, gbc);
    gbc.gridx = 1; panel.add(btnEliminar, gbc);

    btnRegistrar.addActionListener(e -> {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        String dpi = txtDpi.getText().trim();
        String tel = txtTelefono.getText().trim();

        boolean exito = sistema.registrarAdoptante(cod, nom, dpi, tel);
        if (exito) {
            JOptionPane.showMessageDialog(panel, "Adoptante registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtCodigo.setText("");
            txtNombre.setText("");
            txtDpi.setText("");
            txtTelefono.setText("");
        } else {
            JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    });

    btnEliminar.addActionListener(e -> {
        String dpi = txtDpi.getText().trim();
        if (dpi.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Ingrese el DPI del adoptante a eliminar en el campo DPI.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panel, "¿Desea eliminar al adoptante con DPI " + dpi + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = sistema.eliminarAdoptante(dpi);
            if (exito) {
                JOptionPane.showMessageDialog(panel, "Adoptante eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtDpi.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, sistema.getUltimoError(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    return panel;
}
    private JPanel crearPanelBitacora() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JTextArea txtBitacoraAcciones = new JTextArea();
        JTextArea txtBitacoraErrores = new JTextArea();
        txtBitacoraAcciones.setEditable(false);
        txtBitacoraErrores.setEditable(false);
        
        JTabbedPane subTabbed = new JTabbedPane();
        subTabbed.addTab("ACCIONES", new JScrollPane(txtBitacoraAcciones));
        subTabbed.addTab("Errores", new JScrollPane(txtBitacoraErrores));
        
        JButton btnRefrescar = new JButton("Actualizar Bitácora");
        btnRefrescar.addActionListener(e -> {
            txtBitacoraAcciones.setText(sistema.cargarBitacoraTexto("bitacora_acciones.txt"));
            txtBitacoraErrores.setText(sistema.cargarBitacoraTexto("bitacora_errores.txt"));
        });
        
        txtBitacoraAcciones.setText(sistema.cargarBitacoraTexto("bitacora_acciones.txt"));
        txtBitacoraErrores.setText(sistema.cargarBitacoraTexto("bitacora_errores.txt"));
        
        panel.add(subTabbed, BorderLayout.CENTER);
        panel.add(btnRefrescar, BorderLayout.SOUTH);
        
        return panel;
    }
}

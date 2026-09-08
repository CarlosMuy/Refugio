
package com.mycompany.refugio;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;

public class VentanaPrincipal extends JFrame {
 
    private final ControlSistema sistema;
    private JPanel panelMatriz;
    
    private JTextField txtNombre;
    private JComboBox<String> cbEspecie;
    private JSpinner spEdad;
    private JComboBox<Integer> cbFila;
    private JComboBox<Integer> cbColumna;
    
    public VentanaPrincipal(ControlSistema sistema) {
        this.sistema = sistema;
        
        setTitle("Sisitema de Gestión - Centro de Rescate Animal");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Header
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
                // Verificar Espacio en jaula
                Animal a = sistema.obtenerAnimalEnMatriz(i, j);
                if ( a != null) {
                btnJaula.setText("<html><center>Z" + (i + 1) + "_J" + (j + 1) + "<br><b>" + a.getNombre() + "</b></center><html>");
                btnJaula.setBackground(new Color(255, 180, 180));
            } else {
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
    
    private JPanel crearPanelRegistro() {
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createTitledBorder("Formulario de Ingreso de Animales "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtNombre = new JTextField(15);
        cbEspecie = new JComboBox<>(new String[]{"Perro", "Gato"});
        spEdad = new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));
        cbFila = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        cbColumna = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelMain.add(txtNombre, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelMain.add(new JLabel("Especie:"), gbc);
        gbc.gridx = 1; panelMain.add(cbEspecie, gbc); y++;
        
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
    
    private void registrarAnimal(){
        String nombre = txtNombre.getText().trim();
        String especie = (String) cbEspecie.getSelectedItem();
        int edad = (int) spEdad.getValue();
        int fila = (int) cbFila.getSelectedItem() - 1;
        int col = (int) cbColumna.getSelectedItem() - 1;
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Nombre del animal.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean exito = sistema.registrarAnimal(nombre, especie, edad, fila, col);
        
        if(exito) {
            JOptionPane.showMessageDialog(this, "Animal registrado exitosamenten en la ubicación" + (fila + 1) + "-J" + (col + 1), "Exito", JOptionPane.INFORMATION_MESSAGE);
            txtNombre.setText("");
            actualizarMatrizVisual();
        } else {
            JOptionPane.showMessageDialog(this, "La jaula" + (fila + 1) + "-J" + (col + 1) + "Ya esta ocupada.", "Error de Asignación", JOptionPane.ERROR_MESSAGE);
        }
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

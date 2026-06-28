package gui;

import estructura.ConexionBD;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FrmLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField     txtCorreo   = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JButton        btnIngresar = new JButton("Ingresar");
    private JButton        btnCancelar = new JButton("Cancelar");

    // ---------------------------------------------------------------
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }

    // ---------------------------------------------------------------
    public FrmLogin() {
        setTitle("SGRO - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setContentPane(root);

        root.add(panelTitulo(),    BorderLayout.NORTH);
        root.add(panelFormulario(), BorderLayout.CENTER);
        root.add(panelBotones(),   BorderLayout.SOUTH);

        configurarEventos();

        // Enter en cualquier campo dispara el login
        KeyAdapter enterLogin = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) iniciarSesion();
            }
        };
        txtCorreo.addKeyListener(enterLogin);
        txtPassword.addKeyListener(enterLogin);
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Reservas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(0, 51, 102));
        panel.add(lblTitulo);
        return panel;
    }

    // ---------------------------------------------------------------
    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Credenciales",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints lbl = new GridBagConstraints();
        lbl.insets  = new Insets(10, 10, 10, 6);
        lbl.anchor  = GridBagConstraints.EAST;

        GridBagConstraints fld = new GridBagConstraints();
        fld.insets  = new Insets(10, 0, 10, 10);
        fld.fill    = GridBagConstraints.HORIZONTAL;
        fld.weightx = 1.0;

        lbl.gridx = 0; lbl.gridy = 0; panel.add(new JLabel("Correo:"), lbl);
        fld.gridx = 1; fld.gridy = 0; panel.add(txtCorreo, fld);

        lbl.gridx = 0; lbl.gridy = 1; panel.add(new JLabel("Password:"), lbl);
        fld.gridx = 1; fld.gridy = 1; panel.add(txtPassword, fld);

        return panel;
    }

    // ---------------------------------------------------------------
    private JPanel panelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        btnIngresar.setPreferredSize(new Dimension(120, 32));
        btnIngresar.setBackground(new Color(0, 123, 255));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);

        btnCancelar.setPreferredSize(new Dimension(120, 32));
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);

        panel.add(btnIngresar);
        panel.add(btnCancelar);
        return panel;
    }

    // ---------------------------------------------------------------
    private void configurarEventos() {
        btnIngresar.addActionListener(e -> iniciarSesion());
        btnCancelar.addActionListener(e -> System.exit(0));
    }

    // ---------------------------------------------------------------
    private void iniciarSesion() {
        String correo   = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (correo.isEmpty()) {
            mostrarError("Ingrese su correo."); txtCorreo.requestFocusInWindow(); return;
        }
        if (password.isEmpty()) {
            mostrarError("Ingrese su contraseña."); txtPassword.requestFocusInWindow(); return;
        }

        String nombreCliente = validarCredenciales(correo, password);
        if (nombreCliente != null) {
            dispose();
            new FrmMenuPrincipal(nombreCliente).setVisible(true);
        } else {
            mostrarError("Correo o contraseña incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }

    // ---------------------------------------------------------------
    // Retorna el nombre del cliente si las credenciales son válidas, o null si no
    private String validarCredenciales(String correo, String password) {
        String sql = "SELECT nombre FROM clientes WHERE correo = ? AND password = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nombre");
            return null;
        } catch (Exception e) {
            mostrarError("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

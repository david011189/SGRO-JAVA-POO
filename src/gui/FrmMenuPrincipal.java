package gui;

import javax.swing.*;
import java.awt.*;

public class FrmMenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private String nombreCliente;

    public FrmMenuPrincipal(String nombreCliente) {
        this.nombreCliente = nombreCliente;
        setTitle("SGRO - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setContentPane(root);

        // Título
        JLabel lblTitulo = new JLabel("Bienvenido, " + nombreCliente, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 51, 102));
        root.add(lblTitulo, BorderLayout.NORTH);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 14));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        JButton btnClientes  = crearBoton("Mantenimiento de Clientes",  new Color(0, 123, 255));
        JButton btnReservas  = crearBoton("Registro de Reservas",        new Color(40, 167, 69));
        JButton btnSalir     = crearBoton("Cerrar Sesión",               new Color(220, 53, 69));

        panelBotones.add(btnClientes);
        panelBotones.add(btnReservas);
        panelBotones.add(btnSalir);
        root.add(panelBotones, BorderLayout.CENTER);

        // Eventos
        btnClientes.addActionListener(e -> {
            new FrmMantenimientoClientes().setVisible(true);
        });

        btnReservas.addActionListener(e -> {
            new FrmReserva(nombreCliente).setVisible(true);
        });

        btnSalir.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "¿Desea cerrar sesión?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                dispose();
                new FrmLogin().setVisible(true);
            }
        });
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

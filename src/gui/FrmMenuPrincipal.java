package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class FrmMenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    public FrmMenuPrincipal(String nombre, String rol) {
        setTitle("SGRO - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        setContentPane(root);

        // Foto del hotel en la parte superior
        root.add(panelFoto(), BorderLayout.NORTH);

        // Encabezado: bienvenida + badge de rol
        root.add(panelEncabezado(nombre, rol), BorderLayout.CENTER);

        // Botones según rol
        root.add(construirBotones(nombre, rol), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(480, 300));
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------------
    private JPanel panelFoto() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        URL url = getClass().getResource("/recursos/hotel_barlen.png");
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            Image escalada = original.getImage().getScaledInstance(480, 200, Image.SCALE_SMOOTH);
            JLabel lblFoto = new JLabel(new ImageIcon(escalada));
            lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lblFoto, BorderLayout.CENTER);
        } else {
            JLabel fallback = new JLabel("Hotel Barlen", SwingConstants.CENTER);
            fallback.setFont(new Font("Segoe UI", Font.BOLD, 20));
            fallback.setForeground(new Color(0, 51, 102));
            fallback.setPreferredSize(new Dimension(480, 80));
            panel.add(fallback, BorderLayout.CENTER);
        }
        return panel;
    }

    // ---------------------------------------------------------------
    private JPanel panelEncabezado(String nombre, String rol) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 6, 20));
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblBienvenido = new JLabel("Bienvenido, " + nombre, SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBienvenido.setForeground(new Color(0, 51, 102));
        lblBienvenido.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRol = new JLabel(rolLabel(rol), SwingConstants.CENTER);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRol.setForeground(Color.WHITE);
        lblRol.setOpaque(true);
        lblRol.setBackground(rolColor(rol));
        lblRol.setBorder(BorderFactory.createEmptyBorder(3, 14, 3, 14));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblBienvenido);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblRol);
        return panel;
    }

    // ---------------------------------------------------------------
    private JPanel construirBotones(String nombre, String rol) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 40, 14, 40));
        panel.setBackground(new Color(245, 245, 245));

        switch (rol) {
            case "admin":
                panel.add(boton("Mantenimiento de Clientes",     new Color(0, 123, 255),
                        e -> new FrmMantenimientoClientes().setVisible(true)));
                panel.add(Box.createVerticalStrut(8));
                panel.add(boton("Mantenimiento de Habitaciones", new Color(0, 150, 136),
                        e -> new FrmMantenimientoHabitaciones().setVisible(true)));
                panel.add(Box.createVerticalStrut(8));
                panel.add(boton("Hacer una Reserva",             new Color(40, 167, 69),
                        e -> new FrmCotizacion(nombre).setVisible(true)));
                panel.add(Box.createVerticalStrut(8));
                panel.add(boton("Ver todas las Reservas",        new Color(255, 140, 0),
                        e -> new FrmReserva(nombre).setVisible(true)));
                break;

            case "recepcionista":
                panel.add(boton("Hacer una Reserva", new Color(40, 167, 69),
                        e -> new FrmCotizacion(nombre).setVisible(true)));
                panel.add(Box.createVerticalStrut(8));
                panel.add(boton("Ver todas las Reservas", new Color(255, 140, 0),
                        e -> new FrmReserva(nombre).setVisible(true)));
                break;

            case "cliente":
            default:
                panel.add(boton("Hacer una Reserva", new Color(40, 167, 69),
                        e -> new FrmCotizacion(nombre).setVisible(true)));
                panel.add(Box.createVerticalStrut(8));
                panel.add(boton("Mis Reservas",       new Color(0, 150, 136),
                        e -> new FrmMisReservas(nombre).setVisible(true)));
                break;
        }

        panel.add(Box.createVerticalStrut(8));
        panel.add(boton("Cerrar Sesión", new Color(220, 53, 69), e -> {
            int ok = JOptionPane.showConfirmDialog(this, "¿Desea cerrar sesión?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                dispose();
                new FrmLogin().setVisible(true);
            }
        }));

        return panel;
    }

    // ---------------------------------------------------------------
    private JButton boton(String texto, Color color, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(accion);
        return btn;
    }

    private String rolLabel(String rol) {
        switch (rol) {
            case "admin":         return "Administrador";
            case "recepcionista": return "Recepcionista";
            default:              return "Cliente";
        }
    }

    private Color rolColor(String rol) {
        switch (rol) {
            case "admin":         return new Color(102, 16, 242);
            case "recepcionista": return new Color(0, 123, 255);
            default:              return new Color(40, 167, 69);
        }
    }
}

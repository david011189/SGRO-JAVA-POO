package gui;

import estructura.ConexionBD;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Historial de reservas del cliente logueado (RF-33).
 */
public class FrmMisReservas extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String clienteLogueado;
    private DefaultTableModel modelo;

    // ---------------------------------------------------------------
    public FrmMisReservas(String clienteLogueado) {
        this.clienteLogueado = clienteLogueado;

        setTitle("Mis Reservas — " + clienteLogueado);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        JLabel lbl = new JLabel("Mis Reservas — " + clienteLogueado, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(0, 51, 102));
        root.add(lbl, BorderLayout.NORTH);

        root.add(panelTabla(),   BorderLayout.CENTER);
        root.add(panelBotones(), BorderLayout.SOUTH);

        cargar();
    }

    // ---------------------------------------------------------------
    private JScrollPane panelTabla() {
        modelo = new DefaultTableModel(
                new String[]{"N° Reserva", "Ingreso", "Salida", "Noches", "Habitación", "Método pago", "Total", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);

        int[] anchos = {90, 90, 90, 60, 80, 120, 80, 90};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Historial de reservas",
                TitledBorder.LEFT, TitledBorder.TOP));
        return sc;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        p.setOpaque(false);

        JButton btnNueva = new JButton("Nueva reserva");
        JButton btnCerrar = new JButton("Cerrar");

        btnNueva.setBackground(new Color(0, 123, 255));
        btnNueva.setForeground(Color.WHITE);
        btnNueva.setFocusPainted(false);
        btnNueva.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnCerrar.setBackground(new Color(108, 117, 125));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);

        btnNueva.addActionListener(e -> {
            dispose();
            new FrmCotizacion(clienteLogueado).setVisible(true);
        });

        btnCerrar.addActionListener(e -> dispose());

        p.add(btnNueva);
        p.add(btnCerrar);
        return p;
    }

    // ---------------------------------------------------------------
    private void cargar() {
        modelo.setRowCount(0);
        String sql = "SELECT id, fecha_ingreso, fecha_salida, dias, habitacion, metodo_pago, costo, estado_reserva " +
                     "FROM reserva WHERE cliente = ? ORDER BY id DESC";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clienteLogueado);
            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    String.format("RES-%03d", rs.getInt("id")),
                    sdf.format(rs.getDate("fecha_ingreso")),
                    sdf.format(rs.getDate("fecha_salida")),
                    rs.getInt("dias"),
                    rs.getInt("habitacion"),
                    rs.getString("metodo_pago").toUpperCase(),
                    "S/ " + String.format("%.2f", rs.getDouble("costo")),
                    rs.getString("estado_reserva").toUpperCase()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + ex.getMessage());
        }
    }
}

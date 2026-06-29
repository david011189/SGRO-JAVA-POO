package gui;

import estructura.ConexionBD;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;

/**
 * Paso 1 — Cotización: el cliente elige fechas y ve habitaciones disponibles.
 */
public class FrmCotizacion extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String clienteLogueado;

    private JDateChooser dtIngreso  = new JDateChooser();
    private JDateChooser dtSalida   = new JDateChooser();
    private JLabel       lblDias    = new JLabel("—");
    private JLabel       lblInfo    = new JLabel(" ");

    private JTable             tabla;
    private DefaultTableModel  modelo;

    private JButton btnBuscar    = new JButton("Buscar disponibilidad");
    private JButton btnSeleccionar = new JButton("Seleccionar habitación →");
    private JButton btnVolver    = new JButton("← Volver al menú");

    // ---------------------------------------------------------------
    public FrmCotizacion(String clienteLogueado) {
        this.clienteLogueado = clienteLogueado;
        setTitle("SGRO — Cotización de Habitaciones (Paso 1 de 4)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 530);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),    BorderLayout.NORTH);
        root.add(panelCentro(),    BorderLayout.CENTER);
        root.add(panelBotones(),   BorderLayout.SOUTH);

        configurarEventos();
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("Paso 1: Cotización de Habitaciones", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(new Color(0, 51, 102));

        JLabel pasos = new JLabel("[ 1. Cotización ]  →  2. Carrito  →  3. Pago  →  4. Confirmación", SwingConstants.CENTER);
        pasos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pasos.setForeground(new Color(100, 100, 100));

        p.add(lbl,   BorderLayout.CENTER);
        p.add(pasos, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCentro() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.add(panelFechas(), BorderLayout.NORTH);
        p.add(panelTabla(),  BorderLayout.CENTER);
        return p;
    }

    private JPanel panelFechas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Seleccione sus fechas",
                TitledBorder.LEFT, TitledBorder.TOP));

        dtIngreso.setDateFormatString("dd/MM/yyyy");
        dtIngreso.setPreferredSize(new Dimension(140, 30));
        dtSalida.setDateFormatString("dd/MM/yyyy");
        dtSalida.setPreferredSize(new Dimension(140, 30));

        lblDias.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDias.setForeground(new Color(0, 100, 0));

        btnBuscar.setBackground(new Color(0, 123, 255));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscar.setPreferredSize(new Dimension(200, 34));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.anchor = GridBagConstraints.WEST;

        // Fila 0: Fecha Ingreso | Fecha Salida | Noches
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Fecha Ingreso:"), c);
        c.gridx = 1;               p.add(dtIngreso, c);
        c.gridx = 2;               p.add(new JLabel("Fecha Salida:"), c);
        c.gridx = 3;               p.add(dtSalida, c);
        c.gridx = 4;               p.add(new JLabel("Noches:"), c);
        c.gridx = 5;               p.add(lblDias, c);

        // Fila 1: botón centrado abarcando todas las columnas
        GridBagConstraints cb = new GridBagConstraints();
        cb.gridx = 0; cb.gridy = 1; cb.gridwidth = 6;
        cb.anchor = GridBagConstraints.CENTER;
        cb.insets = new Insets(2, 10, 10, 10);
        p.add(btnBuscar, cb);

        return p;
    }

    private JScrollPane panelTabla() {
        modelo = new DefaultTableModel(
                new String[]{"N° Hab.", "Tipo", "Capacidad", "Descripción", "Tarifa/noche", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);

        int[] anchos = {70, 80, 75, 270, 100, 90};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Habitaciones disponibles",
                TitledBorder.LEFT, TitledBorder.TOP));
        return sc;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        lblInfo.setForeground(new Color(180, 0, 0));
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        derecha.setOpaque(false);

        btnSeleccionar.setBackground(new Color(40, 167, 69));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSeleccionar.setEnabled(false);

        btnVolver.setBackground(new Color(108, 117, 125));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);

        derecha.add(btnVolver);
        derecha.add(btnSeleccionar);

        p.add(lblInfo,  BorderLayout.WEST);
        p.add(derecha,  BorderLayout.EAST);
        return p;
    }

    // ---------------------------------------------------------------
    private void configurarEventos() {

        java.beans.PropertyChangeListener actualizarDias = evt -> {
            Date fi = dtIngreso.getDate();
            Date fs = dtSalida.getDate();
            if (fi != null && fs != null && fs.after(fi)) {
                long n = (fs.getTime() - fi.getTime()) / (1000 * 60 * 60 * 24);
                lblDias.setText(n + " noche(s)");
            } else {
                lblDias.setText("—");
            }
            btnSeleccionar.setEnabled(false);
            modelo.setRowCount(0);
        };
        dtIngreso.addPropertyChangeListener("date", actualizarDias);
        dtSalida.addPropertyChangeListener("date", actualizarDias);

        btnBuscar.addActionListener(e -> buscarDisponibles());

        btnSeleccionar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { lblInfo.setText("Seleccione una habitación de la lista."); return; }

            int    numHab  = (int) modelo.getValueAt(fila, 0);
            String tipo    = modelo.getValueAt(fila, 1).toString();
            String desc    = modelo.getValueAt(fila, 3).toString();
            double tarifa  = Double.parseDouble(modelo.getValueAt(fila, 4).toString().replace("S/ ", ""));
            Date   fi      = dtIngreso.getDate();
            Date   fs      = dtSalida.getDate();
            long   noches  = (fs.getTime() - fi.getTime()) / (1000 * 60 * 60 * 24);
            double total   = tarifa * noches;

            dispose();
            new FrmCarrito(clienteLogueado, numHab, tipo, desc, tarifa, fi, fs, (int) noches, total)
                    .setVisible(true);
        });

        btnVolver.addActionListener(e -> dispose());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnSeleccionar.setEnabled(tabla.getSelectedRow() >= 0);
        });
    }

    // ---------------------------------------------------------------
    private void buscarDisponibles() {
        Date fi = dtIngreso.getDate();
        Date fs = dtSalida.getDate();

        if (fi == null || fs == null) { lblInfo.setText("Seleccione ambas fechas."); return; }
        if (!fs.after(fi))            { lblInfo.setText("La salida debe ser posterior al ingreso."); return; }

        lblInfo.setText(" ");
        modelo.setRowCount(0);
        btnSeleccionar.setEnabled(false);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String ingBD = sdf.format(fi);
        String salBD = sdf.format(fs);

        // Habitación disponible = estado 'disponible' Y sin reservas solapadas
        String sql = "SELECT h.numero, h.tipo, h.capacidad, h.descripcion, h.tarifa, h.estado " +
                     "FROM habitaciones h " +
                     "WHERE h.estado = 'disponible' " +
                     "AND h.numero NOT IN ( " +
                     "  SELECT habitacion FROM reserva " +
                     "  WHERE NOT (fecha_salida <= ? OR fecha_ingreso >= ?) " +
                     ") ORDER BY h.numero";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ingBD);
            ps.setString(2, salBD);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("numero"),
                    rs.getString("tipo"),
                    rs.getInt("capacidad"),
                    rs.getString("descripcion"),
                    "S/ " + rs.getDouble("tarifa"),
                    rs.getString("estado")
                });
                count++;
            }
            if (count == 0)
                lblInfo.setText("No hay habitaciones disponibles para esas fechas.");
        } catch (Exception ex) {
            lblInfo.setText("Error: " + ex.getMessage());
        }
    }
}

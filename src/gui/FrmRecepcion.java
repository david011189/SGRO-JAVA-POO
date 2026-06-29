package gui;

import estructura.ConexionBD;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Módulo de Recepción: gestión de Check-in y Check-out.
 * Solo accesible para admin y recepcionista.
 */
public class FrmRecepcion extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String usuarioLogueado;

    private JTextField     txtBuscar  = new JTextField();
    private JTable         tabla;
    private DefaultTableModel modelo;

    // Detalle de la reserva seleccionada
    private JLabel lblIdVal      = new JLabel("-");
    private JLabel lblClienteVal = new JLabel("-");
    private JLabel lblHabVal     = new JLabel("-");
    private JLabel lblIngresoVal = new JLabel("-");
    private JLabel lblSalidaVal  = new JLabel("-");
    private JLabel lblEstadoVal  = new JLabel("-");
    private JLabel lblCheckinVal = new JLabel("-");
    private JLabel lblCheckoutVal= new JLabel("-");

    private JButton btnCheckin  = new JButton("✔  Check-in");
    private JButton btnCheckout = new JButton("✔  Check-out");
    private JButton btnBuscar   = new JButton("Buscar");
    private JButton btnTodos    = new JButton("Ver todos");

    private int idSeleccionado   = -1;
    private String estadoActual  = "";

    // ---------------------------------------------------------------
    public FrmRecepcion(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;

        setTitle("SGRO — Recepción (Check-in / Check-out)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),    BorderLayout.NORTH);
        root.add(panelIzquierdo(), BorderLayout.WEST);
        root.add(panelDerecho(),   BorderLayout.CENTER);

        actualizarBotones();
        cargarReservas(null);
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("RECEPCIÓN — CHECK-IN / CHECK-OUT", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(0, 51, 102));

        JLabel sub = new JLabel("Seleccione una reserva de la tabla para registrar la llegada o salida del huésped",
                SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        sub.setForeground(new Color(100, 100, 100));

        p.add(lbl, BorderLayout.CENTER);
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------------
    private JPanel panelIzquierdo() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(320, 0));

        outer.add(panelDetalle(),  BorderLayout.CENTER);
        outer.add(panelAcciones(), BorderLayout.SOUTH);
        return outer;
    }

    private JPanel panelDetalle() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Detalle del Huésped",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(7, 12, 7, 6);

        GridBagConstraints vc = new GridBagConstraints();
        vc.anchor  = GridBagConstraints.WEST;
        vc.fill    = GridBagConstraints.HORIZONTAL;
        vc.weightx = 1.0;
        vc.insets  = new Insets(7, 0, 7, 12);

        // Estilo de valores
        for (JLabel lbl : new JLabel[]{lblIdVal, lblClienteVal, lblHabVal,
                lblIngresoVal, lblSalidaVal, lblEstadoVal, lblCheckinVal, lblCheckoutVal}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        lblEstadoVal.setFont(new Font("Segoe UI", Font.BOLD, 13));

        Object[][] filas = {
            {"N° Reserva:",   lblIdVal},
            {"Cliente:",      lblClienteVal},
            {"Habitación:",   lblHabVal},
            {"F. Ingreso:",   lblIngresoVal},
            {"F. Salida:",    lblSalidaVal},
            {"Estado:",       lblEstadoVal},
            {"Check-in:",     lblCheckinVal},
            {"Check-out:",    lblCheckoutVal},
        };

        for (int i = 0; i < filas.length; i++) {
            lc.gridx = 0; lc.gridy = i;
            JLabel etq = new JLabel(filas[i][0].toString());
            etq.setFont(new Font("Segoe UI", Font.BOLD, 12));
            p.add(etq, lc);

            vc.gridx = 1; vc.gridy = i;
            p.add((Component) filas[i][1], vc);
        }

        // Separador + leyenda de colores
        GridBagConstraints sc = new GridBagConstraints();
        sc.gridx = 0; sc.gridy = filas.length; sc.gridwidth = 2;
        sc.fill  = GridBagConstraints.HORIZONTAL;
        sc.insets = new Insets(10, 12, 4, 12);

        JPanel leyenda = new JPanel(new GridLayout(4, 1, 0, 3));
        leyenda.setOpaque(false);
        leyenda.add(chip("● Confirmada  — esperando llegada",  new Color(173, 216, 230)));
        leyenda.add(chip("● En curso    — huésped alojado",    new Color(255, 255, 153)));
        leyenda.add(chip("● Completada  — ya hizo check-out",  new Color(144, 238, 144)));
        leyenda.add(chip("● Cancelada   — reserva cancelada",  new Color(255, 182, 193)));
        p.add(leyenda, sc);

        return p;
    }

    private JLabel chip(String texto, Color fondo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setOpaque(true);
        lbl.setBackground(fondo);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return lbl;
    }

    private JPanel panelAcciones() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setOpaque(false);

        estilo(btnCheckin,  new Color(0, 150, 136));
        estilo(btnCheckout, new Color(255, 140, 0));

        btnCheckin.addActionListener(e  -> registrarCheckin());
        btnCheckout.addActionListener(e -> registrarCheckout());

        p.add(btnCheckin);
        p.add(btnCheckout);
        return p;
    }

    // ---------------------------------------------------------------
    private JPanel panelDerecho() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setOpaque(false);

        p.add(panelBusqueda(), BorderLayout.NORTH);
        p.add(panelTabla(),    BorderLayout.CENTER);
        return p;
    }

    private JPanel panelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setOpaque(false);

        txtBuscar.setPreferredSize(new Dimension(220, 30));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        estilo(btnBuscar, new Color(0, 123, 255));
        estilo(btnTodos,  new Color(108, 117, 125));
        btnBuscar.setPreferredSize(new Dimension(90, 30));
        btnTodos.setPreferredSize(new Dimension(100, 30));

        btnBuscar.addActionListener(e -> cargarReservas(txtBuscar.getText().trim()));
        btnTodos.addActionListener(e  -> { txtBuscar.setText(""); cargarReservas(null); });

        // Buscar también al presionar Enter
        txtBuscar.addActionListener(e -> cargarReservas(txtBuscar.getText().trim()));

        JLabel lbl = new JLabel("Buscar por cliente o N° reserva:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        p.add(lbl);
        p.add(txtBuscar);
        p.add(btnBuscar);
        p.add(btnTodos);
        return p;
    }

    private JScrollPane panelTabla() {
        modelo = new DefaultTableModel(
                new String[]{"N° Reserva", "Cliente", "Habitación", "Ingreso", "Salida", "Estado", "Check-in", "Check-out"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setGridColor(new Color(210, 210, 210));

        int[] anchos = {85, 130, 80, 90, 90, 90, 120, 120};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Colorear filas según estado
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) {
                    setBackground(new Color(0, 123, 255));
                    setForeground(Color.WHITE);
                } else {
                    String estado = modelo.getValueAt(row, 5) != null
                            ? modelo.getValueAt(row, 5).toString() : "";
                    switch (estado) {
                        case "confirmada":
                            setBackground(new Color(173, 216, 230));
                            setForeground(Color.DARK_GRAY); break;
                        case "en_curso":
                            setBackground(new Color(255, 255, 153));
                            setForeground(new Color(100, 70, 0)); break;
                        case "completada":
                            setBackground(new Color(144, 238, 144));
                            setForeground(new Color(0, 80, 0)); break;
                        case "cancelada":
                            setBackground(new Color(255, 182, 193));
                            setForeground(new Color(150, 0, 0)); break;
                        default:
                            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 250, 255));
                            setForeground(Color.DARK_GRAY);
                    }
                }
                return this;
            }
        });

        // Clic en fila → cargar detalle
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFila();
        });

        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Reservas",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));
        return sc;
    }

    // ---------------------------------------------------------------
    private void cargarReservas(String filtro) {
        modelo.setRowCount(0);
        limpiarDetalle();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdtf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String sql = "SELECT id, cliente, habitacion, fecha_ingreso, fecha_salida, " +
                     "estado_reserva, fecha_checkin, fecha_checkout " +
                     "FROM reserva ";

        if (filtro != null && !filtro.isEmpty()) {
            sql += "WHERE cliente LIKE ? OR CAST(id AS CHAR) LIKE ? ";
        }
        sql += "ORDER BY " +
               "FIELD(estado_reserva,'en_curso','confirmada','completada','cancelada'), " +
               "fecha_ingreso ASC";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtro != null && !filtro.isEmpty()) {
                String like = "%" + filtro + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String checkin  = rs.getTimestamp("fecha_checkin")  != null
                        ? sdtf.format(rs.getTimestamp("fecha_checkin"))  : "-";
                String checkout = rs.getTimestamp("fecha_checkout") != null
                        ? sdtf.format(rs.getTimestamp("fecha_checkout")) : "-";

                modelo.addRow(new Object[]{
                    String.format("RES-%03d", rs.getInt("id")),
                    rs.getString("cliente"),
                    "N° " + rs.getInt("habitacion"),
                    sdf.format(rs.getDate("fecha_ingreso")),
                    sdf.format(rs.getDate("fecha_salida")),
                    rs.getString("estado_reserva"),
                    checkin,
                    checkout
                });
            }
        } catch (Exception ex) {
            error("Error al cargar reservas: " + ex.getMessage());
        }
    }

    private void cargarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { limpiarDetalle(); return; }

        String idStr = modelo.getValueAt(fila, 0).toString().replace("RES-", "");
        idSeleccionado = Integer.parseInt(idStr);
        estadoActual   = modelo.getValueAt(fila, 5).toString();

        lblIdVal.setText(modelo.getValueAt(fila, 0).toString());
        lblClienteVal.setText(modelo.getValueAt(fila, 1).toString());
        lblHabVal.setText(modelo.getValueAt(fila, 2).toString());
        lblIngresoVal.setText(modelo.getValueAt(fila, 3).toString());
        lblSalidaVal.setText(modelo.getValueAt(fila, 4).toString());
        lblCheckinVal.setText(modelo.getValueAt(fila, 6).toString());
        lblCheckoutVal.setText(modelo.getValueAt(fila, 7).toString());

        // Colorear badge de estado
        switch (estadoActual) {
            case "confirmada":
                lblEstadoVal.setText("CONFIRMADA");
                lblEstadoVal.setForeground(new Color(0, 80, 150)); break;
            case "en_curso":
                lblEstadoVal.setText("EN CURSO (alojado)");
                lblEstadoVal.setForeground(new Color(150, 100, 0)); break;
            case "completada":
                lblEstadoVal.setText("COMPLETADA");
                lblEstadoVal.setForeground(new Color(0, 120, 0)); break;
            case "cancelada":
                lblEstadoVal.setText("CANCELADA");
                lblEstadoVal.setForeground(new Color(180, 0, 0)); break;
        }

        actualizarBotones();
    }

    // ---------------------------------------------------------------
    private void registrarCheckin() {
        if (idSeleccionado < 0) return;

        int ok = JOptionPane.showConfirmDialog(this,
                "¿Registrar Check-in para " + lblClienteVal.getText() + " — " + lblHabVal.getText() + "?\n" +
                "Se marcará la reserva como EN CURSO.",
                "Confirmar Check-in", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE reserva SET estado_reserva='en_curso', fecha_checkin=NOW() WHERE id=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeleccionado);
            ps.executeUpdate();
            info("Check-in registrado correctamente.\nEl huésped está ahora alojado.");
            cargarReservas(txtBuscar.getText().trim().isEmpty() ? null : txtBuscar.getText().trim());
            limpiarDetalle();
        } catch (Exception ex) {
            error("Error al registrar check-in: " + ex.getMessage());
        }
    }

    private void registrarCheckout() {
        if (idSeleccionado < 0) return;

        int ok = JOptionPane.showConfirmDialog(this,
                "¿Registrar Check-out para " + lblClienteVal.getText() + " — " + lblHabVal.getText() + "?\n" +
                "La habitación quedará disponible nuevamente.",
                "Confirmar Check-out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        // Obtener número de habitación para liberarla
        String sqlHab = "SELECT habitacion FROM reserva WHERE id=?";
        try (Connection con = ConexionBD.conectar()) {
            int numHab = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlHab)) {
                ps.setInt(1, idSeleccionado);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) numHab = rs.getInt("habitacion");
            }

            // Actualizar reserva
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE reserva SET estado_reserva='completada', fecha_checkout=NOW() WHERE id=?")) {
                ps.setInt(1, idSeleccionado);
                ps.executeUpdate();
            }

            // Liberar habitación
            if (numHab > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE habitaciones SET estado='disponible' WHERE numero=?")) {
                    ps.setInt(1, numHab);
                    ps.executeUpdate();
                }
            }

            info("Check-out registrado correctamente.\nLa habitación N° " + numHab + " está nuevamente disponible.");
            cargarReservas(txtBuscar.getText().trim().isEmpty() ? null : txtBuscar.getText().trim());
            limpiarDetalle();
        } catch (Exception ex) {
            error("Error al registrar check-out: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------------
    private void limpiarDetalle() {
        idSeleccionado = -1;
        estadoActual   = "";
        for (JLabel lbl : new JLabel[]{lblIdVal, lblClienteVal, lblHabVal,
                lblIngresoVal, lblSalidaVal, lblEstadoVal, lblCheckinVal, lblCheckoutVal}) {
            lbl.setText("-");
            lbl.setForeground(Color.DARK_GRAY);
        }
        actualizarBotones();
    }

    private void actualizarBotones() {
        // Check-in: solo si estado es "confirmada"
        btnCheckin.setEnabled(estadoActual.equals("confirmada"));
        // Check-out: solo si estado es "en_curso"
        btnCheckout.setEnabled(estadoActual.equals("en_curso"));

        btnCheckin.setBackground(btnCheckin.isEnabled()
                ? new Color(0, 150, 136) : new Color(180, 180, 180));
        btnCheckout.setBackground(btnCheckout.isEnabled()
                ? new Color(255, 140, 0) : new Color(180, 180, 180));
    }

    private void estilo(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}

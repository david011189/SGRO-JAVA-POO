package gui;

import estructura.ConexionBD;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mantenimiento de Reservas — solo para admin y recepcionista.
 * El panel izquierdo se activa al seleccionar una fila de la tabla.
 */
public class FrmReserva extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String usuarioLogueado;

    // Campos detalle (solo lectura por defecto, se habilitan al seleccionar fila)
    private JTextField   txtId          = new JTextField();
    private JDateChooser dtIngreso      = new JDateChooser();
    private JDateChooser dtSalida       = new JDateChooser();
    private JTextField   txtDias        = new JTextField();
    private JTextField   txtCliente     = new JTextField();
    private JTextField   txtHabitacion  = new JTextField();
    private JTextField   txtCosto       = new JTextField();
    private JTextField   txtMetodoPago  = new JTextField();
    private JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"confirmada","cancelada"});

    // Tabla
    private JTable            tabla;
    private DefaultTableModel modelo;

    // Botones
    private JButton btnModificar = new JButton("Modificar");
    private JButton btnEliminar  = new JButton("Eliminar");
    private JButton btnLimpiar   = new JButton("Limpiar selección");

    private int idSeleccionado = -1;

    // ---------------------------------------------------------------
    public FrmReserva(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        setTitle("SGRO — Mantenimiento de Reservas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),  BorderLayout.NORTH);
        root.add(panelIzquierdo(), BorderLayout.WEST);
        root.add(panelTabla(),   BorderLayout.CENTER);

        configurarEventos();
        deshabilitarCampos();
        actualizarBotones(false);
        cargarReservas();
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("MANTENIMIENTO DE RESERVAS", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(0, 51, 102));
        JLabel sub = new JLabel("Seleccione una reserva de la tabla para ver su detalle y editarla", SwingConstants.CENTER);
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
        outer.setPreferredSize(new Dimension(310, 0));

        outer.add(panelDetalle(), BorderLayout.CENTER);
        outer.add(panelBotones(), BorderLayout.SOUTH);
        return outer;
    }

    private JPanel panelDetalle() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Detalle de la Reserva",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 12, 6, 6);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = new Insets(6, 0, 6, 12);

        dtIngreso.setDateFormatString("dd/MM/yyyy");
        dtSalida.setDateFormatString("dd/MM/yyyy");

        Object[][] campos = {
            {"N° Reserva:",    txtId},
            {"Fecha Ingreso:", dtIngreso},
            {"Fecha Salida:",  dtSalida},
            {"Días:",          txtDias},
            {"Cliente:",       txtCliente},
            {"Habitación:",    txtHabitacion},
            {"Costo (S/):",    txtCosto},
            {"Método pago:",   txtMetodoPago},
            {"Estado:",        cmbEstado},
        };

        for (int i = 0; i < campos.length; i++) {
            lc.gridx = 0; lc.gridy = i;
            JLabel lbl = new JLabel(campos[i][0].toString());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            p.add(lbl, lc);

            fc.gridx = 1; fc.gridy = i;
            p.add((Component) campos[i][1], fc);
        }

        return p;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setOpaque(false);

        JPanel fila1 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila1.setOpaque(false);

        estilo(btnModificar, new Color(40, 167, 69));
        estilo(btnEliminar,  new Color(220, 53, 69));
        estilo(btnLimpiar,   new Color(108, 117, 125));
        btnLimpiar.setPreferredSize(new Dimension(0, 36));

        fila1.add(btnModificar);
        fila1.add(btnEliminar);
        p.add(fila1);
        p.add(btnLimpiar);
        return p;
    }

    // ---------------------------------------------------------------
    private JScrollPane panelTabla() {
        modelo = new DefaultTableModel(
                new String[]{"N° Reserva","Cliente","Ingreso","Salida","Días","Habitación","Costo","Método pago","Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setGridColor(new Color(210, 210, 210));
        tabla.setShowGrid(true);

        int[] anchos = {85, 130, 90, 90, 45, 80, 70, 100, 90};
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
                    String estado = modelo.getValueAt(row, 8) != null
                            ? modelo.getValueAt(row, 8).toString() : "";
                    if (estado.equals("cancelada")) {
                        setBackground(new Color(255, 235, 235));
                        setForeground(new Color(150, 0, 0));
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 250, 255));
                        setForeground(Color.DARK_GRAY);
                    }
                }
                return this;
            }
        });

        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Lista de Reservas",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));
        return sc;
    }

    // ---------------------------------------------------------------
    private void configurarEventos() {

        // Clic en fila → cargar detalle
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFila();
        });

        // Calcular días al cambiar fechas
        java.beans.PropertyChangeListener calcDias = evt -> {
            Date fi = dtIngreso.getDate();
            Date fs = dtSalida.getDate();
            if (fi != null && fs != null && fs.after(fi)) {
                long d = (fs.getTime() - fi.getTime()) / (1000 * 60 * 60 * 24);
                txtDias.setText(String.valueOf(d));
            } else {
                txtDias.setText("");
            }
        };
        dtIngreso.addPropertyChangeListener("date", calcDias);
        dtSalida.addPropertyChangeListener("date", calcDias);

        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    // ---------------------------------------------------------------
    private void cargarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        String idStr = modelo.getValueAt(fila, 0).toString().replace("RES-", "");
        idSeleccionado = Integer.parseInt(idStr);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtId.setText(modelo.getValueAt(fila, 0).toString());
        txtCliente.setText(modelo.getValueAt(fila, 1).toString());
        try {
            dtIngreso.setDate(sdf.parse(modelo.getValueAt(fila, 2).toString()));
            dtSalida.setDate(sdf.parse(modelo.getValueAt(fila, 3).toString()));
        } catch (Exception ex) { ex.printStackTrace(); }
        txtDias.setText(modelo.getValueAt(fila, 4).toString());
        txtHabitacion.setText(modelo.getValueAt(fila, 5).toString());
        txtCosto.setText(modelo.getValueAt(fila, 6).toString().replace("S/ ", ""));
        txtMetodoPago.setText(modelo.getValueAt(fila, 7).toString());
        cmbEstado.setSelectedItem(modelo.getValueAt(fila, 8).toString());

        habilitarCampos(true);
        actualizarBotones(true);
    }

    private void modificar() {
        if (idSeleccionado < 0) return;
        if (dtIngreso.getDate() == null || dtSalida.getDate() == null) {
            error("Seleccione ambas fechas."); return;
        }
        if (!dtSalida.getDate().after(dtIngreso.getDate())) {
            error("La salida debe ser posterior al ingreso."); return;
        }
        double costo;
        try { costo = Double.parseDouble(txtCosto.getText().trim()); }
        catch (NumberFormatException ex) { error("Costo inválido."); return; }

        SimpleDateFormat sdfBD = new SimpleDateFormat("yyyy-MM-dd");
        String ingBD = sdfBD.format(dtIngreso.getDate());
        String salBD = sdfBD.format(dtSalida.getDate());
        long dias = (dtSalida.getDate().getTime() - dtIngreso.getDate().getTime()) / (1000*60*60*24);

        String sql = "UPDATE reserva SET fecha_ingreso=?, fecha_salida=?, habitacion=?, costo=?, dias=?, estado_reserva=? WHERE id=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ingBD);
            ps.setString(2, salBD);
            ps.setInt(3, Integer.parseInt(txtHabitacion.getText().trim()));
            ps.setDouble(4, costo);
            ps.setLong(5, dias);
            ps.setString(6, cmbEstado.getSelectedItem().toString());
            ps.setInt(7, idSeleccionado);
            ps.executeUpdate();
            info("Reserva modificada correctamente.");
            cargarReservas();
            limpiar();
        } catch (Exception ex) { error("Error: " + ex.getMessage()); }
    }

    private void eliminar() {
        if (idSeleccionado < 0) return;
        int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar reserva " + txtId.getText() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM reserva WHERE id=?")) {
            ps.setInt(1, idSeleccionado);
            ps.executeUpdate();
            info("Reserva eliminada.");
            cargarReservas();
            limpiar();
        } catch (Exception ex) { error("Error: " + ex.getMessage()); }
    }

    private void limpiar() {
        txtId.setText(""); txtCliente.setText(""); txtDias.setText("");
        txtHabitacion.setText(""); txtCosto.setText(""); txtMetodoPago.setText("");
        dtIngreso.setDate(null); dtSalida.setDate(null);
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
        deshabilitarCampos();
        actualizarBotones(false);
    }

    // ---------------------------------------------------------------
    private void cargarReservas() {
        modelo.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String sql = "SELECT id, cliente, fecha_ingreso, fecha_salida, dias, habitacion, costo, metodo_pago, estado_reserva " +
                     "FROM reserva ORDER BY id DESC";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    String.format("RES-%03d", rs.getInt("id")),
                    rs.getString("cliente"),
                    sdf.format(rs.getDate("fecha_ingreso")),
                    sdf.format(rs.getDate("fecha_salida")),
                    rs.getInt("dias"),
                    rs.getInt("habitacion"),
                    "S/ " + String.format("%.2f", rs.getDouble("costo")),
                    rs.getString("metodo_pago"),
                    rs.getString("estado_reserva")
                });
            }
        } catch (Exception ex) { error("Error al cargar reservas: " + ex.getMessage()); }
    }

    // ---------------------------------------------------------------
    private void habilitarCampos(boolean on) {
        Color bg = on ? Color.WHITE : new Color(235, 235, 235);
        for (JTextField f : new JTextField[]{txtCliente, txtHabitacion, txtCosto}) {
            f.setEditable(on); f.setBackground(bg);
        }
        // txtId, txtDias, txtMetodoPago: siempre solo lectura
        for (JTextField f : new JTextField[]{txtId, txtDias, txtMetodoPago}) {
            f.setEditable(false);
            f.setBackground(new Color(235, 235, 235));
        }
        dtIngreso.setEnabled(on);
        dtSalida.setEnabled(on);
        cmbEstado.setEnabled(on);
    }

    private void deshabilitarCampos() { habilitarCampos(false); }

    private void actualizarBotones(boolean activo) {
        btnModificar.setEnabled(activo);
        btnEliminar.setEnabled(activo);
    }

    private void estilo(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(0, 36));
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}

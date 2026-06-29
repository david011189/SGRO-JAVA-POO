package gui;

import estructura.Habitacion;
import estructura.MantenimientoHabitacion;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FrmMantenimientoHabitaciones extends JFrame {

    private static final long serialVersionUID = 1L;

    // Campos
    private JTextField     txtNumero      = crearCampoNumero();
    private JTextField     txtTipo        = new JTextField();
    private JTextField     txtCapacidad   = crearCampoEntero();
    private JTextField     txtDescripcion = new JTextField();
    private JTextField     txtTarifa      = new JTextField();
    private JComboBox<String> cmbEstado   = new JComboBox<>(new String[]{"disponible","ocupada","mantenimiento"});

    // Tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton btnNuevo     = new JButton("Nuevo");
    private JButton btnGuardar   = new JButton("Guardar");
    private JButton btnModificar = new JButton("Modificar");
    private JButton btnEliminar  = new JButton("Eliminar");
    private JButton btnLimpiar   = new JButton("Limpiar");

    private MantenimientoHabitacion mantenimiento = new MantenimientoHabitacion();
    private int numeroSeleccionado = -1;

    // ---------------------------------------------------------------
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new FrmMantenimientoHabitaciones().setVisible(true));
    }

    // ---------------------------------------------------------------
    public FrmMantenimientoHabitaciones() {
        setTitle("Mantenimiento de Habitaciones - SGRO");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 530);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(panelFormulario(), BorderLayout.NORTH);
        root.add(panelTabla(),      BorderLayout.CENTER);
        root.add(panelBotones(),    BorderLayout.SOUTH);

        configurarEventos();
        habilitarCampos(false);
        actualizarBotones(false);
        refrescarTabla();
    }

    // ---------------------------------------------------------------
    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Datos de la Habitación",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints lbl = new GridBagConstraints();
        lbl.insets = new Insets(7, 10, 7, 4);
        lbl.anchor = GridBagConstraints.EAST;

        GridBagConstraints fld = new GridBagConstraints();
        fld.insets  = new Insets(7, 0, 7, 10);
        fld.fill    = GridBagConstraints.HORIZONTAL;
        fld.weightx = 1.0;

        // Fila 0: Número | Tipo | Capacidad
        lbl.gridx=0; lbl.gridy=0; panel.add(new JLabel("N° Habitación:"), lbl);
        fld.gridx=1; fld.gridy=0; panel.add(txtNumero, fld);

        lbl.gridx=2; panel.add(new JLabel("Tipo:"), lbl);
        fld.gridx=3; panel.add(txtTipo, fld);

        lbl.gridx=4; panel.add(new JLabel("Capacidad:"), lbl);
        fld.gridx=5; panel.add(txtCapacidad, fld);

        // Fila 1: Descripción | Tarifa | Estado
        lbl.gridx=0; lbl.gridy=1; panel.add(new JLabel("Descripción:"), lbl);
        fld.gridx=1; fld.gridy=1; fld.gridwidth=2; panel.add(txtDescripcion, fld);
        fld.gridwidth=1;

        lbl.gridx=3; lbl.gridy=1; panel.add(new JLabel("Tarifa (S/):"), lbl);
        fld.gridx=4; fld.gridy=1; panel.add(txtTarifa, fld);

        lbl.gridx=5; panel.add(new JLabel("Estado:"), lbl);
        fld.gridx=6; panel.add(cmbEstado, fld);

        return panel;
    }

    // ---------------------------------------------------------------
    private JScrollPane panelTabla() {
        modeloTabla = new DefaultTableModel(
                new String[]{"N° Hab.", "Tipo", "Capacidad", "Descripción", "Tarifa", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));

        // Anchos de columna
        int[] anchos = {75, 80, 75, 260, 75, 100};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFila();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Lista de Habitaciones",
                TitledBorder.LEFT, TitledBorder.TOP));
        return scroll;
    }

    // ---------------------------------------------------------------
    private JPanel panelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        Dimension tam = new Dimension(120, 32);
        for (JButton b : new JButton[]{btnNuevo, btnGuardar, btnModificar, btnEliminar, btnLimpiar}) {
            b.setPreferredSize(tam);
            panel.add(b);
        }
        return panel;
    }

    // ---------------------------------------------------------------
    private void configurarEventos() {

        btnNuevo.addActionListener(e -> {
            limpiar();
            habilitarCampos(true);
            actualizarBotones(true);
            numeroSeleccionado = -1;
            tabla.clearSelection();
            txtNumero.requestFocusInWindow();
        });

        btnGuardar.addActionListener(e -> {
            if (!validar()) return;
            Habitacion h = construirHabitacion();
            String err = mantenimiento.registrarHabitacion(h);
            if (err != null) { mostrarError(err); return; }
            mostrarInfo("Habitación registrada correctamente.");
            refrescarTabla(); limpiar(); habilitarCampos(false); actualizarBotones(false);
        });

        btnModificar.addActionListener(e -> {
            if (numeroSeleccionado < 0) { mostrarError("Seleccione una habitación."); return; }
            if (!validar()) return;
            Habitacion h = construirHabitacion();
            String err = mantenimiento.modificarHabitacion(
                    numeroSeleccionado, h.getNumero(), h.getTipo(),
                    h.getCapacidad(), h.getDescripcion(), h.getPrecio(), h.getEstado());
            if (err != null) { mostrarError(err); return; }
            mostrarInfo("Habitación modificada correctamente.");
            refrescarTabla(); limpiar(); habilitarCampos(false); actualizarBotones(false);
            numeroSeleccionado = -1;
        });

        btnEliminar.addActionListener(e -> {
            if (numeroSeleccionado < 0) { mostrarError("Seleccione una habitación."); return; }
            int ok = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar habitación N° " + numeroSeleccionado + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                mantenimiento.eliminarHabitacion(numeroSeleccionado);
                mostrarInfo("Habitación eliminada.");
                refrescarTabla(); limpiar(); habilitarCampos(false); actualizarBotones(false);
                numeroSeleccionado = -1;
            }
        });

        btnLimpiar.addActionListener(e -> {
            limpiar(); habilitarCampos(false); actualizarBotones(false);
            tabla.clearSelection(); numeroSeleccionado = -1;
        });
    }

    // ---------------------------------------------------------------
    private void cargarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        numeroSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        txtNumero.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtTipo.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtCapacidad.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtDescripcion.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtTarifa.setText(modeloTabla.getValueAt(fila, 4).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 5).toString());
        habilitarCampos(true);
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Habitacion h : mantenimiento.getListaHabitaciones()) {
                modeloTabla.addRow(new Object[]{
                        h.getNumero(), h.getTipo(), h.getCapacidad(),
                        h.getDescripcion(), h.getPrecio(), h.getEstado()
                });
            }
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private Habitacion construirHabitacion() {
        Habitacion h = new Habitacion(
                Integer.parseInt(txtNumero.getText().trim()),
                txtTipo.getText().trim(),
                Double.parseDouble(txtTarifa.getText().trim()));
        h.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
        h.setDescripcion(txtDescripcion.getText().trim());
        h.setEstado(cmbEstado.getSelectedItem().toString());
        return h;
    }

    private boolean validar() {
        if (txtNumero.getText().trim().isEmpty())
            { mostrarError("El número de habitación es obligatorio."); txtNumero.requestFocusInWindow(); return false; }
        if (txtTipo.getText().trim().isEmpty())
            { mostrarError("El tipo es obligatorio."); txtTipo.requestFocusInWindow(); return false; }
        if (txtCapacidad.getText().trim().isEmpty())
            { mostrarError("La capacidad es obligatoria."); txtCapacidad.requestFocusInWindow(); return false; }
        if (txtDescripcion.getText().trim().isEmpty())
            { mostrarError("La descripción es obligatoria."); txtDescripcion.requestFocusInWindow(); return false; }
        if (txtTarifa.getText().trim().isEmpty())
            { mostrarError("La tarifa es obligatoria."); txtTarifa.requestFocusInWindow(); return false; }
        try { Double.parseDouble(txtTarifa.getText().trim()); }
        catch (NumberFormatException e)
            { mostrarError("La tarifa debe ser un número válido."); txtTarifa.requestFocusInWindow(); return false; }
        return true;
    }

    private void limpiar() {
        txtNumero.setText(""); txtTipo.setText(""); txtCapacidad.setText("");
        txtDescripcion.setText(""); txtTarifa.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    private void habilitarCampos(boolean on) {
        Color bg = on ? Color.WHITE : UIManager.getColor("Panel.background");
        for (JTextField f : new JTextField[]{txtNumero, txtTipo, txtCapacidad, txtDescripcion, txtTarifa}) {
            f.setEditable(on);
            f.setBackground(bg);
        }
        cmbEstado.setEnabled(on);
    }

    private void actualizarBotones(boolean modoNuevo) {
        btnGuardar.setEnabled(modoNuevo);
        btnModificar.setEnabled(!modoNuevo && numeroSeleccionado >= 0);
        btnEliminar.setEnabled(!modoNuevo && numeroSeleccionado >= 0);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    // Filtro: solo números enteros positivos
    private static JTextField crearCampoNumero() {
        JTextField campo = new JTextField();
        campo.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                String solo = str.replaceAll("[^0-9]", "");
                if (!solo.isEmpty()) super.insertString(offs, solo, a);
            }
        });
        return campo;
    }

    private static JTextField crearCampoEntero() {
        return crearCampoNumero();
    }
}

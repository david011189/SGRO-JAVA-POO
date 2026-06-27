package gui;

import estructura.Cliente;
import estructura.MantenimientoCliente;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class FrmMantenimientoClientes extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtDni      = crearCampoDni();
    private JTextField txtNombre   = new JTextField();
    private JTextField txtCorreo   = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo     = new JButton("Nuevo");
    private JButton btnGuardar   = new JButton("Guardar");
    private JButton btnModificar = new JButton("Modificar");
    private JButton btnEliminar  = new JButton("Eliminar");
    private JButton btnLimpiar   = new JButton("Limpiar");

    private MantenimientoCliente mantenimiento = new MantenimientoCliente();
    private String dniSeleccionado = null;

    private static JTextField crearCampoDni() {
        JTextField campo = new JTextField();
        campo.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                String soloNumeros = str.replaceAll("[^0-9]", "");
                if (getLength() + soloNumeros.length() <= 8)
                    super.insertString(offs, soloNumeros, a);
            }
        });
        return campo;
    }

    // ---------------------------------------------------------------
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new FrmMantenimientoClientes().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ---------------------------------------------------------------
    public FrmMantenimientoClientes() {
        setTitle("Mantenimiento de Clientes - SGRO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 530);
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
                BorderFactory.createEtchedBorder(), "Datos del Cliente",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints lbl = new GridBagConstraints();
        lbl.insets  = new Insets(8, 10, 8, 4);
        lbl.anchor  = GridBagConstraints.EAST;

        GridBagConstraints fld = new GridBagConstraints();
        fld.insets  = new Insets(8, 0, 8, 10);
        fld.fill    = GridBagConstraints.HORIZONTAL;
        fld.weightx = 1.0;

        // Fila 0
        lbl.gridx = 0; lbl.gridy = 0; panel.add(new JLabel("DNI:"), lbl);
        fld.gridx = 1; fld.gridy = 0; panel.add(txtDni, fld);

        lbl.gridx = 2; panel.add(new JLabel("Nombre:"), lbl);
        fld.gridx = 3; panel.add(txtNombre, fld);

        // Fila 1
        lbl.gridx = 0; lbl.gridy = 1; panel.add(new JLabel("Correo:"), lbl);
        fld.gridx = 1; fld.gridy = 1; panel.add(txtCorreo, fld);

        lbl.gridx = 2; panel.add(new JLabel("Password:"), lbl);
        fld.gridx = 3; panel.add(txtPassword, fld);

        return panel;
    }

    // ---------------------------------------------------------------
    private JScrollPane panelTabla() {
        modeloTabla = new DefaultTableModel(
                new String[]{"DNI", "Nombre", "Correo", "Password"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFila();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Lista de Clientes",
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
            dniSeleccionado = null;
            tabla.clearSelection();
            txtDni.requestFocusInWindow();
        });

        btnGuardar.addActionListener(e -> {
            if (!validar()) return;
            Cliente c = new Cliente(
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    new String(txtPassword.getPassword()).trim(),
                    txtDni.getText().trim());
            String err = mantenimiento.registrarCliente(c);
            if (err != null) { mostrarError(err); return; }
            mostrarInfo("Cliente registrado correctamente.");
            refrescarTabla();
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
        });

        btnModificar.addActionListener(e -> {
            if (dniSeleccionado == null) { mostrarError("Seleccione un cliente."); return; }
            if (!validar()) return;
            String err = mantenimiento.modificarCliente(
                    dniSeleccionado,
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    new String(txtPassword.getPassword()).trim(),
                    txtDni.getText().trim());
            if (err != null) { mostrarError(err); return; }
            mostrarInfo("Cliente modificado correctamente.");
            refrescarTabla();
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
            dniSeleccionado = null;
        });

        btnEliminar.addActionListener(e -> {
            if (dniSeleccionado == null) { mostrarError("Seleccione un cliente."); return; }
            int ok = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar cliente con DNI: " + dniSeleccionado + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                mantenimiento.eliminarCliente(dniSeleccionado);
                mostrarInfo("Cliente eliminado.");
                refrescarTabla();
                limpiar();
                habilitarCampos(false);
                actualizarBotones(false);
                dniSeleccionado = null;
            }
        });

        btnLimpiar.addActionListener(e -> {
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
            tabla.clearSelection();
            dniSeleccionado = null;
        });
    }

    // ---------------------------------------------------------------
    private void cargarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        dniSeleccionado       = (String) modeloTabla.getValueAt(fila, 0);
        txtDni.setText(dniSeleccionado);
        txtNombre.setText    ((String) modeloTabla.getValueAt(fila, 1));
        txtCorreo.setText    ((String) modeloTabla.getValueAt(fila, 2));
        txtPassword.setText  ((String) modeloTabla.getValueAt(fila, 3));
        habilitarCampos(true);
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Cliente c : mantenimiento.getListaClientes())
                modeloTabla.addRow(new Object[]{
                        c.getDni(), c.getNombre(), c.getCorreo(), c.getPassword()});
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private boolean validar() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty())
            { mostrarError("El DNI es obligatorio."); txtDni.requestFocusInWindow(); return false; }
        if (!dni.matches("\\d{8}"))
            { mostrarError("El DNI debe contener exactamente 8 dígitos numéricos."); txtDni.requestFocusInWindow(); return false; }
        if (txtNombre.getText().trim().isEmpty())
            { mostrarError("El nombre es obligatorio."); txtNombre.requestFocusInWindow(); return false; }
        if (txtCorreo.getText().trim().isEmpty() || !txtCorreo.getText().contains("@"))
            { mostrarError("Ingrese un correo válido."); txtCorreo.requestFocusInWindow(); return false; }
        if (new String(txtPassword.getPassword()).trim().isEmpty())
            { mostrarError("La contraseña es obligatoria."); txtPassword.requestFocusInWindow(); return false; }
        return true;
    }

    private void limpiar() {
        txtDni.setText(""); txtNombre.setText("");
        txtCorreo.setText(""); txtPassword.setText("");
    }

    private void habilitarCampos(boolean on) {
        Color bg = on ? Color.WHITE : UIManager.getColor("Panel.background");
        for (JTextField f : new JTextField[]{txtDni, txtNombre, txtCorreo, txtPassword}) {
            f.setEditable(on);
            f.setBackground(bg);
        }
    }

    private void actualizarBotones(boolean modoNuevo) {
        btnGuardar.setEnabled(modoNuevo);
        btnModificar.setEnabled(!modoNuevo && dniSeleccionado != null);
        btnEliminar.setEnabled(!modoNuevo && dniSeleccionado != null);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}

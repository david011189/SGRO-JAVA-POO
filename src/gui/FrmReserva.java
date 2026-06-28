package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import estructura.ConexionBD;
import java.sql.ResultSet;

import estructura.Cliente;
import estructura.Habitacion;
import estructura.Reserva;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

public class FrmReserva extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtId;
    private JDateChooser txtIngreso;
    private JDateChooser txtFechaSalida;
    private JTextField txtCliente;
    private JTextField txtHabitacion;
    private JButton btnRegistrar;
    private JButton btnLimpiar;
    private JTable table;
    private DefaultTableModel modelo;
    private ArrayList<Reserva> listaReservas = new ArrayList<>();
    private int contador;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JTextField txtCosto;
    private String clienteLogueado = "";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FrmReserva frame = new FrmReserva("");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public FrmReserva(String nombreCliente) {
        this.clienteLogueado = nombreCliente;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contador = obtenerSiguienteId();

        contentPane.setBackground(new java.awt.Color(240, 248, 255));

        JLabel lblTitulo = new JLabel("REGISTRO DE RESERVA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new java.awt.Color(0, 51, 102));
        lblTitulo.setBounds(199, 10, 250, 49);
        contentPane.add(lblTitulo);

        // ID Reserva
        JLabel lblId = new JLabel("ID Reserva");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblId.setBounds(127, 72, 84, 13);
        contentPane.add(lblId);
        txtId = new JTextField();
        txtId.setBounds(260, 72, 120, 25);
        txtId.setEditable(false);
        txtId.setText(String.format("RES-%03d", contador));
        contentPane.add(txtId);

        // Fecha Ingreso
        JLabel lblIngreso = new JLabel("Fecha Ingreso");
        lblIngreso.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblIngreso.setBounds(127, 108, 96, 13);
        contentPane.add(lblIngreso);
        txtIngreso = new JDateChooser();
        txtIngreso.setDateFormatString("dd/MM/yyyy");
        txtIngreso.setBounds(260, 104, 150, 25);
        contentPane.add(txtIngreso);

        // Fecha Salida
        JLabel lblSalida = new JLabel("Fecha Salida");
        lblSalida.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblSalida.setBounds(127, 145, 96, 13);
        contentPane.add(lblSalida);
        txtFechaSalida = new JDateChooser();
        txtFechaSalida.setDateFormatString("dd/MM/yyyy");
        txtFechaSalida.setBounds(260, 141, 150, 25);
        contentPane.add(txtFechaSalida);

        // Cliente
        JLabel lblCliente = new JLabel("Cliente");
        lblCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblCliente.setBounds(127, 183, 47, 13);
        contentPane.add(lblCliente);
        txtCliente = new JTextField();
        txtCliente.setBounds(260, 183, 150, 25);
        txtCliente.setText(clienteLogueado);
        txtCliente.setEditable(false);
        txtCliente.setBackground(new java.awt.Color(230, 230, 230));
        contentPane.add(txtCliente);

        // Habitacion
        JLabel lblHabitacion = new JLabel("Habitación");
        lblHabitacion.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblHabitacion.setBounds(127, 219, 64, 13);
        contentPane.add(lblHabitacion);
        txtHabitacion = new JTextField();
        txtHabitacion.setBounds(260, 219, 150, 25);
        contentPane.add(txtHabitacion);

        // Costo
        JLabel lblCosto = new JLabel("Costo");
        lblCosto.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblCosto.setBounds(127, 263, 64, 13);
        contentPane.add(lblCosto);
        txtCosto = new JTextField();
        txtCosto.setBounds(260, 260, 150, 25);
        txtCosto.setEditable(true);
        contentPane.add(txtCosto);

        // Botón Registrar
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new java.awt.Color(0, 123, 255));
        btnRegistrar.setForeground(java.awt.Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBounds(80, 360, 130, 35);
        contentPane.add(btnRegistrar);

        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (txtIngreso.getDate() == null) {
                        JOptionPane.showMessageDialog(null, "Seleccione fecha de ingreso"); return;
                    }
                    if (txtFechaSalida.getDate() == null) {
                        JOptionPane.showMessageDialog(null, "Seleccione fecha de salida"); return;
                    }
                    if (txtHabitacion.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Ingrese habitación"); return;
                    }
                    if (!txtHabitacion.getText().matches("\\d+")) {
                        JOptionPane.showMessageDialog(null, "La habitación debe contener solo números"); return;
                    }

                    Date fechaIngreso = txtIngreso.getDate();
                    Date fechaSalida  = txtFechaSalida.getDate();

                    if (!fechaSalida.after(fechaIngreso)) {
                        JOptionPane.showMessageDialog(null, "La fecha de salida debe ser posterior a la de ingreso"); return;
                    }

                    SimpleDateFormat sdf   = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdfBD = new SimpleDateFormat("yyyy-MM-dd");
                    String ingreso   = sdf.format(fechaIngreso);
                    String salida    = sdf.format(fechaSalida);
                    String ingresoBD = sdfBD.format(fechaIngreso);
                    String salidaBD  = sdfBD.format(fechaSalida);

                    if (txtCosto.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Ingrese el costo"); return;
                    }
                    double costo;
                    try {
                        costo = Double.parseDouble(txtCosto.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "El costo debe ser un número válido"); return;
                    }

                    int id = contador++;
                    String idVisual      = String.format("RES-%03d", id);
                    String nombreCliente = txtCliente.getText();
                    int numHabitacion    = Integer.parseInt(txtHabitacion.getText());

                    Connection conn = ConexionBD.conectar();
                    String sql = "INSERT INTO reserva(cliente, fecha_ingreso, fecha_salida, habitacion, costo) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, nombreCliente);
                    ps.setString(2, ingresoBD);
                    ps.setString(3, salidaBD);
                    ps.setInt(4, numHabitacion);
                    ps.setDouble(5, costo);
                    ps.executeUpdate();
                    ps.close();
                    conn.close();

                    modelo.addRow(new Object[]{idVisual, nombreCliente, ingreso, salida, numHabitacion, "S/ " + costo});
                    limpiarCampos();
                    JOptionPane.showMessageDialog(null, "Reserva registrada correctamente\nCosto: S/ " + costo);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // Botón Limpiar
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new java.awt.Color(108, 117, 125));
        btnLimpiar.setForeground(java.awt.Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBounds(250, 360, 130, 35);
        contentPane.add(btnLimpiar);
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // Tabla
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(430, 135, 500, 300);
        contentPane.add(scroll);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Cliente");
        modelo.addColumn("Ingreso");
        modelo.addColumn("Salida");
        modelo.addColumn("Habitación");
        modelo.addColumn("Costo");
        table.setModel(modelo);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Botón Eliminar
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setForeground(java.awt.Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBounds(80, 430, 130, 35);
        contentPane.add(btnEliminar);

        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = table.getSelectedRow();
                if (fila >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(null, "¿Eliminar reserva?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            String idTexto = modelo.getValueAt(fila, 0).toString().replace("RES-", "");
                            int idBD = Integer.parseInt(idTexto);
                            Connection conn = ConexionBD.conectar();
                            PreparedStatement ps = conn.prepareStatement("DELETE FROM reserva WHERE id=?");
                            ps.setInt(1, idBD);
                            ps.executeUpdate();
                            ps.close();
                            conn.close();
                            modelo.removeRow(fila);
                            JOptionPane.showMessageDialog(null, "Reserva eliminada");
                            limpiarCampos();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione una fila");
                }
            }
        });

        // Botón Modificar
        btnModificar = new JButton("Modificar");
        btnModificar.setBackground(new java.awt.Color(40, 167, 69));
        btnModificar.setForeground(java.awt.Color.WHITE);
        btnModificar.setFocusPainted(false);
        btnModificar.setBounds(250, 430, 130, 35);
        contentPane.add(btnModificar);

        btnModificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = table.getSelectedRow();
                if (fila < 0) { JOptionPane.showMessageDialog(null, "Seleccione una reserva"); return; }

                if (!txtHabitacion.getText().matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "La habitación debe contener solo números"); return;
                }
                if (txtIngreso.getDate() == null) {
                    JOptionPane.showMessageDialog(null, "Seleccione fecha de ingreso"); return;
                }
                if (txtFechaSalida.getDate() == null) {
                    JOptionPane.showMessageDialog(null, "Seleccione fecha de salida"); return;
                }

                Date fechaIngreso = txtIngreso.getDate();
                Date fechaSalida  = txtFechaSalida.getDate();

                if (!fechaSalida.after(fechaIngreso)) {
                    JOptionPane.showMessageDialog(null, "La fecha de salida debe ser posterior a la de ingreso"); return;
                }

                SimpleDateFormat sdf   = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdfBD = new SimpleDateFormat("yyyy-MM-dd");
                String ingreso   = sdf.format(fechaIngreso);
                String salida    = sdf.format(fechaSalida);
                String ingresoBD = sdfBD.format(fechaIngreso);
                String salidaBD  = sdfBD.format(fechaSalida);

                double costo;
                try {
                    costo = Double.parseDouble(txtCosto.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El costo debe ser un número válido"); return;
                }

                modelo.setValueAt(txtCliente.getText(), fila, 1);
                modelo.setValueAt(ingreso, fila, 2);
                modelo.setValueAt(salida,  fila, 3);
                modelo.setValueAt(txtHabitacion.getText(), fila, 4);
                modelo.setValueAt("S/ " + costo, fila, 5);

                try {
                    String idTexto = modelo.getValueAt(fila, 0).toString().replace("RES-", "");
                    int idBD = Integer.parseInt(idTexto);
                    Connection conn = ConexionBD.conectar();
                    String sql = "UPDATE reserva SET cliente=?, fecha_ingreso=?, fecha_salida=?, habitacion=?, costo=? WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, txtCliente.getText());
                    ps.setString(2, ingresoBD);
                    ps.setString(3, salidaBD);
                    ps.setInt(4, Integer.parseInt(txtHabitacion.getText()));
                    ps.setDouble(5, costo);
                    ps.setInt(6, idBD);
                    ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al modificar: " + ex.getMessage()); return;
                }

                JOptionPane.showMessageDialog(null, "Reserva actualizada");
                limpiarCampos();
            }
        });

        // Click en tabla → cargar fila
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = table.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modelo.getValueAt(fila, 0).toString());
                    txtCliente.setText(modelo.getValueAt(fila, 1).toString());

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        txtIngreso.setDate(sdf.parse(modelo.getValueAt(fila, 2).toString()));
                        txtFechaSalida.setDate(sdf.parse(modelo.getValueAt(fila, 3).toString()));
                    } catch (Exception ex) { ex.printStackTrace(); }

                    txtHabitacion.setText(modelo.getValueAt(fila, 4).toString());
                    txtCosto.setText(modelo.getValueAt(fila, 5).toString());
                }
            }
        });

        cargarReservas();
    }

    private int obtenerSiguienteId() {
        try {
            Connection conn = ConexionBD.conectar();
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(id) FROM reserva");
            ResultSet rs = ps.executeQuery();
            int siguiente = 1;
            if (rs.next()) siguiente = rs.getInt(1) + 1;
            rs.close(); ps.close(); conn.close();
            return siguiente;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private void cargarReservas() {
        try {
            Connection conn = ConexionBD.conectar();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM reserva");
            ResultSet rs = ps.executeQuery();
            modelo.setRowCount(0);
            while (rs.next()) {
                String ingreso = rs.getString("fecha_ingreso");
                String[] f1 = ingreso.split("-");
                ingreso = f1[2] + "/" + f1[1] + "/" + f1[0];
                String salida = rs.getString("fecha_salida");
                String[] f2 = salida.split("-");
                salida = f2[2] + "/" + f2[1] + "/" + f2[0];
                modelo.addRow(new Object[]{
                    "RES-" + String.format("%03d", rs.getInt("id")),
                    rs.getString("cliente"), ingreso, salida,
                    rs.getInt("habitacion"), "S/ " + rs.getDouble("costo")
                });
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtIngreso.setDate(null);
        txtFechaSalida.setDate(null);
        txtHabitacion.setText("");
        txtCosto.setText("");
        txtCliente.setText(clienteLogueado);
        contador = obtenerSiguienteId();
        txtId.setText(String.format("RES-%03d", contador));
    }
}

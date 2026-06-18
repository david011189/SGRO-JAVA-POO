package gui;


import estructura.Cliente;
import estructura.Habitacion;
import estructura.Reserva;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.text.SimpleDateFormat;
import java.util.Date;

// guardar varias reservas y mostrarlas en una tabla (JTable)
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
	private JTextField txtIngreso;
	private JTextField txtFechaSalida;
	private JTextField txtCliente;
	private JTextField txtHabitacion;
	private JButton btnRegistrar;
	private JButton btnLimpiar;
	private JTable table;
    private DefaultTableModel modelo;
    private ArrayList<Reserva> listaReservas = new ArrayList<>();
    private int contador = 1;
    private JButton btnEliminar;
    private JButton btnModificar;
   


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmReserva frame = new FrmReserva();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmReserva() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("Habitación");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_4.setBounds(127, 219, 64, 13);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNewLabel_3 = new JLabel("Cliente");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(127, 183, 47, 13);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_2 = new JLabel("Fecha Salida");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(127, 145, 96, 13);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_1 = new JLabel("Fecha Ingreso");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(127, 108, 96, 13);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("ID Reserva");
		lblNewLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblNewLabel.setBounds(127, 72, 84, 13);
		contentPane.add(lblNewLabel);
		
		txtId = new JTextField();
		txtId.setBounds(260, 72, 96, 19);
		contentPane.add(txtId);
		txtId.setColumns(10);
         //ID CREACION AUTOMATICA
        txtId.setEditable(false);
        txtId.setText(String.format("RES-%03d", contador));

		
		txtIngreso = new JTextField();
		txtIngreso.setBounds(260, 108, 96, 18);
		contentPane.add(txtIngreso);
		txtIngreso.setColumns(10);
		
		txtFechaSalida = new JTextField();
		txtFechaSalida.setBounds(260, 145, 96, 18);
		contentPane.add(txtFechaSalida);
		txtFechaSalida.setColumns(10);
		
		txtCliente = new JTextField();
		txtCliente.setBounds(260, 183, 96, 18);
		contentPane.add(txtCliente);
		txtCliente.setColumns(10);
		
		txtHabitacion = new JTextField();
		txtHabitacion.setBounds(260, 219, 96, 18);
		contentPane.add(txtHabitacion);
		txtHabitacion.setColumns(10);
		contentPane.setBackground(new java.awt.Color(240, 248, 255)); // azul clarito
		
		JLabel lblNewLabel_5 = new JLabel("REGISTRO DE RESERVA");
        lblNewLabel_5.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNewLabel_5.setForeground(new java.awt.Color(0, 51, 102));
		lblNewLabel_5.setBounds(199, 10, 205, 49);
		contentPane.add(lblNewLabel_5);
		
		btnRegistrar = new JButton("Registrar");

		// 👇 ESTILO
		btnRegistrar.setBackground(new java.awt.Color(0, 123, 255));
		btnRegistrar.setForeground(java.awt.Color.WHITE);
		btnRegistrar.setFocusPainted(false);
        //
		btnRegistrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					if (txtCliente.getText().isEmpty()) {
					    JOptionPane.showMessageDialog(null, "Ingrese cliente");
					    return;
					}
					
					if (txtIngreso.getText().isEmpty()) {
					    JOptionPane.showMessageDialog(null, "Ingrese fecha ingreso");
					    return;
					}
					
					if (txtFechaSalida.getText().isEmpty()) {
					    JOptionPane.showMessageDialog(null, "Ingrese fecha salida");
					    return;
					}
					
					if (txtHabitacion.getText().isEmpty()) {
					    JOptionPane.showMessageDialog(null, "Ingrese habitación");
					    return;
					}
					
					// NUEVAS VALIDACIONES
					if (!txtCliente.getText().matches("[a-zA-Z ]+")) {
					    JOptionPane.showMessageDialog(null, "El nombre solo debe contener letras");
					    return;
					}

					if (!txtHabitacion.getText().matches("\\d+")) {
					    JOptionPane.showMessageDialog(null, "La habitación debe contener solo números");
					    return;
					}

					if (!txtIngreso.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
					    JOptionPane.showMessageDialog(null, "Formato fecha ingreso: dd/MM/yyyy");
					    return;
					}

					if (!txtFechaSalida.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
					    JOptionPane.showMessageDialog(null, "Formato fecha salida: dd/MM/yyyy");
					    return;
					}
					
					//comparación de fechas
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					sdf.setLenient(false);

					Date fechaIngreso = sdf.parse(txtIngreso.getText());
					Date fechaSalida = sdf.parse(txtFechaSalida.getText());

					if (fechaSalida.before(fechaIngreso)) {
					    JOptionPane.showMessageDialog(null,
					            "La fecha de salida debe ser posterior a la fecha de ingreso");
					    return;
					}
					
					
					
				    //int id = Integer.parseInt(txtId.getText());
                    //contador de reservas automaticas
                    int id = contador++;
                    String idVisual = String.format("RES-%03d", id);
				    String ingreso = txtIngreso.getText();
				    String salida = txtFechaSalida.getText();
				    String nombreCliente = txtCliente.getText();
				    int numHabitacion = Integer.parseInt(txtHabitacion.getText());

				    Cliente cliente = new Cliente(nombreCliente, "correo@test.com", "123", "00000000");
				    Habitacion habitacion = new Habitacion(numHabitacion, "Simple", 70);

				    Reserva reserva = new Reserva(id, cliente, ingreso, salida);
				    reserva.asignarHabitacion(habitacion);
                    //Lista de reservas

                     listaReservas.add(reserva);

                     modelo.addRow(new Object[]{
                    		 idVisual,
                     nombreCliente,
                     ingreso,
                     salida,
                      numHabitacion
                          });

				    JOptionPane.showMessageDialog(null, "Reserva registrada correctamente");

				} catch (Exception ex) {
				    JOptionPane.showMessageDialog(null, "Error en los datos");
				}

				
				
				
				
				
			}
		});
		btnRegistrar.setBounds(56, 294, 96, 20);
		contentPane.add(btnRegistrar);
		
		btnLimpiar = new JButton("Limpiar");
		//colores de botones
		btnLimpiar.setBackground(new java.awt.Color(108, 117, 125));
		btnLimpiar.setForeground(java.awt.Color.WHITE);
		btnLimpiar.setFocusPainted(false);
        //
		btnLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				txtId.setText("");
				txtIngreso.setText("");
				txtFechaSalida.setText("");
				txtCliente.setText("");
				txtHabitacion.setText("");
				txtId.setText(String.format("RES-%03d", contador));
				
				
			}
		});
		btnLimpiar.setBounds(213, 294, 84, 20);
		contentPane.add(btnLimpiar);
		
		
		table = new JTable();
		//table.setBounds(366, 145, 260, 184);
		//contentPane.add(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(366, 145, 260, 184);
        contentPane.add(scroll);

        //jtable

         modelo = new DefaultTableModel();

         modelo.addColumn("ID");
         modelo.addColumn("Cliente");
         modelo.addColumn("Ingreso");
         modelo.addColumn("Salida");
         modelo.addColumn("Habitación");

         table.setModel(modelo);
         //tamaño para mostrar a los clientes
         table.setRowHeight(28);
         table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
         table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

         table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         
         btnEliminar = new JButton("Eliminar");
         //colores
         btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
         btnEliminar.setForeground(java.awt.Color.WHITE);
         btnEliminar.setFocusPainted(false);

         btnEliminar.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		
         		int fila = table.getSelectedRow();

         		if (fila >= 0) {

         			int confirm = JOptionPane.showConfirmDialog(
         			        null,
         			        "¿Eliminar reserva?");

         			if (confirm == JOptionPane.YES_OPTION) {

         			    modelo.removeRow(fila);
         			    listaReservas.remove(fila);

         			    JOptionPane.showMessageDialog(null, "Reserva eliminada");
         			}

         		} else {
         		    JOptionPane.showMessageDialog(null, "Seleccione una fila");
         		}	
         		
         	}
         });
         btnEliminar.setBounds(56, 344, 96, 19);
         contentPane.add(btnEliminar);
         
         btnModificar = new JButton("Modificar");
         //colores de botones
         btnModificar.setBackground(new java.awt.Color(40, 167, 69));
         btnModificar.setForeground(java.awt.Color.WHITE);
         btnModificar.setFocusPainted(false);
         //
         btnModificar.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		 int fila = table.getSelectedRow();

                 if (fila >= 0) {
                	// validaciones
                	 
                	 if (!txtCliente.getText().matches("[a-zA-Z ]+")) {
                		    JOptionPane.showMessageDialog(null, "El nombre solo debe contener letras");
                		    return;
                		}

                		if (!txtHabitacion.getText().matches("\\d+")) {
                		    JOptionPane.showMessageDialog(null, "La habitación debe contener solo números");
                		    return;
                		}

                		if (!txtIngreso.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
                		    JOptionPane.showMessageDialog(null, "Formato fecha ingreso: dd/MM/yyyy");
                		    return;
                		}

                		if (!txtFechaSalida.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
                		    JOptionPane.showMessageDialog(null, "Formato fecha salida: dd/MM/yyyy");
                		    return;
                		}
                		//comparación de fechas
                		try {

                		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                		    sdf.setLenient(false);

                		    Date fechaIngreso = sdf.parse(txtIngreso.getText());
                		    Date fechaSalida = sdf.parse(txtFechaSalida.getText());

                		    if (fechaSalida.before(fechaIngreso)) {
                		        JOptionPane.showMessageDialog(
                		                null,
                		                "La fecha de salida debe ser posterior a la fecha de ingreso");
                		        return;
                		    }

                		} catch (Exception ex) {
                		    JOptionPane.showMessageDialog(null, "Error en el formato de fecha");
                		    return;
                		}
                		
                		
                		//
                     modelo.setValueAt(txtCliente.getText(), fila, 1);
                     modelo.setValueAt(txtIngreso.getText(), fila, 2);
                     modelo.setValueAt(txtFechaSalida.getText(), fila, 3);
                     modelo.setValueAt(txtHabitacion.getText(), fila, 4);

                     JOptionPane.showMessageDialog(null, "Reserva actualizada");

                 } else {

                     JOptionPane.showMessageDialog(null, "Seleccione una reserva");
                 }
         		
         		
         	}
         });
         btnModificar.setBounds(213, 343, 96, 20);
         contentPane.add(btnModificar);

         table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
         table.getColumnModel().getColumn(1).setPreferredWidth(120); // Cliente
         table.getColumnModel().getColumn(2).setPreferredWidth(100); // Ingreso
         table.getColumnModel().getColumn(3).setPreferredWidth(100); // Salida
         table.getColumnModel().getColumn(4).setPreferredWidth(100); // Habitación
            //Cargar datos en el formulario
         
         table.addMouseListener(new java.awt.event.MouseAdapter() {
        	    public void mouseClicked(java.awt.event.MouseEvent e) {

        	        int fila = table.getSelectedRow();

        	        if (fila >= 0) {
        	        	txtId.setText(modelo.getValueAt(fila, 0).toString());
        	            txtCliente.setText(modelo.getValueAt(fila, 1).toString());
        	            txtIngreso.setText(modelo.getValueAt(fila, 2).toString());
        	            txtFechaSalida.setText(modelo.getValueAt(fila, 3).toString());
        	            txtHabitacion.setText(modelo.getValueAt(fila, 4).toString());
        	        }
        	    }
        	});


	}
}

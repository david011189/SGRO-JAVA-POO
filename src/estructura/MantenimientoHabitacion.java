package estructura;

import java.sql.*;
import java.util.ArrayList;

public class MantenimientoHabitacion {

    public String registrarHabitacion(Habitacion h) {
        String sql = "INSERT INTO habitaciones (numero, tipo, capacidad, descripcion, tarifa) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getNumero());
            ps.setString(2, h.getTipo());
            ps.setInt(3, h.getCapacidad());
            ps.setString(4, h.getDescripcion());
            ps.setDouble(5, h.getPrecio());
            ps.executeUpdate();
            return null;
        } catch (SQLIntegrityConstraintViolationException e) {
            return "El número de habitación ya está registrado.";
        } catch (Exception e) {
            return "Error al registrar: " + e.getMessage();
        }
    }

    public String modificarHabitacion(int numeroOriginal, int nuevoNumero, String tipo,
                                       int capacidad, String descripcion, double tarifa, String estado) {
        String sql = "UPDATE habitaciones SET numero=?, tipo=?, capacidad=?, descripcion=?, tarifa=?, estado=? WHERE numero=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoNumero);
            ps.setString(2, tipo);
            ps.setInt(3, capacidad);
            ps.setString(4, descripcion);
            ps.setDouble(5, tarifa);
            ps.setString(6, estado);
            ps.setInt(7, numeroOriginal);
            int filas = ps.executeUpdate();
            if (filas == 0) return "Habitación no encontrada.";
            return null;
        } catch (SQLIntegrityConstraintViolationException e) {
            return "El número de habitación ya está en uso.";
        } catch (Exception e) {
            return "Error al modificar: " + e.getMessage();
        }
    }

    public boolean eliminarHabitacion(int numero) {
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM habitaciones WHERE numero=?")) {
            ps.setInt(1, numero);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Habitacion> getListaHabitaciones() throws Exception {
        ArrayList<Habitacion> lista = new ArrayList<>();
        Connection con = ConexionBD.conectar();
        if (con == null) throw new Exception("No se pudo conectar a la base de datos.");
        String sql = "SELECT numero, tipo, capacidad, descripcion, estado, tarifa FROM habitaciones ORDER BY numero";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Habitacion h = new Habitacion(
                        rs.getInt("numero"),
                        rs.getString("tipo"),
                        rs.getDouble("tarifa"));
                h.setCapacidad(rs.getInt("capacidad"));
                h.setDescripcion(rs.getString("descripcion"));
                h.setEstado(rs.getString("estado"));
                h.setDisponible(rs.getString("estado").equals("disponible"));
                lista.add(h);
            }
        } finally {
            con.close();
        }
        return lista;
    }
}

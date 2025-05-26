package servlet;

import dao.ClientesJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Clientes;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.json.*;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/clientes")
public class ClientesServlet extends HttpServlet {

    private ClientesJpaController clienteDAO;

    @Override
    public void init() throws ServletException {
        clienteDAO = new ClientesJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Clientes> clientes = clienteDAO.findClientesEntities();

        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (Clientes c : clientes) {
            jsonArray.add(Json.createObjectBuilder()
                    .add("idCliente", c.getIdCliente())
                    .add("nombre", c.getNombre())
                    .add("apellido", c.getApellido())
                    .add("correo", c.getCorreo())
                    .add("telefono", c.getTelefono() != null ? c.getTelefono() : "")
                    .add("direccion", c.getDireccion() != null ? c.getDireccion() : "")
            );
        }

        JsonWriter writer = Json.createWriter(response.getOutputStream());
        writer.writeArray(jsonArray.build());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        Clientes cliente = new Clientes();
        cliente.setNombre(json.getString("nombre"));
        cliente.setApellido(json.getString("apellido"));
        cliente.setCorreo(json.getString("correo"));
        cliente.setTelefono(json.getString("telefono", ""));
        cliente.setDireccion(json.getString("direccion", ""));

        clienteDAO.create(cliente);

        response.setContentType("application/json;charset=UTF-8");
        Json.createWriter(response.getOutputStream()).writeObject(Json.createObjectBuilder()
                .add("message", "Cliente creado correctamente").build());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        int id = json.getInt("idCliente");
        Clientes cliente = clienteDAO.findClientes(id);
        if (cliente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        cliente.setNombre(json.getString("nombre"));
        cliente.setApellido(json.getString("apellido"));
        cliente.setCorreo(json.getString("correo"));
        cliente.setTelefono(json.getString("telefono", ""));
        cliente.setDireccion(json.getString("direccion", ""));

        try {
            clienteDAO.edit(cliente);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.setContentType("application/json;charset=UTF-8");
        Json.createWriter(response.getOutputStream()).writeObject(Json.createObjectBuilder()
                .add("message", "Cliente actualizado").build());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        int id = json.getInt("idCliente");
        try {
            clienteDAO.destroy(id);
        } catch (NonexistentEntityException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        Json.createWriter(response.getOutputStream()).writeObject(Json.createObjectBuilder()
                .add("message", "Cliente eliminado").build());
    }
}

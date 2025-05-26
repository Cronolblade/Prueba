package servlet;

import dao.VentasJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Ventas;
import dto.Clientes;

import javax.json.*;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "VentasServlet", urlPatterns = {"/ventas"})
public class VentasServlet extends HttpServlet {

    private VentasJpaController ventasDao;

    @Override
    public void init() throws ServletException {
        ventasDao = new VentasJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        List<Ventas> ventasList = ventasDao.findVentasEntities();

        try (JsonWriter writer = Json.createWriter(response.getWriter())) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Ventas venta : ventasList) {
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("idVenta", venta.getIdVenta())
                        .add("fechaVenta", venta.getFechaVenta() != null
                                ? new SimpleDateFormat("dd/MM/yyyy").format(venta.getFechaVenta()) : "")
                        .add("total", venta.getTotal().toString())
                        .add("idCliente", venta.getIdCliente() != null ? venta.getIdCliente().getIdCliente() : 0)
                );
            }
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        Ventas venta = new Ventas();

        // Parsear fecha desde yyyy-MM-dd
        try {
            String fechaStr = json.getString("fechaVenta");
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            venta.setFechaVenta(formato.parse(fechaStr));
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha incorrecto");
            return;
        }

        venta.setTotal(new BigDecimal(json.getString("total")));
        if (json.containsKey("idCliente")) {
            Clientes cliente = new Clientes();
            cliente.setIdCliente(json.getInt("idCliente"));
            venta.setIdCliente(cliente);
        }

        ventasDao.create(venta);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Venta creada\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        Ventas venta = ventasDao.findVentas(json.getInt("idVenta"));
        if (venta == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            String fechaStr = json.getString("fechaVenta");
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            venta.setFechaVenta(formato.parse(fechaStr));
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha incorrecto");
            return;
        }

        venta.setTotal(new BigDecimal(json.getString("total")));
        if (json.containsKey("idCliente")) {
            Clientes cliente = new Clientes();
            cliente.setIdCliente(json.getInt("idCliente"));
            venta.setIdCliente(cliente);
        }

        try {
            ventasDao.edit(venta);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Venta actualizada\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        int id = json.getInt("idVenta");

        try {
            ventasDao.destroy(id);
        } catch (NonexistentEntityException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Venta eliminada\"}");
    }
}

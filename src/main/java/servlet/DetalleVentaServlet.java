/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dao.DetalleVentaJpaController;
import dto.DetalleVenta;
import dto.Libros;
import dto.Ventas;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author FERNANDO
 */
@WebServlet("/detalleventa")
public class DetalleVentaServlet extends HttpServlet {

    private DetalleVentaJpaController detalleDao;
    
    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        detalleDao = new DetalleVentaJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        List<DetalleVenta> lista = detalleDao.findDetalleVentaEntities();

        try (JsonWriter writer = Json.createWriter(response.getWriter())) {
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (DetalleVenta d : lista) {
                array.add(Json.createObjectBuilder()
                        .add("idDetalle", d.getIdDetalle())
                        .add("cantidad", d.getCantidad())
                        .add("precioUnitario", d.getPrecioUnitario().toString())
                        .add("idLibro", d.getIdLibro() != null ? d.getIdLibro().getIdLibro() : 0)
                        .add("idVenta", d.getIdVenta() != null ? d.getIdVenta().getIdVenta() : 0)
                );
            }
            writer.writeArray(array.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();

        DetalleVenta d = new DetalleVenta();
        d.setCantidad(json.getInt("cantidad"));
        d.setPrecioUnitario(new BigDecimal(json.getString("precioUnitario")));

        if (json.containsKey("idLibro")) {
            Libros libro = new Libros();
            libro.setIdLibro(json.getInt("idLibro"));
            d.setIdLibro(libro);
        }
        if (json.containsKey("idVenta")) {
            Ventas venta = new Ventas();
            venta.setIdVenta(json.getInt("idVenta"));
            d.setIdVenta(venta);
        }

        detalleDao.create(d);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"DetalleVenta creado\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        DetalleVenta d = detalleDao.findDetalleVenta(json.getInt("idDetalle"));
        if (d == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        d.setCantidad(json.getInt("cantidad"));
        d.setPrecioUnitario(new BigDecimal(json.getString("precioUnitario")));

        if (json.containsKey("idLibro")) {
            Libros libro = new Libros();
            libro.setIdLibro(json.getInt("idLibro"));
            d.setIdLibro(libro);
        }
        if (json.containsKey("idVenta")) {
            Ventas venta = new Ventas();
            venta.setIdVenta(json.getInt("idVenta"));
            d.setIdVenta(venta);
        }

        try {
            detalleDao.edit(d);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"DetalleVenta actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        int idDetalle = json.getInt("idDetalle");
        try {
            detalleDao.destroy(idDetalle);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"DetalleVenta eliminado\"}");
    }
}

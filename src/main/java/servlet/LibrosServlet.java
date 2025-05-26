/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dao.LibrosJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Libros;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LibrosServlet")
public class LibrosServlet extends HttpServlet {
    private LibrosJpaController dao;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU");
        dao = new LibrosJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();

        List<Libros> libros = dao.findLibrosEntities();
        for (Libros l : libros) {
    JsonObjectBuilder job = Json.createObjectBuilder()
        .add("idLibro", l.getIdLibro())
        .add("titulo", l.getTitulo())
        .add("isbn", l.getIsbn() == null ? "" : l.getIsbn())
        .add("precio", l.getPrecio().toString());

    if (l.getAnioPublicacion() != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(l.getAnioPublicacion());
        job.add("anioPublicacion", year);
    } else {
        job.add("anioPublicacion", "");
    }

    jsonArray.add(job);
}

        response.getWriter().print(jsonArray.build().toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = Json.createReader(request.getReader()).readObject();

        Libros libro = new Libros();
        libro.setTitulo(json.getString("titulo"));
        libro.setIsbn(json.getString("isbn", ""));
        libro.setAnioPublicacion(json.isNull("anioPublicacion") || json.getString("anioPublicacion").isEmpty() ? null : java.sql.Date.valueOf(json.getString("anioPublicacion")));
        libro.setPrecio(new BigDecimal(json.getString("precio")));

        dao.create(libro);

        response.setContentType("application/json;charset=UTF-8");
        JsonObject resp = Json.createObjectBuilder()
                .add("message", "Libro creado correctamente")
                .build();
        response.getWriter().print(resp.toString());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = Json.createReader(request.getReader()).readObject();

        Libros libro = dao.findLibros(json.getInt("idLibro"));
        if (libro != null) {
            libro.setTitulo(json.getString("titulo"));
            libro.setIsbn(json.getString("isbn", ""));
            libro.setAnioPublicacion(json.isNull("anioPublicacion") || json.getString("anioPublicacion").isEmpty() ? null : java.sql.Date.valueOf(json.getString("anioPublicacion")));
            libro.setPrecio(new BigDecimal(json.getString("precio")));
            try {
                dao.edit(libro);
                response.setContentType("application/json;charset=UTF-8");
                JsonObject resp = Json.createObjectBuilder()
                        .add("message", "Libro actualizado correctamente")
                        .build();
                response.getWriter().print(resp.toString());
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar libro");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro no encontrado");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = Json.createReader(request.getReader()).readObject();
        int idLibro = json.getInt("idLibro");
        try {
            dao.destroy(idLibro);
            response.setContentType("application/json;charset=UTF-8");
            JsonObject resp = Json.createObjectBuilder()
                    .add("message", "Libro eliminado correctamente")
                    .build();
            response.getWriter().print(resp.toString());
        } catch (NonexistentEntityException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro no encontrado");
        }
    }
}

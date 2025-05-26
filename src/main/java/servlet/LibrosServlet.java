package servlet;

import dao.LibrosJpaController;
import dto.Libros;

import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/libros")
public class LibrosServlet extends HttpServlet {

    private LibrosJpaController dao;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU");
        dao = new LibrosJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        try (JsonWriter writer = Json.createWriter(res.getWriter())) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Libros libro : dao.findLibrosEntities()) {
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("idLibro", libro.getIdLibro())
                        .add("titulo", libro.getTitulo())
                        .add("isbn", libro.getIsbn() == null ? "" : libro.getIsbn())
                        .add("anioPublicacion", libro.getAnioPublicacion() == null ? ""
                                : new SimpleDateFormat("yyyy-MM-dd").format(libro.getAnioPublicacion()))
                        .add("precio", libro.getPrecio().toString())
                );
            }
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject json = reader.readObject();

            Libros libro = new Libros();
            libro.setTitulo(json.getString("titulo", ""));
            libro.setIsbn(json.getString("isbn", ""));
            libro.setPrecio(new BigDecimal(json.getString("precio", "0")));

            if (json.containsKey("anioPublicacion") && !json.getString("anioPublicacion").isEmpty()) {
                libro.setAnioPublicacion(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("anioPublicacion")));
            }

            dao.create(libro);

            res.getWriter().write(Json.createObjectBuilder()
                    .add("status", "created")
                    .build().toString());
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject json = reader.readObject();

            Libros libro = dao.findLibros(json.getInt("idLibro"));
            if (libro != null) {
                libro.setTitulo(json.getString("titulo", ""));
                libro.setIsbn(json.getString("isbn", ""));
                libro.setPrecio(new BigDecimal(json.getString("precio", "0")));

                String fechaStr = json.getString("anioPublicacion", "").trim();
                if (!fechaStr.isEmpty()) {
                    try {
                        libro.setAnioPublicacion(new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr));
                    } catch (Exception ex) {
                        res.setStatus(400);
                        res.getWriter().write(Json.createObjectBuilder()
                                .add("error", "Formato de fecha inválido. Use yyyy-MM-dd.")
                                .build().toString());
                        return;
                    }
                } else {
                    libro.setAnioPublicacion(null); // Si viene vacío, lo borra
                }

                dao.edit(libro);

                res.getWriter().write(Json.createObjectBuilder()
                        .add("status", "updated")
                        .build().toString());
            } else {
                res.setStatus(404);
                res.getWriter().write(Json.createObjectBuilder().add("error", "Libro no encontrado").build().toString());
            }
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject json = reader.readObject();
            int idLibro = json.getInt("idLibro");

            dao.destroy(idLibro);

            res.getWriter().write(Json.createObjectBuilder()
                    .add("status", "deleted")
                    .build().toString());
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
        }
    }
}

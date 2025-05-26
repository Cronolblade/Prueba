package servlets;

import dao.AutoresJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Autores;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.json.*;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "AutoresServlet", urlPatterns = {"/AutoresServlet"})
public class AutoresServlet extends HttpServlet {

    private AutoresJpaController autoresDAO;

    @Override
    public void init() throws ServletException {
        autoresDAO = new AutoresJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Autores> autoresList = autoresDAO.findAutoresEntities();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Autores autor : autoresList) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("idAutor", autor.getIdAutor())
                    .add("nombre", autor.getNombre())
                    .add("apellido", autor.getApellido())
                    .add("paisOrigen", autor.getPaisOrigen() != null ? autor.getPaisOrigen() : "")
                    .add("fechaNacimiento", autor.getFechaNacimiento() != null ? sdf.format(autor.getFechaNacimiento()) : "")
            );
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(arrayBuilder.build().toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        Autores autor = new Autores();
        autor.setNombre(json.getString("nombre"));
        autor.setApellido(json.getString("apellido"));
        autor.setPaisOrigen(json.getString("paisOrigen", null));

        try {
            String fecha = json.getString("fechaNacimiento", null);
            if (fecha != null && !fecha.isEmpty()) {
                Date fechaNacimiento = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
                autor.setFechaNacimiento(fechaNacimiento);
            }
            autoresDAO.create(autor);

            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("status", "success").build().toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        try {
            Integer id = json.getInt("idAutor");
            Autores autor = autoresDAO.findAutores(id);
            if (autor == null) {
                response.setStatus(404);
                try (PrintWriter out = response.getWriter()) {
                    out.print(Json.createObjectBuilder().add("error", "Autor no encontrado").build().toString());
                }
                return;
            }

            autor.setNombre(json.getString("nombre"));
            autor.setApellido(json.getString("apellido"));
            autor.setPaisOrigen(json.getString("paisOrigen", null));

            String fecha = json.getString("fechaNacimiento", null);
            if (fecha != null && !fecha.isEmpty()) {
                Date fechaNacimiento = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
                autor.setFechaNacimiento(fechaNacimiento);
            } else {
                autor.setFechaNacimiento(null);
            }

            autoresDAO.edit(autor);

            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("status", "updated").build().toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        try {
            Integer id = json.getInt("idAutor");
            autoresDAO.destroy(id);
            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("status", "deleted").build().toString());
            }
        } catch (NonexistentEntityException e) {
            response.setStatus(404);
            try (PrintWriter out = response.getWriter()) {
                out.print(Json.createObjectBuilder().add("error", "Autor no encontrado").build().toString());
            }
        }
    }
}

package servlet;

import dao.CategoriasJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Categorias;

import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "CategoriasServlet", urlPatterns = {"/categorias"})
public class CategoriasServlet extends HttpServlet {

    private EntityManagerFactory emf;
    private CategoriasJpaController categoriasJpa;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_war_1.0-SNAPSHOTPU"); // Ajusta el nombre
        categoriasJpa = new CategoriasJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Categorias> lista = categoriasJpa.findCategoriasEntities();

        try (JsonWriter writer = Json.createWriter(resp.getOutputStream())) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Categorias c : lista) {
                JsonObjectBuilder obj = Json.createObjectBuilder()
                        .add("idCategoria", c.getIdCategoria())
                        .add("nombreCategoria", c.getNombreCategoria())
                        .add("descripcion", c.getDescripcion() == null ? "" : c.getDescripcion());
                arrayBuilder.add(obj);
            }
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json;charset=UTF-8");

        JsonObject jsonRequest;
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            jsonRequest = reader.readObject();
        }

        String nombreCategoria = jsonRequest.getString("nombreCategoria", "").trim();
        String descripcion = jsonRequest.getString("descripcion", "").trim();

        if (nombreCategoria.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "El nombre de la categoría es obligatorio.");
            return;
        }

        Categorias cat = new Categorias();
        cat.setNombreCategoria(nombreCategoria);
        cat.setDescripcion(descripcion);

        try {
            categoriasJpa.create(cat);
            resp.setStatus(HttpServletResponse.SC_CREATED);

            try (JsonWriter writer = Json.createWriter(resp.getOutputStream())) {
                JsonObject obj = Json.createObjectBuilder()
                        .add("idCategoria", cat.getIdCategoria())
                        .add("nombreCategoria", cat.getNombreCategoria())
                        .add("descripcion", cat.getDescripcion() == null ? "" : cat.getDescripcion())
                        .build();
                writer.writeObject(obj);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(resp, "Error al crear la categoría: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json;charset=UTF-8");

        JsonObject jsonRequest;
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            jsonRequest = reader.readObject();
        }

        int idCategoria = jsonRequest.getInt("idCategoria", -1);
        String nombreCategoria = jsonRequest.getString("nombreCategoria", "").trim();
        String descripcion = jsonRequest.getString("descripcion", "").trim();

        if (idCategoria == -1 || nombreCategoria.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "El id y el nombre de la categoría son obligatorios.");
            return;
        }

        Categorias cat = categoriasJpa.findCategorias(idCategoria);
        if (cat == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, "Categoría no encontrada.");
            return;
        }

        cat.setNombreCategoria(nombreCategoria);
        cat.setDescripcion(descripcion);

        try {
            categoriasJpa.edit(cat);
            resp.setStatus(HttpServletResponse.SC_OK);

            try (JsonWriter writer = Json.createWriter(resp.getOutputStream())) {
                JsonObject obj = Json.createObjectBuilder()
                        .add("idCategoria", cat.getIdCategoria())
                        .add("nombreCategoria", cat.getNombreCategoria())
                        .add("descripcion", cat.getDescripcion() == null ? "" : cat.getDescripcion())
                        .build();
                writer.writeObject(obj);
            }
        } catch (NonexistentEntityException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, "La categoría no existe.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(resp, "Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        // Para DELETE recibimos el id desde el query param "id"
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "Falta el parámetro id.");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            categoriasJpa.destroy(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "ID inválido.");
        } catch (NonexistentEntityException ex) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, "La categoría no existe.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(resp, "Error al eliminar la categoría: " + e.getMessage());
        }
    }

    private void writeError(HttpServletResponse resp, String message) throws IOException {
        try (JsonWriter writer = Json.createWriter(resp.getOutputStream())) {
            JsonObject obj = Json.createObjectBuilder()
                    .add("error", message)
                    .build();
            writer.writeObject(obj);
        }
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}

package servlet;

import com.google.gson.Gson;
import dto.Categorias;
import dao.CategoriasJpaController;
import dao.exceptions.NonexistentEntityException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoriasServlet", urlPatterns = {"/categorias"})
public class CategoriasServlet extends HttpServlet {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("TuUnidadDePersistencia");
    private CategoriasJpaController controller = new CategoriasJpaController(emf);
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Categorias> lista = controller.findCategoriasEntities();
        String categoriasJson = gson.toJson(lista);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(categoriasJson);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Categorias nueva = gson.fromJson(reader, Categorias.class);
        controller.create(nueva);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            Categorias editada = gson.fromJson(reader, Categorias.class);
            controller.edit(editada);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NonexistentEntityException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            Categorias cat = gson.fromJson(reader, Categorias.class);
            controller.destroy(cat.getIdCategoria());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NonexistentEntityException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }
}

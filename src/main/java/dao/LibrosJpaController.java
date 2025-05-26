/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Autores;
import dto.Categorias;
import dto.DetalleVenta;
import dto.Libros;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class LibrosJpaController implements Serializable {

    public LibrosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libros libros) {
        if (libros.getDetalleVentaCollection() == null) {
            libros.setDetalleVentaCollection(new ArrayList<DetalleVenta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autores idAutor = libros.getIdAutor();
            if (idAutor != null) {
                idAutor = em.getReference(idAutor.getClass(), idAutor.getIdAutor());
                libros.setIdAutor(idAutor);
            }
            Categorias idCategoria = libros.getIdCategoria();
            if (idCategoria != null) {
                idCategoria = em.getReference(idCategoria.getClass(), idCategoria.getIdCategoria());
                libros.setIdCategoria(idCategoria);
            }
            Collection<DetalleVenta> attachedDetalleVentaCollection = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaCollectionDetalleVentaToAttach : libros.getDetalleVentaCollection()) {
                detalleVentaCollectionDetalleVentaToAttach = em.getReference(detalleVentaCollectionDetalleVentaToAttach.getClass(), detalleVentaCollectionDetalleVentaToAttach.getIdDetalle());
                attachedDetalleVentaCollection.add(detalleVentaCollectionDetalleVentaToAttach);
            }
            libros.setDetalleVentaCollection(attachedDetalleVentaCollection);
            em.persist(libros);
            if (idAutor != null) {
                idAutor.getLibrosCollection().add(libros);
                idAutor = em.merge(idAutor);
            }
            if (idCategoria != null) {
                idCategoria.getLibrosCollection().add(libros);
                idCategoria = em.merge(idCategoria);
            }
            for (DetalleVenta detalleVentaCollectionDetalleVenta : libros.getDetalleVentaCollection()) {
                Libros oldIdLibroOfDetalleVentaCollectionDetalleVenta = detalleVentaCollectionDetalleVenta.getIdLibro();
                detalleVentaCollectionDetalleVenta.setIdLibro(libros);
                detalleVentaCollectionDetalleVenta = em.merge(detalleVentaCollectionDetalleVenta);
                if (oldIdLibroOfDetalleVentaCollectionDetalleVenta != null) {
                    oldIdLibroOfDetalleVentaCollectionDetalleVenta.getDetalleVentaCollection().remove(detalleVentaCollectionDetalleVenta);
                    oldIdLibroOfDetalleVentaCollectionDetalleVenta = em.merge(oldIdLibroOfDetalleVentaCollectionDetalleVenta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Libros libros) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libros persistentLibros = em.find(Libros.class, libros.getIdLibro());
            Autores idAutorOld = persistentLibros.getIdAutor();
            Autores idAutorNew = libros.getIdAutor();
            Categorias idCategoriaOld = persistentLibros.getIdCategoria();
            Categorias idCategoriaNew = libros.getIdCategoria();
            Collection<DetalleVenta> detalleVentaCollectionOld = persistentLibros.getDetalleVentaCollection();
            Collection<DetalleVenta> detalleVentaCollectionNew = libros.getDetalleVentaCollection();
            if (idAutorNew != null) {
                idAutorNew = em.getReference(idAutorNew.getClass(), idAutorNew.getIdAutor());
                libros.setIdAutor(idAutorNew);
            }
            if (idCategoriaNew != null) {
                idCategoriaNew = em.getReference(idCategoriaNew.getClass(), idCategoriaNew.getIdCategoria());
                libros.setIdCategoria(idCategoriaNew);
            }
            Collection<DetalleVenta> attachedDetalleVentaCollectionNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaCollectionNewDetalleVentaToAttach : detalleVentaCollectionNew) {
                detalleVentaCollectionNewDetalleVentaToAttach = em.getReference(detalleVentaCollectionNewDetalleVentaToAttach.getClass(), detalleVentaCollectionNewDetalleVentaToAttach.getIdDetalle());
                attachedDetalleVentaCollectionNew.add(detalleVentaCollectionNewDetalleVentaToAttach);
            }
            detalleVentaCollectionNew = attachedDetalleVentaCollectionNew;
            libros.setDetalleVentaCollection(detalleVentaCollectionNew);
            libros = em.merge(libros);
            if (idAutorOld != null && !idAutorOld.equals(idAutorNew)) {
                idAutorOld.getLibrosCollection().remove(libros);
                idAutorOld = em.merge(idAutorOld);
            }
            if (idAutorNew != null && !idAutorNew.equals(idAutorOld)) {
                idAutorNew.getLibrosCollection().add(libros);
                idAutorNew = em.merge(idAutorNew);
            }
            if (idCategoriaOld != null && !idCategoriaOld.equals(idCategoriaNew)) {
                idCategoriaOld.getLibrosCollection().remove(libros);
                idCategoriaOld = em.merge(idCategoriaOld);
            }
            if (idCategoriaNew != null && !idCategoriaNew.equals(idCategoriaOld)) {
                idCategoriaNew.getLibrosCollection().add(libros);
                idCategoriaNew = em.merge(idCategoriaNew);
            }
            for (DetalleVenta detalleVentaCollectionOldDetalleVenta : detalleVentaCollectionOld) {
                if (!detalleVentaCollectionNew.contains(detalleVentaCollectionOldDetalleVenta)) {
                    detalleVentaCollectionOldDetalleVenta.setIdLibro(null);
                    detalleVentaCollectionOldDetalleVenta = em.merge(detalleVentaCollectionOldDetalleVenta);
                }
            }
            for (DetalleVenta detalleVentaCollectionNewDetalleVenta : detalleVentaCollectionNew) {
                if (!detalleVentaCollectionOld.contains(detalleVentaCollectionNewDetalleVenta)) {
                    Libros oldIdLibroOfDetalleVentaCollectionNewDetalleVenta = detalleVentaCollectionNewDetalleVenta.getIdLibro();
                    detalleVentaCollectionNewDetalleVenta.setIdLibro(libros);
                    detalleVentaCollectionNewDetalleVenta = em.merge(detalleVentaCollectionNewDetalleVenta);
                    if (oldIdLibroOfDetalleVentaCollectionNewDetalleVenta != null && !oldIdLibroOfDetalleVentaCollectionNewDetalleVenta.equals(libros)) {
                        oldIdLibroOfDetalleVentaCollectionNewDetalleVenta.getDetalleVentaCollection().remove(detalleVentaCollectionNewDetalleVenta);
                        oldIdLibroOfDetalleVentaCollectionNewDetalleVenta = em.merge(oldIdLibroOfDetalleVentaCollectionNewDetalleVenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = libros.getIdLibro();
                if (findLibros(id) == null) {
                    throw new NonexistentEntityException("The libros with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libros libros;
            try {
                libros = em.getReference(Libros.class, id);
                libros.getIdLibro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libros with id " + id + " no longer exists.", enfe);
            }
            Autores idAutor = libros.getIdAutor();
            if (idAutor != null) {
                idAutor.getLibrosCollection().remove(libros);
                idAutor = em.merge(idAutor);
            }
            Categorias idCategoria = libros.getIdCategoria();
            if (idCategoria != null) {
                idCategoria.getLibrosCollection().remove(libros);
                idCategoria = em.merge(idCategoria);
            }
            Collection<DetalleVenta> detalleVentaCollection = libros.getDetalleVentaCollection();
            for (DetalleVenta detalleVentaCollectionDetalleVenta : detalleVentaCollection) {
                detalleVentaCollectionDetalleVenta.setIdLibro(null);
                detalleVentaCollectionDetalleVenta = em.merge(detalleVentaCollectionDetalleVenta);
            }
            em.remove(libros);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libros> findLibrosEntities() {
        return findLibrosEntities(true, -1, -1);
    }

    public List<Libros> findLibrosEntities(int maxResults, int firstResult) {
        return findLibrosEntities(false, maxResults, firstResult);
    }

    private List<Libros> findLibrosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libros.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Libros findLibros(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libros.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibrosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libros> rt = cq.from(Libros.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

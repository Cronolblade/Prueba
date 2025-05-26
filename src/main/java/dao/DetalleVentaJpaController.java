/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dto.DetalleVenta;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Libros;
import dto.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class DetalleVentaJpaController implements Serializable {

    public DetalleVentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleVenta detalleVenta) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libros idLibro = detalleVenta.getIdLibro();
            if (idLibro != null) {
                idLibro = em.getReference(idLibro.getClass(), idLibro.getIdLibro());
                detalleVenta.setIdLibro(idLibro);
            }
            Ventas idVenta = detalleVenta.getIdVenta();
            if (idVenta != null) {
                idVenta = em.getReference(idVenta.getClass(), idVenta.getIdVenta());
                detalleVenta.setIdVenta(idVenta);
            }
            em.persist(detalleVenta);
            if (idLibro != null) {
                idLibro.getDetalleVentaCollection().add(detalleVenta);
                idLibro = em.merge(idLibro);
            }
            if (idVenta != null) {
                idVenta.getDetalleVentaCollection().add(detalleVenta);
                idVenta = em.merge(idVenta);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleVenta detalleVenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleVenta persistentDetalleVenta = em.find(DetalleVenta.class, detalleVenta.getIdDetalle());
            Libros idLibroOld = persistentDetalleVenta.getIdLibro();
            Libros idLibroNew = detalleVenta.getIdLibro();
            Ventas idVentaOld = persistentDetalleVenta.getIdVenta();
            Ventas idVentaNew = detalleVenta.getIdVenta();
            if (idLibroNew != null) {
                idLibroNew = em.getReference(idLibroNew.getClass(), idLibroNew.getIdLibro());
                detalleVenta.setIdLibro(idLibroNew);
            }
            if (idVentaNew != null) {
                idVentaNew = em.getReference(idVentaNew.getClass(), idVentaNew.getIdVenta());
                detalleVenta.setIdVenta(idVentaNew);
            }
            detalleVenta = em.merge(detalleVenta);
            if (idLibroOld != null && !idLibroOld.equals(idLibroNew)) {
                idLibroOld.getDetalleVentaCollection().remove(detalleVenta);
                idLibroOld = em.merge(idLibroOld);
            }
            if (idLibroNew != null && !idLibroNew.equals(idLibroOld)) {
                idLibroNew.getDetalleVentaCollection().add(detalleVenta);
                idLibroNew = em.merge(idLibroNew);
            }
            if (idVentaOld != null && !idVentaOld.equals(idVentaNew)) {
                idVentaOld.getDetalleVentaCollection().remove(detalleVenta);
                idVentaOld = em.merge(idVentaOld);
            }
            if (idVentaNew != null && !idVentaNew.equals(idVentaOld)) {
                idVentaNew.getDetalleVentaCollection().add(detalleVenta);
                idVentaNew = em.merge(idVentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleVenta.getIdDetalle();
                if (findDetalleVenta(id) == null) {
                    throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.");
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
            DetalleVenta detalleVenta;
            try {
                detalleVenta = em.getReference(DetalleVenta.class, id);
                detalleVenta.getIdDetalle();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.", enfe);
            }
            Libros idLibro = detalleVenta.getIdLibro();
            if (idLibro != null) {
                idLibro.getDetalleVentaCollection().remove(detalleVenta);
                idLibro = em.merge(idLibro);
            }
            Ventas idVenta = detalleVenta.getIdVenta();
            if (idVenta != null) {
                idVenta.getDetalleVentaCollection().remove(detalleVenta);
                idVenta = em.merge(idVenta);
            }
            em.remove(detalleVenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleVenta> findDetalleVentaEntities() {
        return findDetalleVentaEntities(true, -1, -1);
    }

    public List<DetalleVenta> findDetalleVentaEntities(int maxResults, int firstResult) {
        return findDetalleVentaEntities(false, maxResults, firstResult);
    }

    private List<DetalleVenta> findDetalleVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleVenta.class));
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

    public DetalleVenta findDetalleVenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleVenta> rt = cq.from(DetalleVenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

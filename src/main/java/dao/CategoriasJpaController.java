/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dto.Categorias;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Libros;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author FERNANDO
 */
public class CategoriasJpaController implements Serializable {

    public CategoriasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categorias categorias) {
        if (categorias.getLibrosCollection() == null) {
            categorias.setLibrosCollection(new ArrayList<Libros>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Libros> attachedLibrosCollection = new ArrayList<Libros>();
            for (Libros librosCollectionLibrosToAttach : categorias.getLibrosCollection()) {
                librosCollectionLibrosToAttach = em.getReference(librosCollectionLibrosToAttach.getClass(), librosCollectionLibrosToAttach.getIdLibro());
                attachedLibrosCollection.add(librosCollectionLibrosToAttach);
            }
            categorias.setLibrosCollection(attachedLibrosCollection);
            em.persist(categorias);
            for (Libros librosCollectionLibros : categorias.getLibrosCollection()) {
                Categorias oldIdCategoriaOfLibrosCollectionLibros = librosCollectionLibros.getIdCategoria();
                librosCollectionLibros.setIdCategoria(categorias);
                librosCollectionLibros = em.merge(librosCollectionLibros);
                if (oldIdCategoriaOfLibrosCollectionLibros != null) {
                    oldIdCategoriaOfLibrosCollectionLibros.getLibrosCollection().remove(librosCollectionLibros);
                    oldIdCategoriaOfLibrosCollectionLibros = em.merge(oldIdCategoriaOfLibrosCollectionLibros);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categorias categorias) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorias persistentCategorias = em.find(Categorias.class, categorias.getIdCategoria());
            Collection<Libros> librosCollectionOld = persistentCategorias.getLibrosCollection();
            Collection<Libros> librosCollectionNew = categorias.getLibrosCollection();
            Collection<Libros> attachedLibrosCollectionNew = new ArrayList<Libros>();
            for (Libros librosCollectionNewLibrosToAttach : librosCollectionNew) {
                librosCollectionNewLibrosToAttach = em.getReference(librosCollectionNewLibrosToAttach.getClass(), librosCollectionNewLibrosToAttach.getIdLibro());
                attachedLibrosCollectionNew.add(librosCollectionNewLibrosToAttach);
            }
            librosCollectionNew = attachedLibrosCollectionNew;
            categorias.setLibrosCollection(librosCollectionNew);
            categorias = em.merge(categorias);
            for (Libros librosCollectionOldLibros : librosCollectionOld) {
                if (!librosCollectionNew.contains(librosCollectionOldLibros)) {
                    librosCollectionOldLibros.setIdCategoria(null);
                    librosCollectionOldLibros = em.merge(librosCollectionOldLibros);
                }
            }
            for (Libros librosCollectionNewLibros : librosCollectionNew) {
                if (!librosCollectionOld.contains(librosCollectionNewLibros)) {
                    Categorias oldIdCategoriaOfLibrosCollectionNewLibros = librosCollectionNewLibros.getIdCategoria();
                    librosCollectionNewLibros.setIdCategoria(categorias);
                    librosCollectionNewLibros = em.merge(librosCollectionNewLibros);
                    if (oldIdCategoriaOfLibrosCollectionNewLibros != null && !oldIdCategoriaOfLibrosCollectionNewLibros.equals(categorias)) {
                        oldIdCategoriaOfLibrosCollectionNewLibros.getLibrosCollection().remove(librosCollectionNewLibros);
                        oldIdCategoriaOfLibrosCollectionNewLibros = em.merge(oldIdCategoriaOfLibrosCollectionNewLibros);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categorias.getIdCategoria();
                if (findCategorias(id) == null) {
                    throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.");
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
            Categorias categorias;
            try {
                categorias = em.getReference(Categorias.class, id);
                categorias.getIdCategoria();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.", enfe);
            }
            Collection<Libros> librosCollection = categorias.getLibrosCollection();
            for (Libros librosCollectionLibros : librosCollection) {
                librosCollectionLibros.setIdCategoria(null);
                librosCollectionLibros = em.merge(librosCollectionLibros);
            }
            em.remove(categorias);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categorias> findCategoriasEntities() {
        return findCategoriasEntities(true, -1, -1);
    }

    public List<Categorias> findCategoriasEntities(int maxResults, int firstResult) {
        return findCategoriasEntities(false, maxResults, firstResult);
    }

    private List<Categorias> findCategoriasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categorias.class));
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

    public Categorias findCategorias(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categorias.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categorias> rt = cq.from(Categorias.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

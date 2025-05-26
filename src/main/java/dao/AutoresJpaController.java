/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dto.Autores;
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
 * @author NITRO
 */
public class AutoresJpaController implements Serializable {

    public AutoresJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Autores autores) {
        if (autores.getLibrosCollection() == null) {
            autores.setLibrosCollection(new ArrayList<Libros>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Libros> attachedLibrosCollection = new ArrayList<Libros>();
            for (Libros librosCollectionLibrosToAttach : autores.getLibrosCollection()) {
                librosCollectionLibrosToAttach = em.getReference(librosCollectionLibrosToAttach.getClass(), librosCollectionLibrosToAttach.getIdLibro());
                attachedLibrosCollection.add(librosCollectionLibrosToAttach);
            }
            autores.setLibrosCollection(attachedLibrosCollection);
            em.persist(autores);
            for (Libros librosCollectionLibros : autores.getLibrosCollection()) {
                Autores oldIdAutorOfLibrosCollectionLibros = librosCollectionLibros.getIdAutor();
                librosCollectionLibros.setIdAutor(autores);
                librosCollectionLibros = em.merge(librosCollectionLibros);
                if (oldIdAutorOfLibrosCollectionLibros != null) {
                    oldIdAutorOfLibrosCollectionLibros.getLibrosCollection().remove(librosCollectionLibros);
                    oldIdAutorOfLibrosCollectionLibros = em.merge(oldIdAutorOfLibrosCollectionLibros);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Autores autores) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autores persistentAutores = em.find(Autores.class, autores.getIdAutor());
            Collection<Libros> librosCollectionOld = persistentAutores.getLibrosCollection();
            Collection<Libros> librosCollectionNew = autores.getLibrosCollection();
            Collection<Libros> attachedLibrosCollectionNew = new ArrayList<Libros>();
            for (Libros librosCollectionNewLibrosToAttach : librosCollectionNew) {
                librosCollectionNewLibrosToAttach = em.getReference(librosCollectionNewLibrosToAttach.getClass(), librosCollectionNewLibrosToAttach.getIdLibro());
                attachedLibrosCollectionNew.add(librosCollectionNewLibrosToAttach);
            }
            librosCollectionNew = attachedLibrosCollectionNew;
            autores.setLibrosCollection(librosCollectionNew);
            autores = em.merge(autores);
            for (Libros librosCollectionOldLibros : librosCollectionOld) {
                if (!librosCollectionNew.contains(librosCollectionOldLibros)) {
                    librosCollectionOldLibros.setIdAutor(null);
                    librosCollectionOldLibros = em.merge(librosCollectionOldLibros);
                }
            }
            for (Libros librosCollectionNewLibros : librosCollectionNew) {
                if (!librosCollectionOld.contains(librosCollectionNewLibros)) {
                    Autores oldIdAutorOfLibrosCollectionNewLibros = librosCollectionNewLibros.getIdAutor();
                    librosCollectionNewLibros.setIdAutor(autores);
                    librosCollectionNewLibros = em.merge(librosCollectionNewLibros);
                    if (oldIdAutorOfLibrosCollectionNewLibros != null && !oldIdAutorOfLibrosCollectionNewLibros.equals(autores)) {
                        oldIdAutorOfLibrosCollectionNewLibros.getLibrosCollection().remove(librosCollectionNewLibros);
                        oldIdAutorOfLibrosCollectionNewLibros = em.merge(oldIdAutorOfLibrosCollectionNewLibros);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = autores.getIdAutor();
                if (findAutores(id) == null) {
                    throw new NonexistentEntityException("The autores with id " + id + " no longer exists.");
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
            Autores autores;
            try {
                autores = em.getReference(Autores.class, id);
                autores.getIdAutor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autores with id " + id + " no longer exists.", enfe);
            }
            Collection<Libros> librosCollection = autores.getLibrosCollection();
            for (Libros librosCollectionLibros : librosCollection) {
                librosCollectionLibros.setIdAutor(null);
                librosCollectionLibros = em.merge(librosCollectionLibros);
            }
            em.remove(autores);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Autores> findAutoresEntities() {
        return findAutoresEntities(true, -1, -1);
    }

    public List<Autores> findAutoresEntities(int maxResults, int firstResult) {
        return findAutoresEntities(false, maxResults, firstResult);
    }

    private List<Autores> findAutoresEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Autores.class));
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

    public Autores findAutores(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Autores.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutoresCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Autores> rt = cq.from(Autores.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

package com.compensar.gestion.service;

import com.compensar.gestion.model.Docente;
import com.compensar.gestion.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DocenteService {

    public List<Docente> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Docente d ORDER BY d.id DESC", Docente.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Docente buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Docente docente = em.find(Docente.class, id);
            if (docente == null) {
                throw new BusinessException("No existe el docente con id " + id);
            }
            return docente;
        } finally {
            em.close();
        }
    }

    public Docente crear(Docente docente) {
        validar(docente);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(docente);
            em.getTransaction().commit();
            return docente;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo guardar el docente. Verifica que el correo no esté repetido.");
        } finally {
            em.close();
        }
    }

    public Docente actualizar(Long id, Docente datos) {
        validar(datos);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Docente docente = em.find(Docente.class, id);
            if (docente == null) {
                throw new BusinessException("No existe el docente con id " + id);
            }

            em.getTransaction().begin();
            docente.setNombre(datos.getNombre().trim());
            docente.setCorreo(datos.getCorreo().trim());
            docente.setArea(datos.getArea().trim());
            em.getTransaction().commit();
            return docente;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo actualizar el docente.");
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Docente docente = em.find(Docente.class, id);
            if (docente == null) {
                throw new BusinessException("No existe el docente con id " + id);
            }
            em.getTransaction().begin();
            em.remove(docente);
            em.getTransaction().commit();
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se puede eliminar el docente porque tiene proyectos asociados.");
        } finally {
            em.close();
        }
    }

    private void validar(Docente docente) {
        if (docente == null) {
            throw new BusinessException("Los datos del docente son obligatorios.");
        }
        if (esVacio(docente.getNombre())) {
            throw new BusinessException("El nombre del docente es obligatorio.");
        }
        if (esVacio(docente.getCorreo()) || !docente.getCorreo().contains("@")) {
            throw new BusinessException("Debes ingresar un correo válido para el docente.");
        }
        if (esVacio(docente.getArea())) {
            throw new BusinessException("El área del docente es obligatoria.");
        }
        docente.setNombre(docente.getNombre().trim());
        docente.setCorreo(docente.getCorreo().trim());
        docente.setArea(docente.getArea().trim());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

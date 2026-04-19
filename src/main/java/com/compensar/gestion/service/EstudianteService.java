package com.compensar.gestion.service;

import com.compensar.gestion.model.Estudiante;
import com.compensar.gestion.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EstudianteService {

    public List<Estudiante> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Estudiante e ORDER BY e.id DESC", Estudiante.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Estudiante buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante == null) {
                throw new BusinessException("No existe el estudiante con id " + id);
            }
            return estudiante;
        } finally {
            em.close();
        }
    }

    public Estudiante crear(Estudiante estudiante) {
        validar(estudiante);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(estudiante);
            em.getTransaction().commit();
            return estudiante;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo guardar el estudiante. Verifica que el correo no esté repetido.");
        } finally {
            em.close();
        }
    }

    public Estudiante actualizar(Long id, Estudiante datos) {
        validar(datos);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante == null) {
                throw new BusinessException("No existe el estudiante con id " + id);
            }

            em.getTransaction().begin();
            estudiante.setNombre(datos.getNombre().trim());
            estudiante.setCorreo(datos.getCorreo().trim());
            estudiante.setProgramaAcademico(datos.getProgramaAcademico().trim());
            em.getTransaction().commit();
            return estudiante;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo actualizar el estudiante.");
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante == null) {
                throw new BusinessException("No existe el estudiante con id " + id);
            }
            em.getTransaction().begin();
            em.remove(estudiante);
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
            throw new BusinessException("No se puede eliminar el estudiante porque tiene entregables asociados.");
        } finally {
            em.close();
        }
    }

    private void validar(Estudiante estudiante) {
        if (estudiante == null) {
            throw new BusinessException("Los datos del estudiante son obligatorios.");
        }
        if (esVacio(estudiante.getNombre())) {
            throw new BusinessException("El nombre del estudiante es obligatorio.");
        }
        if (esVacio(estudiante.getCorreo()) || !estudiante.getCorreo().contains("@")) {
            throw new BusinessException("Debes ingresar un correo válido para el estudiante.");
        }
        if (esVacio(estudiante.getProgramaAcademico())) {
            throw new BusinessException("El programa académico es obligatorio.");
        }
        estudiante.setNombre(estudiante.getNombre().trim());
        estudiante.setCorreo(estudiante.getCorreo().trim());
        estudiante.setProgramaAcademico(estudiante.getProgramaAcademico().trim());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

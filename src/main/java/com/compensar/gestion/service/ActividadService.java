package com.compensar.gestion.service;

import com.compensar.gestion.model.Actividad;
import com.compensar.gestion.model.Proyecto;
import com.compensar.gestion.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ActividadService {

    public List<Actividad> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Actividad a ORDER BY a.id DESC", Actividad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Actividad buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Actividad actividad = em.find(Actividad.class, id);
            if (actividad == null) {
                throw new BusinessException("No existe la actividad con id " + id);
            }
            return actividad;
        } finally {
            em.close();
        }
    }

    public Actividad crear(Actividad actividad) {
        validar(actividad);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Proyecto proyecto = em.find(Proyecto.class, actividad.getProyecto().getId());
            if (proyecto == null) {
                throw new BusinessException("El proyecto seleccionado no existe.");
            }

            actividad.setProyecto(proyecto);
            em.getTransaction().begin();
            em.persist(actividad);
            em.getTransaction().commit();
            return actividad;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo guardar la actividad.");
        } finally {
            em.close();
        }
    }

    public Actividad actualizar(Long id, Actividad datos) {
        validar(datos);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Actividad actividad = em.find(Actividad.class, id);
            if (actividad == null) {
                throw new BusinessException("No existe la actividad con id " + id);
            }

            Proyecto proyecto = em.find(Proyecto.class, datos.getProyecto().getId());
            if (proyecto == null) {
                throw new BusinessException("El proyecto seleccionado no existe.");
            }

            em.getTransaction().begin();
            actividad.setNombre(datos.getNombre().trim());
            actividad.setDescripcion(datos.getDescripcion().trim());
            actividad.setFechaEntrega(datos.getFechaEntrega());
            actividad.setProyecto(proyecto);
            em.getTransaction().commit();
            return actividad;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo actualizar la actividad.");
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Actividad actividad = em.find(Actividad.class, id);
            if (actividad == null) {
                throw new BusinessException("No existe la actividad con id " + id);
            }
            em.getTransaction().begin();
            em.remove(actividad);
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
            throw new BusinessException("No se puede eliminar la actividad porque tiene entregables asociados.");
        } finally {
            em.close();
        }
    }

    private void validar(Actividad actividad) {
        if (actividad == null) {
            throw new BusinessException("Los datos de la actividad son obligatorios.");
        }
        if (esVacio(actividad.getNombre())) {
            throw new BusinessException("El nombre de la actividad es obligatorio.");
        }
        if (esVacio(actividad.getDescripcion())) {
            throw new BusinessException("La descripción de la actividad es obligatoria.");
        }
        if (actividad.getFechaEntrega() == null) {
            throw new BusinessException("La fecha de entrega es obligatoria.");
        }
        if (actividad.getProyecto() == null || actividad.getProyecto().getId() == null) {
            throw new BusinessException("Debes seleccionar un proyecto para la actividad.");
        }
        actividad.setNombre(actividad.getNombre().trim());
        actividad.setDescripcion(actividad.getDescripcion().trim());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

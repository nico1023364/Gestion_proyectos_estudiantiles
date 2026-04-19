package com.compensar.gestion.service;

import com.compensar.gestion.model.Actividad;
import com.compensar.gestion.model.Entregable;
import com.compensar.gestion.model.Estudiante;
import com.compensar.gestion.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;

public class EntregableService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("pendiente", "en progreso", "entregado");

    public List<Entregable> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Entregable e ORDER BY e.id DESC", Entregable.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Entregable buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Entregable entregable = em.find(Entregable.class, id);
            if (entregable == null) {
                throw new BusinessException("No existe el entregable con id " + id);
            }
            return entregable;
        } finally {
            em.close();
        }
    }

    public Entregable crear(Entregable entregable) {
        validar(entregable);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Actividad actividad = em.find(Actividad.class, entregable.getActividad().getId());
            Estudiante estudiante = em.find(Estudiante.class, entregable.getEstudiante().getId());

            if (actividad == null) {
                throw new BusinessException("La actividad seleccionada no existe.");
            }
            if (estudiante == null) {
                throw new BusinessException("El estudiante seleccionado no existe.");
            }

            entregable.setActividad(actividad);
            entregable.setEstudiante(estudiante);
            entregable.setEstado(entregable.getEstado().trim().toLowerCase());

            em.getTransaction().begin();
            em.persist(entregable);
            em.getTransaction().commit();
            return entregable;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo guardar el entregable.");
        } finally {
            em.close();
        }
    }

    public Entregable actualizar(Long id, Entregable datos) {
        validar(datos);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Entregable entregable = em.find(Entregable.class, id);
            if (entregable == null) {
                throw new BusinessException("No existe el entregable con id " + id);
            }

            Actividad actividad = em.find(Actividad.class, datos.getActividad().getId());
            Estudiante estudiante = em.find(Estudiante.class, datos.getEstudiante().getId());

            if (actividad == null) {
                throw new BusinessException("La actividad seleccionada no existe.");
            }
            if (estudiante == null) {
                throw new BusinessException("El estudiante seleccionado no existe.");
            }

            em.getTransaction().begin();
            entregable.setActividad(actividad);
            entregable.setEstudiante(estudiante);
            entregable.setEstado(datos.getEstado().trim().toLowerCase());
            entregable.setFechaSubida(datos.getFechaSubida());
            em.getTransaction().commit();
            return entregable;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo actualizar el entregable.");
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Entregable entregable = em.find(Entregable.class, id);
            if (entregable == null) {
                throw new BusinessException("No existe el entregable con id " + id);
            }
            em.getTransaction().begin();
            em.remove(entregable);
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
            throw new BusinessException("No se pudo eliminar el entregable.");
        } finally {
            em.close();
        }
    }

    private void validar(Entregable entregable) {
        if (entregable == null) {
            throw new BusinessException("Los datos del entregable son obligatorios.");
        }
        if (entregable.getActividad() == null || entregable.getActividad().getId() == null) {
            throw new BusinessException("Debes seleccionar una actividad.");
        }
        if (entregable.getEstudiante() == null || entregable.getEstudiante().getId() == null) {
            throw new BusinessException("Debes seleccionar un estudiante.");
        }
        if (entregable.getFechaSubida() == null) {
            throw new BusinessException("La fecha de subida es obligatoria.");
        }
        if (entregable.getEstado() == null || !ESTADOS_VALIDOS.contains(entregable.getEstado().trim().toLowerCase())) {
            throw new BusinessException("El estado debe ser: pendiente, en progreso o entregado.");
        }
    }
}

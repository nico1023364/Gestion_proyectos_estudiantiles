package com.compensar.gestion.service;

import com.compensar.gestion.model.Docente;
import com.compensar.gestion.model.Proyecto;
import com.compensar.gestion.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProyectoService {

    public List<Proyecto> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Proyecto p ORDER BY p.id DESC", Proyecto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Proyecto buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Proyecto proyecto = em.find(Proyecto.class, id);
            if (proyecto == null) {
                throw new BusinessException("No existe el proyecto con id " + id);
            }
            return proyecto;
        } finally {
            em.close();
        }
    }

    public Proyecto crear(Proyecto proyecto) {
        validar(proyecto);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Docente docente = em.find(Docente.class, proyecto.getDocente().getId());
            if (docente == null) {
                throw new BusinessException("El docente seleccionado no existe.");
            }

            proyecto.setDocente(docente);
            em.getTransaction().begin();
            em.persist(proyecto);
            em.getTransaction().commit();
            return proyecto;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo guardar el proyecto.");
        } finally {
            em.close();
        }
    }

    public Proyecto actualizar(Long id, Proyecto datos) {
        validar(datos);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Proyecto proyecto = em.find(Proyecto.class, id);
            if (proyecto == null) {
                throw new BusinessException("No existe el proyecto con id " + id);
            }

            Docente docente = em.find(Docente.class, datos.getDocente().getId());
            if (docente == null) {
                throw new BusinessException("El docente seleccionado no existe.");
            }

            em.getTransaction().begin();
            proyecto.setNombre(datos.getNombre().trim());
            proyecto.setDescripcion(datos.getDescripcion().trim());
            proyecto.setObjetivos(datos.getObjetivos().trim());
            proyecto.setFechaInicio(datos.getFechaInicio());
            proyecto.setFechaFin(datos.getFechaFin());
            proyecto.setDocente(docente);
            em.getTransaction().commit();
            return proyecto;
        } catch (BusinessException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BusinessException("No se pudo actualizar el proyecto.");
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Proyecto proyecto = em.find(Proyecto.class, id);
            if (proyecto == null) {
                throw new BusinessException("No existe el proyecto con id " + id);
            }
            em.getTransaction().begin();
            em.remove(proyecto);
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
            throw new BusinessException("No se puede eliminar el proyecto porque tiene actividades asociadas.");
        } finally {
            em.close();
        }
    }

    private void validar(Proyecto proyecto) {
        if (proyecto == null) {
            throw new BusinessException("Los datos del proyecto son obligatorios.");
        }
        if (esVacio(proyecto.getNombre())) {
            throw new BusinessException("El nombre del proyecto es obligatorio.");
        }
        if (esVacio(proyecto.getDescripcion())) {
            throw new BusinessException("La descripción del proyecto es obligatoria.");
        }
        if (esVacio(proyecto.getObjetivos())) {
            throw new BusinessException("Los objetivos del proyecto son obligatorios.");
        }
        if (proyecto.getFechaInicio() == null || proyecto.getFechaFin() == null) {
            throw new BusinessException("Las fechas del proyecto son obligatorias.");
        }
        if (proyecto.getFechaFin().isBefore(proyecto.getFechaInicio())) {
            throw new BusinessException("La fecha fin no puede ser menor que la fecha inicio.");
        }
        if (proyecto.getDocente() == null || proyecto.getDocente().getId() == null) {
            throw new BusinessException("Debes seleccionar un docente para el proyecto.");
        }
        proyecto.setNombre(proyecto.getNombre().trim());
        proyecto.setDescripcion(proyecto.getDescripcion().trim());
        proyecto.setObjetivos(proyecto.getObjetivos().trim());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

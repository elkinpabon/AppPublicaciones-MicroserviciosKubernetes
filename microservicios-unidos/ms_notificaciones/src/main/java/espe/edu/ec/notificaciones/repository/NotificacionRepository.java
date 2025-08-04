package espe.edu.ec.notificaciones.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import espe.edu.ec.notificaciones.entity.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // ✅ MÉTODOS BÁSICOS
    List<Notificacion> findByTipo(String tipo);
    List<Notificacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // ✅ MÉTODO QUE USA EL SERVICE - CORREGIR QUERY
    @Query("SELECT n FROM Notificacion n ORDER BY n.fecha DESC")
    List<Notificacion> findAllOrderByFechaDesc();
    
    // ✅ MÉTODO ADICIONAL PARA RECIENTES
    @Query("SELECT n FROM Notificacion n WHERE n.fecha >= ?1 ORDER BY n.fecha DESC")
    List<Notificacion> findRecientes(LocalDateTime desde);
    
    // ✅ MÉTODO PARA LAS ÚLTIMAS 10
    @Query("SELECT n FROM Notificacion n ORDER BY n.fecha DESC LIMIT 10")
    List<Notificacion> findTop10ByOrderByFechaDesc();
}
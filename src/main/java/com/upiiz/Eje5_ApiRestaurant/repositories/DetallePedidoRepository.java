package com.upiiz.Eje5_ApiRestaurant.repositories;
import java.util.List;
import com.upiiz.Eje5_ApiRestaurant.entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedidoId(Long pedidoId);
}

package com.upiiz.Eje5_ApiRestaurant.repositories;

import com.upiiz.Eje5_ApiRestaurant.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}

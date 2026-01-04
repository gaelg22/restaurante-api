package com.upiiz.Eje5_ApiRestaurant.repositories;

import com.upiiz.Eje5_ApiRestaurant.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long > {
}

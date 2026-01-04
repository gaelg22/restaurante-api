package com.upiiz.Eje5_ApiRestaurant.repositories;

import com.upiiz.Eje5_ApiRestaurant.entities.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatilloRepository extends JpaRepository<Platillo, Long> {
}

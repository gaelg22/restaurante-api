package com.upiiz.Eje5_ApiRestaurant.services;

import com.upiiz.Eje5_ApiRestaurant.entities.Platillo;
import com.upiiz.Eje5_ApiRestaurant.repositories.PlatilloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatilloService {

    private final PlatilloRepository platilloRepository;

    public PlatilloService(PlatilloRepository platilloRepository) {
        this.platilloRepository = platilloRepository;
    }

    public Platillo savePlatillo(Platillo platillo) {
        return platilloRepository.save(platillo);
    }

    public List<Platillo> getAllPlatillos() {
        return platilloRepository.findAll();
    }

    public Optional<Platillo> getPlatilloById(Long id) {
        return platilloRepository.findById(id);
    }

    public Platillo updatePlatillo(Long id, Platillo platillo) {
        platillo.setId(id);
        return platilloRepository.save(platillo);
    }

    public void deletePlatillo(Long id) {
        platilloRepository.deleteById(id);
    }
}

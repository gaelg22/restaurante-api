package com.upiiz.Eje5_ApiRestaurant.controllers;

import com.upiiz.Eje5_ApiRestaurant.entities.Platillo;
import com.upiiz.Eje5_ApiRestaurant.services.PlatilloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garufa/public/v1/platillos")
public class PlatilloController {

    private final PlatilloService platilloService;

    public PlatilloController(PlatilloService platilloService) {
        this.platilloService = platilloService;
    }

    @PostMapping
    public ResponseEntity<Platillo> crearPlatillo(@RequestBody Platillo platillo) {
        return ResponseEntity.status(201).body(platilloService.savePlatillo(platillo));
    }

    @GetMapping
    public List<Platillo> listarPlatillos() {
        return platilloService.getAllPlatillos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platillo> obtenerPlatillo(@PathVariable Long id) {
        return platilloService.getPlatilloById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Platillo> actualizarPlatillo(@PathVariable Long id, @RequestBody Platillo platillo) {
        return ResponseEntity.ok(platilloService.updatePlatillo(id, platillo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlatillo(@PathVariable Long id) {
        platilloService.deletePlatillo(id);
        return ResponseEntity.noContent().build();
    }
}


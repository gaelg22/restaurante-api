package com.upiiz.Eje5_ApiRestaurant.controllers;

import com.upiiz.Eje5_ApiRestaurant.entities.Pedido;
import com.upiiz.Eje5_ApiRestaurant.entities.DetallePedido;
import com.upiiz.Eje5_ApiRestaurant.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garufa/public/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        return ResponseEntity.status(201).body(pedidoService.savePedido(pedido));
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.getAllPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return pedidoService.getPedidoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.updatePedido(id, pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints relación Pedido ↔ Platillos
    @PostMapping("/{idPedido}/platillos/{idPlatillo}")
    public ResponseEntity<Pedido> agregarPlatillo(@PathVariable Long idPedido, @PathVariable Long idPlatillo) {
        return ResponseEntity.ok(pedidoService.agregarPlatillo(idPedido, idPlatillo));
    }

    @DeleteMapping("/{idPedido}/platillos/{idPlatillo}")
    public ResponseEntity<Pedido> quitarPlatillo(@PathVariable Long idPedido, @PathVariable Long idPlatillo) {
        return ResponseEntity.ok(pedidoService.quitarPlatillo(idPedido, idPlatillo));
    }

    @GetMapping("/{idPedido}/platillos")
    public List<DetallePedido> listarPlatillosPedido(@PathVariable Long idPedido) {
        return pedidoService.listarPlatillosPedido(idPedido);
    }
}


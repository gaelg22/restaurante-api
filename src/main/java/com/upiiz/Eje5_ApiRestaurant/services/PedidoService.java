package com.upiiz.Eje5_ApiRestaurant.services;

import com.upiiz.Eje5_ApiRestaurant.entities.Pedido;
import com.upiiz.Eje5_ApiRestaurant.entities.Platillo;
import com.upiiz.Eje5_ApiRestaurant.entities.DetallePedido;
import com.upiiz.Eje5_ApiRestaurant.repositories.PedidoRepository;
import com.upiiz.Eje5_ApiRestaurant.repositories.PlatilloRepository;
import com.upiiz.Eje5_ApiRestaurant.repositories.DetallePedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PlatilloRepository platilloRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PlatilloRepository platilloRepository,
                         DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.platilloRepository = platilloRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // ===============================
    // CRUD PEDIDOS
    // ===============================

    public Pedido savePedido(Pedido pedido) {
        pedido.setFecha(LocalDate.now());
        pedido.setTotal(0.0);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> getPedidoById(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido updatePedido(Long id, Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    public void deletePedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    // ===============================
    // RELACIÓN PEDIDO ↔ PLATILLOS
    // ===============================

    public Pedido agregarPlatillo(Long idPedido, Long idPlatillo) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        Platillo platillo = platilloRepository.findById(idPlatillo)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Platillo no encontrado"));

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setPlatillo(platillo);
        detalle.setPrecioActual(platillo.getPrecio());

        detallePedidoRepository.save(detalle);

        pedido.setTotal(pedido.getTotal() + platillo.getPrecio());
        return pedidoRepository.save(pedido);
    }

    public Pedido quitarPlatillo(Long idPedido, Long idPlatillo) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(idPedido);

        DetallePedido detalle = detalles.stream()
                .filter(d -> d.getPlatillo().getId().equals(idPlatillo))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Platillo no existe en el pedido"));

        // Evita total negativo
        double nuevoTotal = pedido.getTotal() - detalle.getPrecioActual();
        pedido.setTotal(Math.max(nuevoTotal, 0.0));

        detallePedidoRepository.delete(detalle);

        return pedidoRepository.save(pedido);
    }

    public List<DetallePedido> listarPlatillosPedido(Long idPedido) {

        if (!pedidoRepository.existsById(idPedido)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        return detallePedidoRepository.findByPedidoId(idPedido);
    }
}

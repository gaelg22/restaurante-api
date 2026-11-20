package com.upiiz.Eje5_ApiRestaurant.services;

import com.upiiz.Eje5_ApiRestaurant.entities.DetallePedido;
import com.upiiz.Eje5_ApiRestaurant.entities.Pedido;
import com.upiiz.Eje5_ApiRestaurant.entities.Platillo;
import com.upiiz.Eje5_ApiRestaurant.repositories.DetallePedidoRepository;
import com.upiiz.Eje5_ApiRestaurant.repositories.PedidoRepository;
import com.upiiz.Eje5_ApiRestaurant.repositories.PlatilloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PlatilloRepository platilloRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void savePedido_poneLaFechaDeHoyYElTotalEnCero() {
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido pedido = new Pedido();
        pedido.setTotal(999.0); // aunque venga con total, se reinicia

        Pedido guardado = pedidoService.savePedido(pedido);

        assertThat(guardado.getFecha()).isEqualTo(LocalDate.now());
        assertThat(guardado.getTotal()).isZero();
    }

    @Test
    void agregarPlatillo_sumaElPrecioAlTotalYGuardaElPrecioDeEseMomento() {
        Pedido pedido = pedidoConTotal(1L, 100.0);
        Platillo tacos = platillo(2L, 85.5);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(platilloRepository.findById(2L)).thenReturn(Optional.of(tacos));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.agregarPlatillo(1L, 2L);

        assertThat(resultado.getTotal()).isEqualTo(185.5);
        ArgumentCaptor<DetallePedido> detalle = ArgumentCaptor.forClass(DetallePedido.class);
        verify(detallePedidoRepository).save(detalle.capture());
        assertThat(detalle.getValue().getPrecioActual()).isEqualTo(85.5);
        assertThat(detalle.getValue().getPlatillo()).isSameAs(tacos);
    }

    @Test
    void agregarPlatillo_conPlatilloInexistente_regresa404YNoCambiaNada() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoConTotal(1L, 50.0)));
        when(platilloRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.agregarPlatillo(1L, 99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(detallePedidoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void quitarPlatillo_restaElPrecioQueSeCobroAunqueElPlatilloHayaSubido() {
        Pedido pedido = pedidoConTotal(1L, 200.0);
        Platillo tacos = platillo(2L, 120.0);          // hoy cuesta 120...
        DetallePedido detalle = detalle(pedido, tacos, 80.0); // ...pero se cobró a 80

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(detallePedidoRepository.findByPedidoId(1L)).thenReturn(List.of(detalle));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.quitarPlatillo(1L, 2L);

        assertThat(resultado.getTotal()).isEqualTo(120.0);
        verify(detallePedidoRepository).delete(detalle);
    }

    @Test
    void quitarPlatillo_nuncaDejaElTotalNegativo() {
        Pedido pedido = pedidoConTotal(1L, 30.0);
        DetallePedido detalle = detalle(pedido, platillo(2L, 50.0), 50.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(detallePedidoRepository.findByPedidoId(1L)).thenReturn(List.of(detalle));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(pedidoService.quitarPlatillo(1L, 2L).getTotal()).isZero();
    }

    @Test
    void quitarPlatillo_queNoEstaEnElPedido_regresa404() {
        Pedido pedido = pedidoConTotal(1L, 100.0);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(detallePedidoRepository.findByPedidoId(1L))
                .thenReturn(List.of(detalle(pedido, platillo(3L, 40.0), 40.0)));

        assertThatThrownBy(() -> pedidoService.quitarPlatillo(1L, 2L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(detallePedidoRepository, never()).delete(any());
    }

    @Test
    void updatePedido_inexistente_regresa404() {
        when(pedidoRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> pedidoService.updatePedido(7L, new Pedido()))
                .isInstanceOf(ResponseStatusException.class);
        verify(pedidoRepository, never()).save(any());
    }

    private Pedido pedidoConTotal(Long id, double total) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setTotal(total);
        return pedido;
    }

    private Platillo platillo(Long id, double precio) {
        Platillo platillo = new Platillo();
        platillo.setId(id);
        platillo.setPrecio(precio);
        return platillo;
    }

    private DetallePedido detalle(Pedido pedido, Platillo platillo, double precioCobrado) {
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setPlatillo(platillo);
        detalle.setPrecioActual(precioCobrado);
        return detalle;
    }
}

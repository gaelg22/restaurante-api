package com.upiiz.Eje5_ApiRestaurant.controllers;

import com.upiiz.Eje5_ApiRestaurant.entities.Pedido;
import com.upiiz.Eje5_ApiRestaurant.services.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    private static final String BASE = "/garufa/public/v1/pedidos";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void obtenerPedido_inexistente_regresa404() throws Exception {
        when(pedidoService.getPedidoById(9L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE + "/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarPlatillo_regresaElPedidoConElTotalNuevo() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setTotal(185.5);
        when(pedidoService.agregarPlatillo(1L, 2L)).thenReturn(pedido);

        mockMvc.perform(post(BASE + "/1/platillos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(185.5));
    }

    @Test
    void agregarPlatillo_inexistente_regresa404() throws Exception {
        when(pedidoService.agregarPlatillo(1L, 99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Platillo no encontrado"));

        mockMvc.perform(post(BASE + "/1/platillos/99"))
                .andExpect(status().isNotFound());
    }
}

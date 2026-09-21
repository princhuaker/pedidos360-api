package cl.duoc.pedidos360.controller;

import cl.duoc.pedidos360.model.Pedido;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    @GetMapping("/prueba")
    public Map<String, Object> probarApi() {
        return Map.of(
                "mensaje", "Pedidos360 funciona correctamente",
                "estado", "activo",
                "codigo", 200
        );
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable Long id) {

        return pedidos.stream()
                .filter(pedido -> pedido.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @Valid @RequestBody Pedido pedido
    ) {
        pedido.setId(contador.getAndIncrement());

        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            pedido.setEstado("PENDIENTE");
        }

        pedidos.add(pedido);

        return ResponseEntity
                .status(201)
                .body(pedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody Pedido datosNuevos
    ) {
        for (Pedido pedido : pedidos) {

            if (pedido.getId().equals(id)) {
                pedido.setCliente(datosNuevos.getCliente());
                pedido.setProducto(datosNuevos.getProducto());
                pedido.setCantidad(datosNuevos.getCantidad());

                if (datosNuevos.getEstado() != null
                        && !datosNuevos.getEstado().isBlank()) {
                    pedido.setEstado(datosNuevos.getEstado());
                }

                return ResponseEntity.ok(pedido);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {

        boolean eliminado = pedidos.removeIf(
                pedido -> pedido.getId().equals(id)
        );

        if (eliminado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
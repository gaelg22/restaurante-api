# Restaurante Garufa — API REST

![CI](https://github.com/gaelg22/ejercicio05/actions/workflows/ci.yml/badge.svg)

API para un restaurante: clientes, platillos y pedidos. Un pedido se arma agregando o quitando platillos y el total se va recalculando solo. Ejercicio de la materia en la UPIIZ-IPN.

## Stack

Java 17 · Spring Boot 3.5 · Spring Data JPA · MySQL · JUnit 5 + Mockito · Docker · GitHub Actions

## Qué tiene

- CRUD de **clientes, platillos y pedidos**.
- Relación pedido ↔ platillos con una tabla de detalle (`detalle_pedidos`).
- Cada detalle guarda el **precio con el que se cobró**. Si el platillo sube de precio después, quitarlo del pedido resta lo que se cobró y no el precio nuevo.
- El total nunca queda negativo y los errores regresan `404` con mensaje claro (`ResponseStatusException`).
- Dockerfile multi-stage: compila con Maven dentro de Docker, así que no hace falta subir el JAR.
- **Tests** que corren solos en cada push con GitHub Actions.

## Endpoints

Base: `/garufa/public/v1`

| Método | Ruta | Qué hace |
|---|---|---|
| GET, POST | `/clientes`, `/platillos`, `/pedidos` | Listar y crear |
| GET, PUT, DELETE | `/clientes/{id}`, `/platillos/{id}`, `/pedidos/{id}` | Ver, editar y borrar |
| POST | `/pedidos/{idPedido}/platillos/{idPlatillo}` | Agregar un platillo al pedido |
| DELETE | `/pedidos/{idPedido}/platillos/{idPlatillo}` | Quitar un platillo del pedido |
| GET | `/pedidos/{idPedido}/platillos` | Ver los platillos de un pedido |

## Variables de entorno

| Variable | Para qué |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de MySQL, p. ej. `jdbc:mysql://localhost:3306/restaurante` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Usuario y contraseña de la base |
| `PORT` | Puerto (opcional, por defecto 8080) |

Hay un ejemplo en [`.env.example`](.env.example).

## Correrlo

```bash
./mvnw spring-boot:run
# o con Docker
docker build -t restaurante-api . && docker run --env-file .env -p 8080:8080 restaurante-api
```

## Tests

```bash
./mvnw test
```

Usan H2 en memoria, no necesitas MySQL.

- `PedidoServiceTest`: total al agregar y quitar platillos, precio cobrado vs precio actual, total que no baja de cero y errores 404.
- `PedidoControllerTest`: respuestas HTTP del controlador con `@WebMvcTest` y el servicio mockeado.
- `Eje5ApiRestaurantApplicationTests`: que la app levante completa con H2.

## Pendiente

- Cambiar los montos de `Double` a `BigDecimal` para no tener errores de redondeo con dinero.

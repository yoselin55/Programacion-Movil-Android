# LavaCampus

App de la lavandería de un campus universitario, hecha con **Kotlin, Jetpack Compose y Material 3**, con **MVVM + Repository**, **Room** y **DataStore**.

## Descripción

Tiene dos perfiles en el mismo dispositivo que comparten la misma base de datos Room.

- **Estudiante:** arma un pedido eligiendo servicios y cantidades (kilos o prendas), ve el total y la fecha estimada de entrega, sigue el estado de su ropa y la recoge en un casillero.
- **Administrador:** recibe los pedidos, los avanza de estado (Recibido, En proceso, Listo, Entregado), asigna el casillero, cobra, administra las tarifas y consulta a sus clientes.

Cuando el administrador marca un pedido como Listo, el estudiante lo ve con su casillero al instante, porque ambas pantallas observan la misma base.

Todos los datos son ficticios (Ley 29733).

**Reglas de negocio principales**

- Máximo 3 pedidos activos por estudiante.
- Servicios por kilo: de 1.0 a 10.0 en pasos de 0.5. Por prenda o unidad: enteros de 1 a 30.
- El pedido y todas sus líneas se guardan en **una sola transacción**, con el precio congelado en cada línea.
- Fecha de entrega: hoy más las horas del servicio, saltando los domingos.
- Los estados avanzan de uno en uno: RECIBIDO → EN_PROCESO → LISTO → ENTREGADO.
- Para pasar a LISTO se exige un casillero libre (C-01 a C-30). Para entregar, el pedido debe estar cobrado.
- Un servicio que ya está en algún pedido no se elimina: solo se desactiva.

## Cuentas de prueba

| Perfil | Correo | Clave |
|---|---|---|
| Estudiante | ana@correo.test | 1234 |
| Estudiante | luis@correo.test | 1234 |
| Estudiante | marta@correo.test | 1234 |
| Administrador | admin@lavacampus.test | admin |

## Arquitectura MVVM + Repository

```text
VIEW (Composable)
   |  eventos (clics)                ^  collectAsState()
   v                                 |
VIEWMODEL (StateFlow + reglas de negocio)
   |  llama                          ^  Flow / resultados
   v                                 |
LavaRepository (interface)
   |  la implementa
   v
LavaRepositoryRoom  -->  DAO  -->  SQLite (lavacampus.db)

DataStore (solo preferencias)  -->  ViewModel
```

| Capa | Conoce a | No debe |
|---|---|---|
| View (`ui/`) | Solo a su ViewModel | Usar DAO, Repository ni reglas de negocio |
| ViewModel | Solo al Repository (interface) | Conocer Composables ni el DAO |
| Repository | Los DAO (en la implementación) | Conocer pantallas ni ViewModels |
| DAO / Room | SQL y entidades | Conocer ViewModels |

## Base de datos (4 tablas)

| Tabla | Relación |
|---|---|
| `usuarios` | — |
| `servicios` | — |
| `pedidos` | N pedidos → 1 estudiante |
| `detalles_pedido` | N detalles → 1 pedido; N detalles → 1 servicio |

## Estructura de paquetes

```text
com.flores.lavacampus
├── MainActivity.kt
├── LavaApp.kt                      crea la base y el repository
├── data
│   ├── local
│   │   ├── entity                  Servicio, Usuario, Pedido, DetallePedido
│   │   ├── dao                     ServicioDao, UsuarioDao, PedidoDao, DetalleDao
│   │   ├── relation                DetalleConServicio, PedidoConDetalle
│   │   ├── LavaDatabase.kt
│   │   └── DatosIniciales.kt
│   ├── repository                  LavaRepository (interface), LavaRepositoryRoom
│   └── prefs                       PreferenciasStore (DataStore)
├── model                           Rol, EstadoPedido, Unidad, LineaPedido, IngresoServicio,
│                                   ClienteResumen, Casilleros, DatoGrafico, Descuento
├── viewmodel                       Sesion, Inicio, NuevoPedido, MisPedidos, DetallePedido,
│                                   Tarifas, Perfil, Panel, Bandeja, GestionPedido, Servicios,
│                                   Clientes, LavaViewModelFactory
└── ui
    ├── theme                       Color, Type, Theme
    ├── components                  AppTopBar, LavaButton, LavaTextField, EstadoPedidoChip,
    │   │                           PedidoCard, StepperEstado, IconoServicio, SelectorCantidad ...
    │   └── graficos                GraficoDona, GraficoBarras
    ├── navigation                  Rutas, NavLava
    ├── estudiante                  Splash, Login, Inicio, NuevoPedido, DetallePedido,
    │                               MisPedidos, Tarifas, Perfil
    └── administrador               Panel, Bandeja, GestionPedido, Servicios,
                                    FormularioServicio, Clientes, Ajustes
```

## Capturas

**Estudiante**

| Inicio | Nuevo pedido | Mis pedidos |
|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/fc3157db-65d4-41ec-806d-0cd2254784e6" width="220" alt="Inicio"> | <img src="https://github.com/user-attachments/assets/b5045972-ed93-48ff-9ff7-7e0847d99ba5" width="220" alt="Nuevo pedido"> | <img src="https://github.com/user-attachments/assets/2c84dc6a-93ac-467c-b68f-eb872d20cf31" width="220" alt="Mis pedidos"> |

**Administrador**

| Panel | Bandeja de pedidos | Gestión del pedido |
|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/3b33c9b9-e971-4a64-8b01-8faccf7b732b" width="220" alt="Panel"> | <img src="https://github.com/user-attachments/assets/b3753b66-6228-402e-96d1-cd45217de640" width="220" alt="Bandeja"> | <img src="https://github.com/user-attachments/assets/dfc00457-27e6-4664-a6ba-d447aa130441" width="220" alt="Gestión"> |

## Para pensar

**¿Por qué un pedido tiene los detalles en otra tabla?**
Porque un pedido puede incluir varios servicios (por ejemplo lavado y planchado). Guardar cada línea como una fila de `detalles_pedido` evita columnas repetidas y permite consultar el ingreso por servicio con un `GROUP BY`. Es la relación uno a muchos más común.

**¿Por qué guardar `precioUnitario` y `subtotal` en el detalle?**
Porque es una "foto" del momento: si mañana la lavandería sube el precio del kilo, los pedidos antiguos conservan lo que costaron y los ingresos del panel no se alteran.

**¿La función que crea el pedido es `suspend` o `Flow`?**
Es `suspend`, porque cambia los datos una sola vez. Las lecturas que deben actualizar la pantalla cuando los datos cambian son `Flow`. No se reemplazan, se complementan.

## Pruebas

Las pruebas de aceptación automáticas están en `app/src/androidTest/.../AceptacionTest.kt` y se ejecutan con:

```bash
./gradlew connectedDebugAndroidTest
```

## Tecnologías

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Room · DataStore · KSP · Coroutines y Flow

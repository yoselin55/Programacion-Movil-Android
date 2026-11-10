# Mycar

App Android para **explorar y comprar autos**, hecha con Kotlin y Jetpack Compose.
Laboratorio 11-12: Compose + MVVM + Retrofit + Room + Firebase Auth (Tecsup, Programación Móvil III).

- Rama `main`: fase 1, cumple el laboratorio.
- Rama `MyCar-IA`: fase 2, mejorada con IA. Los prompts están en [PROMPTS.md](PROMPTS.md).

## Qué hace

- **Catálogo de autos** desde la API pública NHTSA vPIC: marcas (`GetMakesForVehicleType/car`) y modelos por marca y año (`GetModelsForMakeIdYear`).
- **Room como fuente de verdad**: las marcas se guardan en el teléfono y la app funciona sin conexión.
- **Detalle** del auto con pestañas, fotos y descripción (Wikipedia).
- **Favoritos** guardados en Room.
- **Login y registro** con Firebase Auth. Todo el que se registra queda con rol `usuario`.
- **Roles** en Firestore (`usuarios/{uid}` con `rol`): el administrador tiene su propio panel.
- **Compra ficticia**: recoger en la agencia o envío a casa (dirección o ubicación actual), con fecha. No se piden datos de pago.
- **Citas** con horario único (dos personas no pueden tomar el mismo).
- **Mis pedidos**: historial con seguimiento (En proceso, Pagado, Comprado o No recogido).
- **Notificación local** y avisos en tiempo real mientras la app está abierta o en segundo plano.
- **Panel del administrador**: totales, usuarios, catálogo (activar o desactivar marcas y autos), promociones, pedidos y citas, sincronizar y limpiar caché.
- **Modo claro y oscuro** desde Perfil.

## Arquitectura

MVVM con `UiState` y `StateFlow`; el Repository es una interface y la inyección es manual (`AppContainer` + `MycarViewModelFactory`).

```
ui/          composables sin lógica + ViewModels
domain/      modelos (Auto, Item, Cita, Promocion...)
data/        Retrofit (remote), Room (local), Firestore y repositorios
notification/
```

Librerías: Retrofit + kotlinx.serialization, Room, Navigation Compose, Coil 3.3.0, Firebase Auth y Firestore.

## Cómo ejecutarla

1. Abre `Semana12` en Android Studio.
2. Pon tu `google-services.json` en `app/` (no se sube al repositorio).
3. En Firebase activa **Authentication (correo y contraseña)** y **Firestore**.
4. En Firestore, pestaña **Reglas**, pega el contenido de `firestore.rules` y publica.
5. Crea al administrador a mano: un usuario en Authentication y un documento `usuarios/{uid}` con `rol: "admin"`.
6. Ejecuta en un emulador o teléfono con internet.

## Pruebas

- Pruebas unitarias de los ViewModel con repositorios falsos (`ListaViewModelTest`).
- Lint sin errores.

```
./gradlew testDebugUnitTest lintDebug
```

## Mejora con IA: 5 prompts, 5 commits

Cada prompt dio origen a un commit. El detalle de lo que se pidió, lo que respondió la IA, lo que se cambió y lo aprendido está en [PROMPTS.md](PROMPTS.md).

| # | Prompt | Commit |
|---|--------|--------|
| 1 | Diseño final con la paleta, login, perfil, menú lateral y barra inferior | `0b3b184` feat: nuevo diseño, tema, login y componentes de la interfaz |
| 2 | Catálogo de autos desde la API con fotos, logos, detalle y avisos | `60da595` feat: catálogo desde la API, logos, avisos en tiempo real y notificación |
| 3 | Carrito, citas, pedidos, promociones y panel del administrador | `e50457e` feat: panel propio del administrador con catálogo, pedidos y citas |
| 4 | Compra a pantalla completa: recoger en agencia o envío, dirección o ubicación y fecha | `fc5b1a2` fix: compra en pantalla completa con envío o recojo y fecha |
| 5 | Fotos con movimiento, promociones con foto, icono de la app y panel más elegante | `7bb1ece` feat: carrusel de fotos con movimiento, promociones con foto e icono de la app |

## Seguridad

- Las reglas de Firestore (`firestore.rules`) son las que protegen los datos: solo el administrador escribe catálogo, promociones, estados de pedidos y confirma citas.
- Nadie puede registrarse como administrador: las reglas rechazan cualquier rol distinto de `usuario`.
- `google-services.json` está en `.gitignore`.

## Limitaciones

- El pago es una simulación: no hay pasarela ni validación de direcciones.
- Los avisos en tiempo real no llegan con la app cerrada del todo; haría falta Firebase Cloud Messaging.
- Las fotos de los autos y los logos son propios; los modelos y las marcas vienen de la API.

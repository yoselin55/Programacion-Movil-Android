# Prompts utilizados

Fase 2 de LavaCampus (rama de mejora con IA). Herramienta usada: **Claude Code** (Anthropic), en lugar de Gemini.
Todas las mejoras respetan MVVM + Repository: la View no usa DAO ni Repository, el ViewModel solo conoce la interface `LavaRepository`, y la base sube de versión cuando cambia una entidad.

## Prompt 1 · Pedidos atrasados (commit A)

**Texto enviado a la IA:**

> Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un LavaRepository (interface). Agrega en la bandeja del administrador una marca roja para los pedidos atrasados (fechaEntrega menor que hoy y estado distinto de ENTREGADO y CANCELADO), y que el panel los cuente. La consulta va en PedidoDao, la función en LavaRepository y su implementación, y el valor se expone como StateFlow en BandejaViewModel y en PanelViewModel. La View solo lo muestra. No uses XML ni librerías externas. Agrega un dato de ejemplo atrasado y una prueba automática. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** la consulta `observarIdsAtrasados` en `PedidoDao`, la función `observarPedidosAtrasados` en la interface y en `LavaRepositoryRoom`, los `StateFlow` en `BandejaViewModel` y `PanelViewModel`, la tarjeta con borde rojo en la Bandeja, la alerta roja del Panel, un pedido de ejemplo con entrega vencida y una prueba automática.

**Qué tuve que corregir yo:** el texto de la tarjeta ("Atrasado · debía entregarse ...") se partía en dos líneas, así que lo acorté a "Atrasado · era para el ...".

**¿Respeta MVVM?:** Sí. La consulta está en el DAO, la función se declaró primero en la interface, el valor sale como `StateFlow` y la View solo lo muestra. No cambió ninguna entidad, así que no sube la versión de la base.

## Prompt 2 · Descuento por volumen (commit B)

**Texto enviado a la IA:**

> En mi app Kotlin con Compose, Room, MVVM y LavaRepository (interface), agrega un descuento por volumen: si el lavado (servicios por kilo) supera 5 kg se aplica 10 % de descuento sobre el lavado, visible en el resumen de Nuevo pedido y guardado en el pedido. Agrega la columna en la entidad Pedido, sube la versión de la base con una migración, calcula y guarda el descuento en crearPedido (dentro de la transacción) y valida las cantidades en el Repository. El ViewModel expone subtotal, descuento y total como StateFlow; la View solo los muestra. Una sola clase con la regla para que resumen y Repository coincidan. No uses librerías externas. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** la clase `Descuento` con la regla, la columna `descuento` en `Pedido`, la versión 2 de la base con `MIGRACION_1_2`, el cálculo y las validaciones en `crearPedido`, subtotal/descuento/total en `NuevoPedidoViewModel`, el resumen con la línea de descuento y la pista de 5 kg, y 5 pruebas automáticas.

**Qué tuve que corregir yo:**
1. Los ingresos por servicio del Panel no restaban el descuento y no cuadraban con los ingresos de la semana (S/ 111.50 contra S/ 106.70). Lo detectó una prueba automática y se corrigió la consulta de `DetalleDao`.
2. La pista del descuento se partía en dos líneas y se acortó.

**¿Respeta MVVM?:** Sí. El cálculo vive en una clase del modelo usada por el ViewModel y el Repository, la View solo muestra los `StateFlow`, y como cambió la entidad `Pedido` se subió la versión de la base con una migración que conserva los datos.

## Prompt 3 · Repetir un pedido (commit C)

**Texto enviado a la IA:**

> En mi app Kotlin con Compose, Room, MVVM y LavaRepository (interface), agrega "Pedir de nuevo": en el Historial de Mis pedidos, un botón que abre Nuevo pedido con las mismas líneas y cantidades del pedido anterior, pero con los precios actuales. Solo cambian ViewModel y View: usa las funciones que ya existen en LavaRepository. Valida que el pedido sea del mismo estudiante, omite los servicios inactivos y avisa si algún precio cambió. La ruta de Nuevo pedido recibe el id del pedido a repetir como argumento opcional. Agrega pruebas automáticas. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** el botón "Pedir de nuevo" en el Historial y en el Detalle, la ruta con argumento opcional, la carga de líneas en `NuevoPedidoViewModel` con los precios actuales, las validaciones (pedido propio, servicios inactivos, cambio de precios), el aviso en pantalla y 2 pruebas automáticas.

**Qué tuve que corregir yo:** al pasar el id por la ruta, el `popUpTo` dejó de funcionar porque ya no coincidía con la ruta con argumento; se corrigió para usar la ruta completa.

**¿Respeta MVVM?:** Sí. Solo cambian ViewModel y View. El ViewModel usa `observarPedido` y `observarServiciosActivos`, que ya estaban en la interface, y la View no conoce el DAO ni el Repository.

## Prompt 4 · Mejora de interfaz: logo, imágenes, paleta, foto de perfil, seguridad y registro (commit E)

**Texto enviado a la IA:**

> En mi app Kotlin con Jetpack Compose, Material 3, MVVM, Room y LavaRepository (interface), mejora la interfaz.
> (1) Usa mi logo en el Splash, en el Login y como ícono de la app.
> (2) Aplica estas paletas: estudiante en azul (#021024, #052659, #5483B3, #7DA0CA, #C1E8FF) y administrador en vino y rosa (#4B0F1E, #6D1D32, #8E2B44, #B23C59, #CC5671, #E07A94, #F7D6DC), también en modo oscuro. El modo oscuro del administrador debe verse bien: sin bordes blancos y con las tarjetas separadas del fondo.
> (3) Agrega fotos e íconos referentes a la lavandería, tomados de internet con licencia libre.
> (4) Letras más grandes en toda la app.
> (5) El usuario puede poner su foto de perfil.
> (6) "Cambiar de perfil" no debe ser un salto libre: debe pedir las credenciales del otro perfil.
> (7) Al iniciar sesión debe aparecer en la pantalla un mensaje de bienvenida que dure unos segundos.
> (8) En el Login, el Estudiante puede elegir entre "Iniciar sesión" y "Registrarse" (con validaciones de cada campo), y el Administrador solo tiene "Iniciar sesión", sin registro.
> La View no usa DAO ni Repository, el ViewModel solo conoce la interface LavaRepository, y si agregas funciones al Repository, las declaras primero en la interface. No uses librerías externas. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- **Paletas y modo oscuro:** las paletas clara y oscura de cada perfil en `Theme.kt`, con colores de superficie y de borde propios (`surfaceVariant` y `outlineVariant`). Los colores claros fijos de las pantallas se reemplazaron por colores del tema, y la capa de color sobre las fotos y los contadores del Panel se ajustan al modo oscuro. Las letras son un 15 % más grandes en toda la app.
- **Logo e imágenes:** el logo recortado con esquinas transparentes, el ícono adaptativo de la app y los íconos clásicos en todas las densidades. Cinco fotos libres (lavadoras, plancha, tintorería, toallas y canasta) de menos de 200 KB cada una, y los componentes `FotoConDegradado`, `FotoServicio`, `AvatarUsuario`, `EstadoVacio` y `PieLogo`.
- **Foto de perfil:** `FotoPerfilStore` guarda la foto de cada usuario en el almacenamiento interno de la app, reducida, con la rotación corregida y recortada cuadrada. Se elige con el selector de fotos del sistema, que no pide permisos.
- **Cambio de perfil seguro:** el cuadro "Cambiar de perfil" pide correo y clave del otro perfil, y `SesionViewModel.cambiarPerfil(correo, clave)` rechaza claves incorrectas y cuentas del mismo rol.
- **Bienvenida:** un aviso grande arriba de la pantalla, con el logo, que dice "¡Te damos la bienvenida, [nombre]!" y dura 5 segundos.
- **Registro de estudiantes:** tres funciones nuevas en la interface `LavaRepository` (`existeCorreo`, `existeCodigo`, `registrarUsuario`), su implementación en Room y las consultas del DAO. `ValidacionRegistro` valida nombre y apellido, código (U y 9 números), correo, contraseña (mínimo 4 caracteres y sin espacios) y confirmación. Los errores salen debajo de cada campo y desaparecen al corregirlo. El correo y el código no se repiten, sin distinguir mayúsculas. Los registrados quedan siempre como estudiantes, así que el administrador no se puede crear desde la app.
- **Login:** el estudiante ve "Iniciar sesión | Registrarse" y el administrador solo "Ingresar".
- **Pruebas automáticas** del cambio de perfil, de las validaciones y del registro.

**Qué tuve que corregir yo:**
1. El PDF pedía que "Cambiar de perfil" guardara el otro perfil y navegara a su grafo, sin credenciales. Lo consideré un hueco de seguridad y lo cambié para que pida correo y contraseña.
2. El ícono de la app se veía recortado ("vaCam"). Lo corregí usando solo el símbolo del logo, sin el texto, dentro de la zona segura del ícono adaptativo.
3. La IA dejó un color violeta fijo en los gráficos del Panel. Lo cambié por la paleta nueva.
4. El modo oscuro del administrador tenía bordes blancos, tarjetas que no se distinguían del fondo y contadores con poco contraste. Lo corregí con colores de borde y de superficie propios de cada paleta.
5. Una prueba automática mostró que el nombre "carla  mendoza" (con dos espacios) se rechazaba. Se normalizan los espacios antes de validar.
6. El código de estudiante perdía un carácter al escribirlo rápido, porque se transformaba a mayúsculas mientras se escribía. Pasé a usar las mayúsculas del teclado.
7. Los mensajes de error seguían visibles después de corregir un campo. Ahora desaparecen al editarlo.
8. La bienvenida salía como un mensaje pequeño que se veía muy poco. La cambié por un aviso grande arriba de la pantalla que dura 5 segundos.
9. Al iniciar sesión aparecía por un instante la foto de la canasta azul y el texto "No tienes pedidos activos" antes de mostrar los pedidos reales. Pasaba porque las listas empiezan vacías mientras Room carga. Los ViewModels (`InicioViewModel`, `MisPedidosViewModel`, `BandejaViewModel` y `ClientesViewModel`) ahora publican su estado como `null` mientras carga, y la pantalla vacía solo se muestra cuando de verdad no hay datos. Mientras llegan los datos, el Inicio muestra solo el banner de arriba y el resto entra completo de una vez, sin pantallas ni textos intermedios.
10. Un comando de la IA (`git checkout`) devolvió por error 8 archivos al último commit y borró los cambios de interfaz hechos en ellos. Se detectó al compilar y se volvieron a aplicar antes de seguir.

**¿Respeta MVVM?:** Sí. La foto, las credenciales y el registro se manejan en `SesionViewModel`, que solo conoce la interface `LavaRepository` y el almacén de la foto. Las funciones nuevas del registro se declararon primero en la interface. Las validaciones están en una clase del modelo y las pantallas solo muestran el estado. La foto de perfil no va en Room, porque es un archivo y no un dato relacional, y no cambió ninguna entidad, por lo que la base sigue en la versión 2.

## Créditos de las imágenes

Fotos tomadas de Wikimedia Commons con licencia libre:

| Uso en la app | Foto | Autor | Licencia |
|---|---|---|---|
| Lavadoras (Splash, Login, Inicio) | Coin-operated washing machines in Seven Dials Launderette & Dry Cleaners, Brighton | Andy Li | CC0 |
| Planchado | Philips steam iron model HD 1256 A | Peter Häll | CC0 |
| Lavado en seco | Dry Cleaners - interior, West Miami, Florida, July 2022 | Phillip Pessar | CC BY 2.0 |
| Panel del administrador | Towels on shelves 01 | Ата | CC0 |
| Pantallas vacías | Plastic laundry basket | Dodger67 | CC0 |

El logo de LavaCampus es el entregado por la autora del proyecto.

## Reflexión

- **¿Qué mejoró la IA y qué no?** Aceleró el trabajo repetitivo (consultas, ViewModels, pantallas y pruebas) y propuso validaciones en las que no había pensado, como rechazar un pedido ajeno. No entendió sola el contexto del negocio: hubo que decirle qué cuenta como "atrasado" y que el descuento es solo del lavado.
- **¿Qué error cometió?** El descuento no se restaba en los ingresos por servicio del Panel, y la diferencia solo se vio gracias a una prueba automática.
- **¿Qué aprendí sobre MVVM, Room y reglas de negocio?** Que una regla debe vivir en un solo lugar para que resumen, Repository y reportes coincidan. Que al cambiar una entidad hay que subir la versión de la base y escribir una migración para no perder datos. Y que las pruebas automáticas encuentran errores que a simple vista no se ven.

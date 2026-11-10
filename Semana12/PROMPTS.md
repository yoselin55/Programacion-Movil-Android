# PROMPTS.md — Mejora con IA (rama `MyCar-IA`)

Cada entrada es una consulta a la IA: qué se pidió, qué respondió, qué usamos y qué aprendimos.
Las tres consultas se hicieron en orden y cada una terminó en su propio commit.

## Consulta 1 — Diseño, acceso y navegación
- Fecha: 2026-11-09
- Herramienta: Claude Code (Claude Sonnet 5.5)
- Commit: `feat: rediseño con paleta, login, perfil y menú lateral`
- Prompt:

  > Estoy en la rama `MyCar-IA` de mi app **Mycar** (Kotlin + Jetpack Compose + MVVM + Retrofit + Room + Firebase Auth/Firestore). La fase 1 ya está terminada en `main` y cumple el laboratorio. No cambies lo que ya cumple.
  >
  > Dale el **diseño final** a la app, respetando la arquitectura (composables sin lógica, ViewModel sin `Context`, Repository como interface, `UiState`):
  > 1. Usa mi **logo** (imagen adjunta) y esta **paleta**: modo oscuro con `#11120D` (Smoky Black) y `#565449` (Olive Drab); modo claro con `#D8CFBC` (Bone) y `#FFFBF4` (Floral White).
  > 2. **Modo claro y oscuro**: sigue al sistema y se cambia desde una sección **Perfil**.
  > 3. **Login elegante** con mi foto de autos de fondo, correo y contraseña, «Recordarme», «¿Olvidaste tu contraseña?» y **registro** con confirmar contraseña. Un solo login para usuario y administrador; todo el que se registra queda como **usuario**.
  > 4. **Menú lateral** y **barra inferior** (Inicio, Autos, Perfil), sin repetir secciones entre los dos.
  > 5. **Inicio** con una portada, un buscador y las marcas.
  > 6. **Perfil** con foto, datos editables y el selector de apariencia.
  > 7. La **notificación local** debe hablar de los autos («Ya están los últimos autos. ¡Entra y míralos!»).
  > 8. **Íconos, no emojis**. Que compile y que Lint no marque errores.

- Qué respondió (resumen): Definió los esquemas de color claro y oscuro de Material 3 con la paleta pedida, una tipografía con títulos en serif, y guardó la preferencia de tema en `SharedPreferences` detrás de un `TemaRepository`. Armó `LoginScreen` con un fondo de autos, el logo y los campos con íconos, validaciones en `LoginViewModel` y el registro con rol fijo `usuario`. Creó el menú lateral (`ModalNavigationDrawer`), la barra inferior y el `AppNav` con rutas principales. Agregó el perfil con foto elegida del sistema (`PickVisualMedia`, sin permisos) y datos editables en Firestore.
- Qué usamos / qué cambiamos y por qué:
  - La IA sugirió **Coil 3.6.3** para las imágenes, pero daba error de compatibilidad con Kotlin 2.2 de este proyecto; se cambió a **Coil 3.3.0**.
  - Al revisar el login en el emulador, el título salía **negro sobre fondo oscuro** y el botón quedaba **pegado** al campo de contraseña. Se corrigieron indicando el color del texto y agregando espacio.
  - En el modo oscuro el texto usa Bone y Floral White (de la misma paleta); el rojo del logo se usa solo para errores.
  - El correo que escribe la persona se limpia de espacios y caracteres invisibles: el teclado a veces los agrega y Firebase respondía «formato incorrecto», pero la app mostraba «contraseña incorrecta».
  - Las notificaciones funcionan con la app abierta o en segundo plano; con la app cerrada harían falta notificaciones push (Firebase Cloud Messaging), que no se usaron.
- Qué aprendimos:
  - Que hay que **ver la app en los dos modos**: un texto sin color explícito se ve bien en uno y se pierde en el otro.
  - Que antes de subir la versión de una librería conviene comprobar con qué versión de Kotlin fue compilada.
  - Que un mensaje de error genérico esconde la causa real: registrar el código de error de Firebase permitió encontrar el problema del correo.

## Consulta 2 — Catálogo de autos desde la API
- Fecha: 2026-11-10
- Herramienta: Claude Code (Claude Sonnet 5.5)
- Commit: `feat: catálogo de autos desde la API con fotos, logos y detalle`
- Prompt:

  > Los autos en venta deben salir de la **API de NHTSA** que ya usamos, no de autos que registre el administrador. Usa los **dos servicios**: las marcas y los **autos de cada marca**.
  > 1. Limita el catálogo a **10 marcas modernas** (Tesla, Toyota, Hyundai, Kia, Ford, Chevrolet, BMW, Mercedes-Benz, Audi, Porsche) y **hasta 8 autos de 2024 por marca**. Las 195 marcas de la API siguen guardándose en Room, pero en «Marcas» solo se ven estas.
  > 2. Cada auto tiene **mi foto** (las imágenes que te paso, con el nombre escrito abajo; asígnalas a su marca y modelo y recorta el nombre). Si un auto no tiene foto, que muestre un ícono.
  > 3. Muestra el **logo de cada marca** en la lista y el nombre y la descripción del auto tomados de una fuente libre.
  > 4. **Buscador** por marca o auto («No encontrado» si no hay), filtros por marca y **Favoritos** de autos y de marcas.
  > 5. **Detalle del auto** con la foto completa, miniaturas, etiquetas y pestañas «Acerca de», «Galería» y «Especificaciones».
  > 6. Room sigue siendo la fuente única de verdad de las marcas; el modo sin conexión debe seguir funcionando.

- Qué respondió (resumen): Creó `CatalogoAutoRepository`, que combina las marcas de Room con los autos que pide a la API por marca (`GetModelsForMakeIdYear`, por número de marca para evitar remolques y motos) y los guarda en memoria. Armó `FotosLocales` para relacionar cada foto con su marca y modelo, un buscador y filtros en `AutosScreen`, el detalle con pestañas y una tarjeta de auto con el precio de referencia. Para las descripciones usó Wikipedia y para los logos Wikidata.
- Qué usamos / qué cambiamos y por qué:
  - **Las fotos son las nuestras**: la IA primero buscó fotos en Wikipedia y Commons, pero salían logos, edificios o autos equivocados; se descartó y se usan solo las imágenes que aportamos. No se descargó nada de Pinterest ni de Google por derechos de autor.
  - La API solo da nombres: el **precio es de referencia (simulado)** y el mismo para el mismo auto.
  - Pedir los autos por nombre de marca traía basura (remolques de Ford, motos de BMW); se pidió por **número de marca** y se filtraron los nombres raros.
  - Los **logos** salen de Wikidata (propiedad «logo»); la búsqueda en Commons devolvía logos equivocados. Cada logo se guarda en el teléfono para no repetir la consulta. Audi no tenía, así que se usó el logo que aportamos.
  - Wikipedia limita las consultas muy seguidas (error 429); se pidió de a pocas y solo lo que se ve en pantalla.
  - El emulador perdió el DNS: se abre con DNS fijo (`iniciar-emulador.bat`).
- Qué aprendimos:
  - Que una API pública rara vez trae todo lo que la interfaz necesita (fotos, precios): hay que decidir qué es dato real y qué es de referencia, y decirlo.
  - Que conviene probar los datos reales de la API antes de armar la pantalla.
  - Que las fuentes gratuitas tienen límites de uso y hay que respetarlos.
  - Que la fuente única de verdad (Room) permitió cambiar de dónde salen los autos sin rehacer el resto.

## Consulta 3 — Ventas y administración
- Fecha: 2026-11-10
- Herramienta: Claude Code (Claude Sonnet 5.5)
- Commit: `feat: carrito, citas, pedidos, promociones y panel de administrador`
- Prompt:

  > Termina la parte de **ventas** y el **panel del administrador**:
  > 1. **Carrito**: «Agregar al carrito» avisa «Se agregó al carrito» y lleva al carrito, con el total y un botón de compra.
  > 2. **Comprar** pregunta si la persona va a **recoger su auto** y qué día. El pedido pasa por *En proceso → Pagado → Comprado*, o *No recogido* si no llegó. **Mis pedidos** muestra el seguimiento con animaciones.
  > 3. **Agendar cita** en un formulario: día, hora y comentario. **Dos personas no pueden tomar el mismo horario.** Cuando la fecha pasa, la cita aparece como *Cumplida*.
  > 4. **Administrador**: ve cuántos usuarios hay y quiénes son; cambia el estado de los pedidos y confirma las citas (ve quién y para cuándo); **activa o desactiva marcas y autos** (lo desactivado no lo ve nadie más); crea **promociones** de descuento por mes (3 de ejemplo), que se pueden activar o desactivar.
  > 5. **Notificaciones**: al administrador cuando alguien compra o agenda; al usuario cuando cambia el estado de su pedido o le confirman la cita.
  > 6. **Reglas de Firestore** seguras (todo lo del administrador solo lo escribe el admin) y **pruebas unitarias**. Mantén el panel con totales, sincronizar y limpiar caché.
  > 7. Limpia el código que ya no se use, sin cambiar lo que hace.

- Qué respondió (resumen): Creó las colecciones `pedidos`, `horarios`, `desactivados` y `promociones`, con una interface `AutoRepository` que oculta Firestore. Para que nadie repita un horario guarda cada cita junto con un documento `horarios/{fecha_hora}` en un solo `batch`: como un documento solo se puede crear una vez, el segundo intento falla. Armó el carrito, la compra con día de recojo, `PedidosScreen` con una barra de progreso animada, el formulario `AgendarCitaScreen`, las pantallas del administrador (usuarios, catálogo, promociones, pedidos y citas) y las reglas que limitan cada colección. `AvisosViewModel` escucha los cambios de Firestore y muestra las notificaciones.
- Qué usamos / qué cambiamos y por qué:
  - El **pago sigue siendo una simulación**: solo cambia un estado y no se piden datos de tarjeta; en la pantalla ya no se escribe «compra de prueba».
  - Los **usuarios nuevos siempre quedan como `usuario`**: las reglas rechazan cualquier otro rol. El administrador se crea a mano en la consola de Firebase.
  - Las promociones activas bajan el precio de los autos (se aplica la de mayor descuento, no se suman).
  - Se quitó la búsqueda de fotos en Commons, la cita antigua del detalle y los textos y colores sin uso.
  - La IA no puede publicar las reglas de Firestore: las pegamos nosotros en la consola.
- Qué aprendimos:
  - Que la seguridad real está en las **reglas de Firestore**, no en esconder botones.
  - Que un documento con identificador fijo y un `batch` sirven para impedir duplicados sin usar un servidor.
  - Que `addSnapshotListener` dentro de `callbackFlow` convierte los cambios de Firestore en un `Flow`, y que se cierra con `awaitClose`.
  - Que un `PERMISSION_DENIED` en el log suele significar que faltan reglas publicadas, no que el código esté mal.
  - Que un repositorio falso permite probar el `ViewModel` sin red ni notificaciones reales.

## Consulta 4 — Entrega a domicilio, panel propio del administrador e icono

- Commit: `feat: entrega a domicilio, panel propio del administrador e icono de la app`
- Prompt:

  > Corrige y mejora estas partes:
  > 1. El **icono de la notificación** y de la app debe ser el logo de MyCar, no el robot verde de Android.
  > 2. **Comprar** da el error «No se pudo registrar la compra». Arréglalo y cambia la pregunta: en vez de «Todavía no», la persona elige **Recoger en la agencia** (y el día) o **Envío a mi casa**, escribiendo la dirección o usando su **ubicación actual**.
  > 3. El modo oscuro y claro se queda en **Perfil**.
  > 4. En el administrador, «Pedidos y citas» sale vacío con «No se pudieron cargar los datos» aunque hay una cita. Cada lista debe cargar por separado.
  > 5. El administrador debe tener **su propio panel como inicio** y su propia barra inferior (Panel, Pedidos, Catálogo, Promos, Perfil) para activar o desactivar promociones, autos y marcas.
  > 6. Cuando un usuario compra o agenda, el administrador recibe la **notificación en tiempo real**.

- Qué respondió (resumen): Cambió el icono adaptable por el logo, agregó el campo `direccion` al pedido y un diálogo con las dos opciones de entrega (la ubicación usa el permiso solo cuando se toca el botón). Separó la carga de pedidos y citas en dos flujos, de modo que un fallo en una no oculte la otra, y dio al administrador su propia navegación y menú.
- Qué usamos / qué cambiamos y por qué:
  - El error de compra y las listas vacías venían de `PERMISSION_DENIED`: faltaba publicar las reglas nuevas de `firestore.rules`.
  - Las notificaciones en tiempo real funcionan con la app abierta o en segundo plano (listeners de Firestore); para app cerrada haría falta FCM, que queda fuera del alcance.
- Qué aprendimos:
  - Que un fallo de permisos puede parecer un error de la interfaz.
  - Que conviene pedir un permiso sensible (ubicación) solo en el momento en que la persona lo usa.

## Consulta 5 — Compra a pantalla completa, fotos animadas y panel del administrador

- Commits: `fix: compra en pantalla completa con envio o recojo y fecha`, `feat: carrusel de fotos con movimiento en el panel del administrador y promociones`
- Prompt:

  > 1. **Comprar ahora** no muestra nada. Haz que funcione: la persona elige **recoger en la agencia** o **envío a su casa**. Si es envío, escribe su dirección (o usa su ubicación); en los dos casos **elige la fecha** (de recojo o de envío). La compra se registra sola y queda en su **historial de pedidos**; el administrador ve **quién compró qué auto**.
  > 2. Usa mis fotos de autos en el **administrador** y en las **promociones**, con movimiento (fotos que cambian solas, zoom lento, números que suben) para que se vea más elegante e interactivo.
  > 3. El registro pide **contraseña y confirmar contraseña**.

- Qué respondió (resumen): Cambió la ventana emergente de compra por una **pantalla completa** (`DialogoCompra`) con las dos opciones, dirección o ubicación, y selector de fecha; al comprar lleva a *Mis pedidos*. Creó `CarruselFotos` (fundido entre fotos y zoom lento con `Animatable`) y lo puso en el panel, Pedidos y citas y Promociones; las tarjetas de promoción del Inicio ahora llevan foto. Los totales del panel suben con `animateIntAsState`. La confirmación de contraseña ya existía al crear la cuenta.
- Qué usamos / qué cambiamos y por qué:
  - Una ventana emergente puede no responder si el emulador está lento; una pantalla normal es más fiable.
  - Se recortó la marca de agua de una de las fotos.
  - La compra sigue siendo ficticia: no se piden datos de pago ni se valida la dirección.
- Qué aprendimos:
  - Que animar con `Crossfade`, `Animatable` y `animateIntAsState` da movimiento sin librerías extra.
  - Que conviene que un botón nunca quede apagado sin explicación (el primer día ya viene elegido).

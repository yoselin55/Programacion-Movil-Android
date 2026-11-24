# MuroCampus

**Curso:** Programación en Móviles · **Docente:** Juan José León Suiyon
**Alumna:** Flores Quispe Yoselin Fabiola · **Sección:** 4 ciclo-C24-C · TECSUP Santa Anita

## Descripción

MuroCampus es una app Android (Kotlin + Jetpack Compose) que consume el servicio web REST
[JSONPlaceholder](https://jsonplaceholder.typicode.com/). Permite:

- **Listar** las publicaciones (`GET posts`).
- Ver el **detalle** de una publicación con sus **comentarios** (`GET posts/{id}` y `GET posts/{id}/comments`).
- **Filtrar** por usuario (`GET posts?userId=2`).
- **Crear** una publicación nueva (`POST posts`).
- Comportarse bien **sin internet**: muestra un mensaje de error y el botón *Reintentar*.

Tecnologías: Retrofit, OkHttp (con `HttpLoggingInterceptor`), kotlinx.serialization, ViewModel,
StateFlow y corrutinas. Arquitectura **MVVM + Repository**.

## Estructura de paquetes

```
com.flores.murocampus/
├── MainActivity.kt
├── model/
│   └── Modelos.kt              Post, Comentario, NuevoPost (+ Usuario)
├── data/
│   ├── remote/
│   │   ├── PostApi.kt          interface de Retrofit
│   │   └── RetrofitClient.kt   cliente único con OkHttp y conversor JSON
│   └── PostRepository.kt       interface + PostRepositoryRemote (devuelve Result)
├── viewmodel/
│   ├── PostsViewModel.kt       lista, detalle, filtro y crear
│   ├── PostsViewModelFactory.kt
│   └── UiState.kt              Cargando / Exito / Error + mensajeDe()
└── ui/
    ├── Componentes.kt          TarjetaPost, Cargando, ErrorConReintento
    ├── PantallaLista.kt        lista + chips de filtro
    ├── PantallaDetalle.kt
    ├── PantallaCrear.kt
    └── theme/
```

## Diagrama MVVM + Repository

```
View (PantallaLista, PantallaDetalle, PantallaCrear)
   | eventos: cargar, abrir, filtrar, publicar      ^ collectAsStateWithLifecycle()
   v                                                |
ViewModel (PostsViewModel) --- StateFlow<UiState> (Cargando / Exito / Error)
   | viewModelScope.launch
   v
PostRepository (interface)  <- devuelve Result<...>
   -> PostRepositoryRemote (captura IOException, HttpException...)
   |
   v
PostApi (Retrofit) --- OkHttp ---> https://jsonplaceholder.typicode.com/
```

Reglas que se cumplen: el ViewModel solo conoce la interface `PostRepository` (no usa Retrofit,
`PostApi` ni `Context`), y ningún Composable llama a la API.

## Capturas

| Lista | Detalle |
|:---:|:---:|
| ![Lista](capturas/lista.png) | ![Detalle](capturas/detalle.png) |

| Filtro por usuario | Crear publicación |
|:---:|:---:|
| ![Filtro](capturas/filtro.png) | ![Crear](capturas/crear.png) |

| Error de conexión |
|:---:|
| ![Error de conexión](capturas/error.png) |

## Pruebas de errores (Paso 10)

| Prueba | Cómo provocarla | Resultado esperado |
|---|---|---|
| Sin internet | Modo avión y abrir la app | "Sin conexión a internet" y botón Reintentar |
| Reintentar | Desactivar el modo avión y pulsar Reintentar | Carga la lista |
| Recurso inexistente | Cambiar `posts/{id}` por `posts/9999` en el detalle (temporal) | "Error del servidor (404)" |
| JSON inesperado | Quitar un campo de `Post` que sí llega (temporal) | Funciona (por `ignoreUnknownKeys`); si se agrega uno que no llega, "Respuesta inesperada" |
| Cancelación | Abrir el detalle y volver rápido | Sin errores en Logcat |
| Lista y detalle | Abrir varias publicaciones | Siempre 5 comentarios |
| Filtro | Elegir cada usuario | Siempre 10 publicaciones |
| Crear | Enviar una publicación | "Publicado con id 101" |
| Código | Revisar `viewmodel/` | Sin Retrofit, `PostApi` ni `HttpException` en `PostsViewModel` |

> Nota: `HttpException` aparece solo en `UiState.kt` (función `mensajeDe`), archivo entregado en el
> proyecto base; el `PostsViewModel` no la usa.

## Para pensar

**1. ¿Qué diferencia hay entre GET y POST? ¿Por qué crear lleva `@Body` y por usuario lleva `@Query`?**
`GET` solo lee datos y no lleva cuerpo; `POST` crea un recurso y envía los datos en el cuerpo de la
petición (JSON), por eso `crear` usa `@Body`. `porUsuario` usa `@Query` porque es un filtro opcional
que se agrega a la URL (`posts?userId=2`) sobre el mismo recurso; en cambio `@Path` identifica un
recurso concreto (`posts/1`).

**2. ¿Por qué la ruta es `"posts"` y no `"/posts"`?**
Retrofit une la `baseUrl` con la ruta relativa. Una barra inicial indica una ruta absoluta desde la
raíz del servidor y reemplazaría el camino que tenga la URL base; sin barra, la ruta se suma a la base.

**3. ¿Por qué el Repository devuelve `Result` en vez de dejar que la excepción llegue al ViewModel?**
Así el ViewModel no necesita conocer Retrofit ni sus excepciones, y el error se convierte en un valor
explícito que obliga a manejarlo. Una excepción sin capturar en una corrutina puede cerrar la app.

**4. ¿Qué pasa si olvidas el permiso `INTERNET`? ¿Y si olvidas `ignoreUnknownKeys` y el servidor agrega un campo?**
Sin el permiso la app compila pero nunca se conecta y muestra un error de red aunque haya internet.
Sin `ignoreUnknownKeys`, si el servidor agrega un campo que el modelo no tiene, kotlinx.serialization
lanza `SerializationException` y la app muestra "Respuesta inesperada" aunque los datos útiles lleguen.

**5. ¿Por qué la pantalla usa un `when` sobre `UiState` y no un `if` con tres variables?**
`UiState` es una `sealed interface`: los estados son mutuamente excluyentes y el compilador obliga a
cubrir todos. Con tres booleanos podrían darse combinaciones imposibles (por ejemplo, cargando y con
error a la vez).

**6. JSONPlaceholder no guarda los POST. ¿Cómo cambiaría la app con un servicio real?**
Tras crear la publicación habría que refrescar la lista para verla. También harían falta
autenticación (token en un interceptor), el `userId` real del usuario, manejo de errores del servidor
(400, 401, 422) y, probablemente, paginación o caché. Gracias a la arquitectura, el cambio se
concentra en la capa `data`, y las pantallas y el ViewModel casi no se tocan.

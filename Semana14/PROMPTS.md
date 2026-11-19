# PROMPTS.md · Fase 2 (con IA)

Herramienta usada: **Claude Code (Anthropic)**, en lugar de Gemini en Android Studio. La IA propone, yo decido: leí el código generado, lo probé en el emulador y comprobé que no hay `GlobalScope`, `runBlocking` ni `Thread.sleep`, y que la `CancellationException` siempre se vuelve a lanzar.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Agrega la parte con IA: elige 3 mejoras, mejora la interfaz con imágenes, otra letra, textos más grandes y que se entienda mejor; usa estos colores pero sin degradados» | Propuso 3 mejoras de la lista (progreso desde el Repository, progreso en los primos, comparación de tiempos) y un rediseño con paleta verde, crema y amarillo | Dejé el rediseño dentro del commit 1 de la Fase 2 para no hacer un cuarto commit | La regla pide mínimo 3 commits con mensajes que digan la mejora |
| «Mejora 1: mueve la tarea con progreso al Repository como Flow» | `tareaConProgreso()` con `flow { emit }` y `flowOn(Dispatchers.Default)`; el ViewModel solo hace `collect` | Mantuve el Log de hilo en el Repository y en el ViewModel | Así Logcat sigue mostrando main, worker, main |
| «Mejora 2: muestra el porcentaje al contar primos» | Parámetro `alAvanzar: (Float) -> Unit` en `contarPrimos`, llamado cada 1 % | Lo hice opcional (`= {}`) para no romper otras llamadas | El avance se informa desde `Dispatchers.Default`; `StateFlow` es seguro entre hilos |
| «Mejora 3: tabla que acumule los tiempos de Una por una y A la vez» | `Tiempos` en `model`, lista de tiempos en el ViewModel y tabla con barras | Añadí el ahorro en % entre ambos modos | Hace visible por qué `async` es más rápido |
| «Cambia la letra y agranda el texto» | Usó la fuente Poppins (licencia abierta) en `res/font` y tamaños de 15 a 30 sp | Descarté la fuente por descarga en tiempo de ejecución | Funciona sin internet y sin Google Play Services |
| «Tiempo máximo con `withTimeout`» (mejora de la lista) | Descartada | No la implementé | Las cargas tardan 3 s y el límite es 2 s: fallarían siempre |

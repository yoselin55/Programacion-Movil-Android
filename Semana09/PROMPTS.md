# PROMPTS.md · Fase 2 (con IA)

Rama: `MuroCampus-IA`. Cada fila es un prompt importante. La IA propone y yo decido: reviso todo lo
que genera, lo pruebo y corrijo lo que rompe las reglas (nada de Retrofit ni `HttpException` en el
ViewModel, y las pantallas nunca llaman a la API).

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Pasemos a la parte con IA: implementa las mejoras del PDF y mejora también la interfaz con el diseño de referencia (barra lateral rosa, perfil con nombre y foto)». | Propuso 8 commits: tema rosa y barra lateral, interceptor `Accept`, búsqueda, perfil, autor en publicaciones, borrar (DELETE), editar (PATCH) y este archivo. Dejó fuera Room y paginación por ser las más pesadas. | Mantuve el plan, pero usé la rama `MuroCampus-IA` (mi convención en otros laboratorios) en lugar de `mejora-ia`. | Mínimo 3 mejoras y una debe cambiar el Repository o la API: aquí lo hacen borrar y editar. |
| (commit 1) «Cambia el diseño al de la referencia, con imágenes en la navegación». | Reemplazó la barra azul por una barra lateral rosa con degradado, íconos de Material y tarjetas redondeadas con avatar. | Usé íconos vectoriales (`material-icons-core`) en lugar de imágenes bitmap, y la barra lateral solo tiene los destinos que ya existen (Inicio y Crear). | Los íconos vectoriales escalan bien y no pesan; no dejo botones que todavía no hacen nada. |

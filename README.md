# Guía de Estructuración de Prompts para Programadores

Esta guía define la estructura estándar para construir *prompts* efectivos dirigidos a modelos de Inteligencia Artificial en el ámbito del desarrollo de software.

---

## 📌 Componentes Clave de un Prompt para Programadores

* **Rol / Persona:** Define el nivel de seniority y especialidad técnica del modelo (ej. *Senior Backend Developer en Python/FastAPI*).
* **Contexto:** Explica el escenario del proyecto, versión del lenguaje, dependencias o datos de entrada.
* **Tarea Principal:** La instrucción clara y directa de lo que debe generar o solucionar (ej. *Refactorizar, Crear test unitario, Detectar un bug*).
* **Restricciones y Reglas:** Límites explícitos sobre lo que se debe y no se debe hacer (ej. *Sin dependencias externas, Complejidad temporal inferior a O(n²), Seguir guía de estilo PEP8*).
* **Formato de Salida:** La forma exacta en que necesitas el resultado (ej. *Solo bloque de código sin texto, Código + 3 viñetas explicativas*).

---

## 🛠️ Plantilla Base (Meta-Prompt Reutilizable)

```markdown
[ROL] 
Eres un desarrollador Senior especializado en [Lenguaje / Framework].

[CONTEXTO] 
Estoy trabajando en [descripción breve del módulo/sistema]. El stack actual usa [versión/tecnologías].

[TAREA] 
[Acción específica, ej. Escribe una función que reciba X y devuelva Y].

[Insertar código base o interfaz si aplica]

[RESTRICCIONES]
- Cumplir con las buenas prácticas de [Estándar de código].
- Manejar casos borde como [null, arrays vacíos, timeouts].
- No utilizar librerías de terceros adicionales.

[FORMATO DE SALIDA] 
Devuelve únicamente el código en un bloque explicativo con comentarios Inline mínimos.
```

---

## 🚀 Ejemplo Aplicado en Producción

```markdown
Rol: Ingeniero Senior de Node.js y TypeScript.

Contexto: Tengo un microservicio Express que procesa archivos JSON grandes y bloquea el Event Loop.

Tarea: Refactoriza la función adjunta para procesar los datos de forma asíncrona usando Streams o Worker Threads.

Restricciones:
- Mantener compatibilidad con TypeScript 5.x.
- Incluir manejo explícito de errores con bloques try/catch.
- Evitar el uso de la memoria heap por encima de 100 MB.

Formato: Código refactorizado y una lista de 3 puntos explicando los cambios de rendimiento.
```

---

## 🤖 Prompt aplicado al proyecto (Carrito de Compras con IA)

Este laboratorio (Semana02/lab-manual02, rama `Laboratorio-IA`) parte del carrito de compras hecho manualmente y lo mejora aplicando los 4 pilares de la Programación Orientada a Objetos, usando la estructura de prompt descrita arriba:

```markdown
[ROL]
Eres un desarrollador Senior especializado en Kotlin y diseño orientado a
objetos (POO).

[CONTEXTO]
Tengo un programa de consola en Kotlin que simula un carrito de compras:
calcula subtotal, IGV (18%), total y descuento por tramos (10% si el total
supera 5000, 5% si supera 3000), muestra el detalle de productos, permite
buscar un producto por nombre y eliminarlo. Actualmente todo funciona con
datos quemados en el código (cliente y productos ya escritos) y con
funciones sueltas, sin clases propias más allá de un data class simple.

[TAREA]
Refactoriza y mejora este proyecto aplicando los 4 pilares de la
Programación Orientada a Objetos, y hazlo interactivo por consola (usando
readLine()) en vez de datos hardcodeados. Conserva todas las
funcionalidades actuales.

Aplica los pilares así:

1. Abstracción: crea una clase abstracta base para los productos, con las
   propiedades comunes y un método abstracto (por ejemplo, para calcular el
   importe o el descuento) que cada subtipo implemente a su manera.

2. Encapsulamiento: las propiedades deben ser privadas/protegidas, expuestas
   mediante getters públicos o propiedades de solo lectura, con validación
   (precio y cantidad no pueden ser negativos ni cero; si el usuario ingresa
   un valor inválido, debe mostrarse un mensaje claro y volver a pedirlo).

3. Herencia: crea 2 o 3 subclases que representen categorías reales de
   productos (por ejemplo electrónicos, accesorios, importados), cada una
   heredando las propiedades comunes y agregando algún atributo propio si
   aplica.

4. Polimorfismo: cada subclase debe sobrescribir el cálculo de importe o
   descuento de forma distinta, y el carrito debe procesar la lista de
   productos de forma polimórfica, sin importar la subclase concreta de
   cada uno.

El programa debe pedir por consola el nombre del cliente que lo está
ejecutando (ya no debe estar fijo en el código) y mostrar un menú en bucle
con opciones para: agregar producto (pidiendo categoría, nombre, precio y
cantidad), ver el detalle del carrito, buscar producto, eliminar producto,
ver totales (subtotal, IGV, descuento, total final) y salir.

[RESTRICCIONES]
- El programa debe seguir siendo de consola (se ejecuta por terminal), no
  debe convertirse en una app con interfaz gráfica.
- No uses librerías externas adicionales.
- Maneja casos borde: entradas no numéricas, precio o cantidad menores o
  iguales a cero, búsqueda/eliminación de un producto que no existe.
- Mantén las reglas de negocio actuales: IGV 18%, descuento 10% si el total
  supera 5000, 5% si supera 3000, sin descuento por debajo de eso.

[FORMATO DE SALIDA]
Código Kotlin completo, listo para reemplazar la lógica actual, seguido de
una lista de máximo 5 viñetas explicando qué clase representa cada pilar de
POO aplicado.
```

### Resultado aplicado

El desarrollo se dividió en 6 commits incrementales, cada uno dejando el proyecto compilando y funcionando:

1. Corrección de estructura base del proyecto.
2. Encapsulamiento de la clase `Producto`.
3. Reestructuración del modelo en el paquete `model` (abstracción + herencia con subclases por categoría).
4. Comportamiento propio por tipo de producto (polimorfismo) y clase `Carrito`.
5. Conversión del programa a interactivo por consola (menú con `readLine()`).
6. Mejoras finales: validaciones y manejo de casos borde.
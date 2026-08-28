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
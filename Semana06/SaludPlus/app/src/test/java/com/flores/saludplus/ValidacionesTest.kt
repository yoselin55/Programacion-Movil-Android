package com.flores.saludplus

import com.flores.saludplus.util.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Relaciones:
// - Prueba el object Validaciones (util/Validaciones.kt)
// - Usa JUnit 4

// Fase 2: pruebas de las reglas de validación de Registro y Login
class ValidacionesTest {

    // ---- Nombre ----

    // Verifica que un nombre y un apellido simples son válidos
    @Test
    fun nombre_juanPerez_esValido() {
        assertNull(Validaciones.errorNombre("Juan Pérez"))
    }

    // Verifica que un nombre de tres palabras con tildes es válido
    @Test
    fun nombre_mariaJoseQuispe_esValido() {
        assertNull(Validaciones.errorNombre("María José Quispe"))
    }

    // Verifica que un solo nombre con un número no es válido (avisa que solo puede tener letras)
    @Test
    fun nombre_conNumero_noEsValido() {
        assertEquals("El nombre solo puede tener letras", Validaciones.errorNombre("Yoseli8"))
    }

    // Verifica que una sola palabra no es válida (falta el apellido)
    @Test
    fun nombre_unaSolaPalabra_noEsValido() {
        assertEquals("Ingresa tu nombre completo", Validaciones.errorNombre("Juan"))
    }

    // Verifica que palabras de una sola letra no son válidas
    @Test
    fun nombre_palabrasDeUnaLetra_noEsValido() {
        assertEquals("Ingresa tu nombre completo", Validaciones.errorNombre("J P"))
    }

    // Verifica que el nombre vacío no es válido
    @Test
    fun nombre_vacio_noEsValido() {
        assertEquals("Ingresa tu nombre completo", Validaciones.errorNombre(""))
    }

    // ---- Teléfono ----

    // Verifica que 9 dígitos que empiezan con 9 son válidos
    @Test
    fun telefono_nueveDigitosConNueve_esValido() {
        assertNull(Validaciones.errorTelefono("987654321"))
    }

    // Verifica que un teléfono que no empieza con 9 no es válido
    @Test
    fun telefono_noEmpiezaConNueve_noEsValido() {
        assertNotNull(Validaciones.errorTelefono("887654321"))
    }

    // Verifica que un teléfono de 8 dígitos no es válido
    @Test
    fun telefono_ochoDigitos_noEsValido() {
        assertNotNull(Validaciones.errorTelefono("98765432"))
    }

    // Verifica que un teléfono con una letra no es válido
    @Test
    fun telefono_conLetra_noEsValido() {
        assertEquals(
            "El teléfono debe tener 9 dígitos y empezar con 9",
            Validaciones.errorTelefono("98765432a")
        )
    }

    // ---- Correo ----

    // Verifica que el correo vacío es válido porque es opcional
    @Test
    fun correo_vacio_esValido() {
        assertNull(Validaciones.errorCorreo(""))
    }

    // Verifica que un correo con usuario, dominio y extensión es válido
    @Test
    fun correo_completo_esValido() {
        assertNull(Validaciones.errorCorreo("juan@correo.com"))
    }

    // Verifica que un correo sin dominio no es válido
    @Test
    fun correo_sinDominio_noEsValido() {
        assertEquals("Correo no válido", Validaciones.errorCorreo("juan@"))
    }

    // Verifica que un correo con dominio sin extensión no es válido
    @Test
    fun correo_sinExtension_noEsValido() {
        assertEquals("Correo no válido", Validaciones.errorCorreo("juan@correo"))
    }

    // Verifica que un correo con espacio y sin @ no es válido
    @Test
    fun correo_conEspacioSinArroba_noEsValido() {
        assertEquals("Correo no válido", Validaciones.errorCorreo("juan correo.com"))
    }

    // Verifica que un dominio mal escrito (gamil.com) sugiere el correcto
    @Test
    fun correo_dominioMalEscrito_sugiereGmail() {
        assertEquals("¿Quisiste decir gmail.com?", Validaciones.errorCorreo("yoselin@gamil.com"))
    }

    // ---- Contraseña ----

    // Verifica que una contraseña de 6 caracteres con letras y números es válida
    @Test
    fun contrasena_letrasYNumeros_esValida() {
        assertNull(Validaciones.errorContrasena("abc123"))
    }

    // Verifica que una contraseña solo con letras no es válida
    @Test
    fun contrasena_soloLetras_noEsValida() {
        assertNotNull(Validaciones.errorContrasena("abcdef"))
    }

    // Verifica que una contraseña solo con números no es válida
    @Test
    fun contrasena_soloNumeros_noEsValida() {
        assertNotNull(Validaciones.errorContrasena("123456"))
    }

    // Verifica que una contraseña de menos de 6 caracteres no es válida
    @Test
    fun contrasena_muyCorta_noEsValida() {
        assertNotNull(Validaciones.errorContrasena("ab1"))
    }

    // Verifica que una contraseña con espacio no es válida
    @Test
    fun contrasena_conEspacio_noEsValida() {
        assertEquals("Mínimo 6 caracteres, con letras y números", Validaciones.errorContrasena("abc 123"))
    }
}

package com.flores.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.murocampus.data.PostRepository
import com.flores.murocampus.model.Comentario
import com.flores.murocampus.model.NuevoPost
import com.flores.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Commit 8: publicacion y comentarios que se muestran juntos en el detalle.
data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

// Commit 7: el ViewModel solo conoce la interface PostRepository (sin Retrofit ni HttpException).
class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    // Commit 8: estado del detalle (publicacion + comentarios).
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    // Commit 10: resultado del POST (null = aun no se envio nada).
    private val _crear = MutableStateFlow<UiState<Post>?>(null)
    val crear: StateFlow<UiState<Post>?> = _crear.asStateFlow()

    // Commit 9: filtro por usuario (null = Todos).
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            // Commit 9: segun el filtro se pide todo (listar) o solo un usuario (porUsuario).
            val filtro = _usuarioFiltro.value
            val resultado = if (filtro == null) repo.listar() else repo.porUsuario(filtro)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // Commit 10: envia la publicacion nueva (userId fijo = 1) y guarda el resultado.
    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _crear.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo, body = texto))
                .onSuccess { _crear.value = UiState.Exito(it) }
                .onFailure { _crear.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // Commit 10: al abrir el formulario se olvida el resultado anterior.
    fun reiniciarCrear() {
        _crear.value = null
    }

    // Commit 9: guarda el filtro elegido y recarga la lista.
    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    // Commit 8: pide la publicacion y sus comentarios a la vez; si una falla, todo es Error.
    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            val (rPost, rComentarios) = coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                post.await() to comentarios.await()
            }
            val error = rPost.exceptionOrNull() ?: rComentarios.exceptionOrNull()
            _detalle.value = if (error != null) {
                UiState.Error(mensajeDe(error))
            } else {
                UiState.Exito(DetalleUi(rPost.getOrThrow(), rComentarios.getOrThrow()))
            }
        }
    }
}

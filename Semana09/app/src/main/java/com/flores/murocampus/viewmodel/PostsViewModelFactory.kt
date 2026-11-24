package com.flores.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flores.murocampus.data.PostRepository

// Commit 7: fabrica que entrega el Repository al ViewModel (archivo [E] del proyecto base).
class PostsViewModelFactory(private val repo: PostRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PostsViewModel(repo) as T
}

package com.flores.mycar

import com.flores.mycar.data.remote.RetrofitClient
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.data.repository.ItemRepositoryImpl

/** Crea la API, la base de datos y los repositorios. Los ViewModel lo reciben por su Factory. */
class AppContainer {
    val itemRepository: ItemRepository by lazy { ItemRepositoryImpl(RetrofitClient.api) }
}

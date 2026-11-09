package com.flores.mycar

import android.content.Context
import com.flores.mycar.data.auth.AuthRepository
import com.flores.mycar.data.auth.FirebaseAuthRepository
import com.flores.mycar.data.local.AppDatabase
import com.flores.mycar.data.remote.RetrofitClient
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.data.repository.ItemRepositoryImpl
import com.flores.mycar.notification.Notificador
import com.flores.mycar.notification.NotificadorAndroid

/** Crea la API, la base de datos y los repositorios. Los ViewModel lo reciben por su Factory. */
class AppContainer(context: Context) {

    private val database: AppDatabase by lazy { AppDatabase.crear(context) }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(RetrofitClient.api, database.itemDao())
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    val notificador: Notificador by lazy { NotificadorAndroid(context) }
}

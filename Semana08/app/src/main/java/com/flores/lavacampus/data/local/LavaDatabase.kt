package com.flores.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flores.lavacampus.data.local.dao.DetalleDao
import com.flores.lavacampus.data.local.dao.PedidoDao
import com.flores.lavacampus.data.local.dao.ServicioDao
import com.flores.lavacampus.data.local.dao.UsuarioDao
import com.flores.lavacampus.data.local.entity.DetallePedido
import com.flores.lavacampus.data.local.entity.Pedido
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario

// Base de datos Room con las 4 tablas. Si cambias una entidad, sube "version"
@Database(
    entities = [Servicio::class, Usuario::class, Pedido::class, DetallePedido::class],
    version = 2,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {
    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao

    companion object {
        // Version 2: los pedidos guardan el descuento por volumen
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pedidos ADD COLUMN descuento REAL NOT NULL DEFAULT 0.0")
            }
        }

        @Volatile private var INSTANCE: LavaDatabase? = null

        // Singleton: una sola instancia para toda la app
        fun obtener(context: Context): LavaDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                LavaDatabase::class.java,
                "lavacampus.db"
            ).addMigrations(MIGRACION_1_2).build().also { INSTANCE = it }
        }
    }
}

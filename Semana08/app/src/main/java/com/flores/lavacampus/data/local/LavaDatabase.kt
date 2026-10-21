package com.flores.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flores.lavacampus.data.local.dao.DetalleDao
import com.flores.lavacampus.data.local.dao.NotificacionDao
import com.flores.lavacampus.data.local.dao.PedidoDao
import com.flores.lavacampus.data.local.dao.ServicioDao
import com.flores.lavacampus.data.local.dao.UsuarioDao
import com.flores.lavacampus.data.local.entity.DetallePedido
import com.flores.lavacampus.data.local.entity.Notificacion
import com.flores.lavacampus.data.local.entity.Pedido
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario

// Base de datos Room con las 5 tablas. Si cambias una entidad, sube "version"
@Database(
    entities = [Servicio::class, Usuario::class, Pedido::class, DetallePedido::class, Notificacion::class],
    version = 3,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {
    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        // Version 2: los pedidos guardan el descuento por volumen
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pedidos ADD COLUMN descuento REAL NOT NULL DEFAULT 0.0")
            }
        }

        // Version 3: tabla de notificaciones (avisos de cambios en los pedidos)
        val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `notificaciones` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`usuarioId` INTEGER NOT NULL, `pedidoId` INTEGER, " +
                        "`titulo` TEXT NOT NULL, `mensaje` TEXT NOT NULL, `tipo` TEXT NOT NULL, " +
                        "`fecha` INTEGER NOT NULL, `leida` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notificaciones_usuarioId` ON `notificaciones` (`usuarioId`)")
            }
        }

        @Volatile private var INSTANCE: LavaDatabase? = null

        // Singleton: una sola instancia para toda la app
        fun obtener(context: Context): LavaDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                LavaDatabase::class.java,
                "lavacampus.db"
            ).addMigrations(MIGRACION_1_2, MIGRACION_2_3).build().also { INSTANCE = it }
        }
    }
}

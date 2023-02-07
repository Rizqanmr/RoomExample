package com.rizqanmr.jetpack.roomexample.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Subscriber::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = SubscriberDatabase.Migration2to3::class),
        AutoMigration(from = 3, to = 4, spec = SubscriberDatabase.Migration3to4::class)
    ]
)
abstract class SubscriberDatabase : RoomDatabase() {

    abstract val subscriberDAO: SubscriberDAO

    @RenameColumn(tableName = "subscriber_data_table", fromColumnName = "subscriber_phone", toColumnName = "phone")
    class Migration2to3 : AutoMigrationSpec
    @RenameColumn(tableName = "subscriber_data_table", fromColumnName = "subscriber_phone", toColumnName = "phone")
    class Migration3to4 : AutoMigrationSpec

    companion object {
        @Volatile
        private var INSTANCE: SubscriberDatabase? = null
            fun getInstance(context: Context):SubscriberDatabase{
                synchronized(this){
                    var instance = INSTANCE
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            SubscriberDatabase::class.java,
                            "subscriber_data_database"
                        ).build()
                        INSTANCE = instance
                    }
                    return instance
                }
            }
    }
}
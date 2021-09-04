package com.example.kotovskdatabase.repositiry.cursor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

import java.sql.SQLException

const val FIRST_COLUMN = "id"
const val SECOND_COLUMN = "name"
const val THIRD_COLUMN = "breed"
const val FOURTH_COLUMN = "age"
const val FIFTH_COLUMN = "created"

const val CATS_DATABASE = "CATS_DATABASE"
const val TABLE_NAME = "cats_table"
private const val DATABASE_VERSION = 1
private const val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS $TABLE_NAME" +
        " ($FIRST_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT, $SECOND_COLUMN VARCHAR(50), $THIRD_COLUMN VARCHAR(50), " +
        "$FOURTH_COLUMN INTEGER, $FIFTH_COLUMN INTEGER);"

typealias CatListener = (List<Cat>) -> Unit

class CursorDataBase(context: Context) : SQLiteOpenHelper(
    context,
    CATS_DATABASE,
    null,
    DATABASE_VERSION
) {

    private val listeners = mutableSetOf<CatListener>()

    private var listOfTopics = mutableListOf<Cat>()


    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_TABLE_SQL)
        } catch (exception: SQLException) {
            Log.e("onCreate", "Exception while trying to create database", exception)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("onUpgrade", "onUpgrade called")
    }

    private fun getCursorWithTopicsRead(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    private fun listenListCat(): Flow<List<Cat>> = callbackFlow {

        val listener: CatListener = {
            trySend(it)
            Log.d("listener", it.size.toString())
        }

        listeners.add(listener)

        awaitClose {
            listeners.remove(listener)
        }
    }

    fun updateList(): List<Cat> {
        listOfTopics = mutableListOf<Cat>()
        getCursorWithTopicsRead().use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(FIRST_COLUMN))
                    val name = cursor.getString(cursor.getColumnIndex(SECOND_COLUMN))
                    val breed = cursor.getString(cursor.getColumnIndex(THIRD_COLUMN))
                    val age = cursor.getInt(cursor.getColumnIndex(FOURTH_COLUMN))
                    val created = cursor.getLong(cursor.getColumnIndex(FIFTH_COLUMN))
                    val person = Cat(id, name, breed, age, created)
                    listOfTopics.add(person)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        listeners.forEach { it(listOfTopics) }
        Log.d("updateList", listOfTopics.size.toString())
        return listOfTopics
    }

    fun getAll() = flow<List<Cat>> {
        updateList()
        emit(listOfTopics)
        listenListCat().collect() {
            Log.d("getAll", it.size.toString())
            emit(it)
        }
    }

    suspend fun save(cat: Cat) {
        val values = ContentValues()
        values.put(SECOND_COLUMN, cat.name)
        values.put(THIRD_COLUMN, cat.breed)
        values.put(FOURTH_COLUMN, cat.age)
        values.put(FIFTH_COLUMN, cat.created)
        writableDatabase.insert(TABLE_NAME, null, values)
        updateList()
    }

    suspend fun delete(cat: Cat): Int {
        writableDatabase.delete(TABLE_NAME, "id = ${cat.id}", null)
        updateList()
        return 1
    }

    suspend fun update(cat: Cat) {
        val updatedValues = ContentValues()
        updatedValues.put(SECOND_COLUMN, cat.name)
        updatedValues.put(THIRD_COLUMN, cat.breed)
        updatedValues.put(FOURTH_COLUMN, cat.age)
        val where = "id = ${cat.id}"
        writableDatabase.update(TABLE_NAME, updatedValues, where, null)
        updateList()
    }

    companion object {

        private var INSTANCE: CursorDataBase? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CursorDataBase(context)
            }
        }

        fun get(): CursorDataBase =
            INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")

    }

}
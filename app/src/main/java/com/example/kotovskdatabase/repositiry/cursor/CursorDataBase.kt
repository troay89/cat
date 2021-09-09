package com.example.kotovskdatabase.repositiry.cursor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.sql.SQLException

const val ID_COLUMN = "id"
const val NAME_COLUMN = "name"
const val BREED_COLUMN = "breed"
const val AGE_COLUMN = "age"
const val CREATED_COLUMN = "created"

const val CATS_DATABASE = "CATS_DATABASE"
const val TABLE_NAME = "cats_table"
private const val DATABASE_VERSION = 1
private const val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS $TABLE_NAME" +
        " ($ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT, $NAME_COLUMN VARCHAR(50), $BREED_COLUMN VARCHAR(50), " +
        "$AGE_COLUMN INTEGER, $CREATED_COLUMN INTEGER);"

typealias CatListener = (List<Cat>) -> Unit

class CursorDataBase(context: Context) : Repository, SQLiteOpenHelper(
    context,
    CATS_DATABASE,
    null,
    DATABASE_VERSION
) {

    private val listeners = mutableSetOf<CatListener>()

    private var listOfTopics = mutableListOf<Cat>()


    private var typeSorting: String = "BY_DATE"


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

    @ExperimentalCoroutinesApi
    private fun listenListCat(): Flow<List<Cat>> = callbackFlow {

        val listener: CatListener = {
            trySend(it)
        }

        listeners.add(listener)

        awaitClose {
            listeners.remove(listener)
        }
    }


    override fun getListCats(typeSort: String) = flow<List<Cat>> {
        typeSorting = typeSort
        updateList(typeSorting)
        emit(listOfTopics)
        listenListCat().collect {
            emit(it)
        }
    }

    override suspend fun save(cat: Cat) {
        val values = ContentValues()
        values.put(NAME_COLUMN, cat.name)
        values.put(BREED_COLUMN, cat.breed)
        values.put(AGE_COLUMN, cat.age)
        values.put(CREATED_COLUMN, cat.created)
        writableDatabase.insert(TABLE_NAME, null, values)
        updateList(typeSorting)
    }

    override suspend fun delete(cat: Cat): Int {
        writableDatabase.delete(TABLE_NAME, "id = ${cat.id}", null)
        updateList(typeSorting)
        return 1
    }

    override suspend fun update(cat: Cat) {
        val updatedValues = ContentValues()
        updatedValues.put(NAME_COLUMN, cat.name)
        updatedValues.put(BREED_COLUMN, cat.breed)
        updatedValues.put(AGE_COLUMN, cat.age)
        val where = "id = ${cat.id}"
        writableDatabase.update(TABLE_NAME, updatedValues, where, null)
        updateList(typeSorting)
    }


    private fun updateList(sortList: String): List<Cat> {
        listOfTopics = mutableListOf()
        when (sortList) {
            "BY_NAME" -> getListCatsSortName()
            "BY_AGE" -> getListCatsSortAge()
            else -> getListCatsSortCreated()

        }
        listeners.forEach { it(listOfTopics) }
        return listOfTopics
    }

    private fun getListCatsSortCreated() {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY created", null)
            .use { cursor ->
                getData(cursor)
                cursor.close()
            }
    }

    private fun getListCatsSortAge() {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY age", null)
            .use { cursor ->
                getData(cursor)
                cursor.close()
            }
    }

    private fun getListCatsSortName() {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY name", null)
            .use { cursor ->
                getData(cursor)
                cursor.close()
            }
    }

    private fun getData(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN))
                val name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN))
                val breed = cursor.getString(cursor.getColumnIndex(BREED_COLUMN))
                val age = cursor.getInt(cursor.getColumnIndex(AGE_COLUMN))
                val created = cursor.getLong(cursor.getColumnIndex(CREATED_COLUMN))
                val cat = Cat(id, name, breed, age, created)
                listOfTopics.add(cat)
            } while (cursor.moveToNext())
        }
    }

    companion object {

        private var INSTANCE: CursorDataBase? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CursorDataBase(context)
            }
        }

        fun get(): CursorDataBase =
            INSTANCE ?: throw IllegalStateException("CursorDataBase must be initialized")

    }

}
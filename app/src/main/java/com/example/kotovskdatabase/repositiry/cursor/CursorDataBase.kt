package com.example.kotovskdatabase.repositiry.cursor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.model.CatEntity
import com.example.kotovskdatabase.repositiry.mapper.DomainToEntity
import com.example.kotovskdatabase.repositiry.mapper.EntityListToDomainList
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

typealias CatListener = (List<CatEntity>) -> Unit

class CursorDataBase(context: Context) : Repository, SQLiteOpenHelper(
    context,
    CATS_DATABASE,
    null,
    DATABASE_VERSION
) {

    private val listeners = mutableSetOf<CatListener>()

    private var listOfTopics = mutableListOf<CatEntity>()


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
    private fun listenListCat(): Flow<List<CatEntity>> = callbackFlow {

        val listener: CatListener = {
            trySend(it)
        }

        listeners.add(listener)

        awaitClose {
            listeners.remove(listener)
        }
    }


    override fun getListCats(typeSort: String) = flow<List<CatDomain>> {
        Log.d("getListCats", "Я в курсорах")
        typeSorting = typeSort
        updateList(typeSorting)
        EntityListToDomainList.map(listOfTopics)
        emit(EntityListToDomainList.map(listOfTopics))
        listenListCat().collect {
            emit(EntityListToDomainList.map(it))
        }
    }

    override suspend fun save(catDomain: CatDomain) {
        Log.d("save", "Я в курсорах")
        val catEntity = DomainToEntity.map(catDomain)
        val values = ContentValues()
        values.put(NAME_COLUMN, catEntity.name)
        values.put(BREED_COLUMN, catEntity.breed)
        values.put(AGE_COLUMN, catEntity.age)
        values.put(CREATED_COLUMN, catEntity.created)
        writableDatabase.insert(TABLE_NAME, null, values)
        updateList(typeSorting)
    }

    override suspend fun delete(catDomain: CatDomain): Int {
        Log.d("delete", "Я в курсорах")
        val catEntity = DomainToEntity.map(catDomain)
        val result = writableDatabase.delete(TABLE_NAME, "id = ${catEntity.id}", null)
        updateList(typeSorting)
        return result
    }

    override suspend fun update(catDomain: CatDomain) {
        val catEntity = DomainToEntity.map(catDomain)
        val updatedValues = ContentValues()
        updatedValues.put(NAME_COLUMN, catEntity.name)
        updatedValues.put(BREED_COLUMN, catEntity.breed)
        updatedValues.put(AGE_COLUMN, catEntity.age)
        val where = "id = ${catEntity.id}"
        writableDatabase.update(TABLE_NAME, updatedValues, where, null)
        updateList(typeSorting)
    }


    private fun updateList(sortList: String): List<CatEntity> {
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
                val cat = CatEntity(id, name, breed, age, created)
                listOfTopics.add(cat)
            } while (cursor.moveToNext())
        }
    }

//    companion object {
//
//        private var INSTANCE: CursorDataBase? = null
//
//        fun initialize(context: Context) {
//            if (INSTANCE == null) {
//                INSTANCE = CursorDataBase(context)
//            }
//        }
//        fun get(): CursorDataBase =
//            INSTANCE ?: throw IllegalStateException("CursorDataBase must be initialized")
//    }

    companion object {
        @Synchronized
        fun getInstance(context: Context): CursorDataBase {
            return CursorDataBase(context)
        }
    }
}
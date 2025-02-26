package mx.edu.utng.appsqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "EmployeeDatabase"
        private val TABLE_CONTACTS = "EmployeeTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
        private val KEY_CANTIDAD = "cantidad"
        private val KEY_DISTANCIA = "distancia"
        private val KEY_CONTRASENA = "contrasena"
        private val KEY_TELEFONO = "telefono"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_CANTIDAD + " INTEGER, "
                + KEY_DISTANCIA + " REAL, "
                + KEY_CONTRASENA + " TEXT, "
                + KEY_TELEFONO + " TEXT)"
                )
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addEmployee(emp: EmpModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_NAME, emp.userName)
        contentValues.put(KEY_EMAIL,emp.userEmail )
        contentValues.put(KEY_CANTIDAD, emp.cantidad)
        contentValues.put(KEY_DISTANCIA, emp.distancia)
        contentValues.put(KEY_CONTRASENA, emp.contrasena)
        contentValues.put(KEY_TELEFONO, emp.telefono)
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewEmployee(): List<EmpModelClass> {
        val empList = mutableListOf<EmpModelClass>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return emptyList()
        }

        if (cursor.moveToFirst()) {
            do {
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                val userName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CANTIDAD))
                val distancia = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_DISTANCIA))
                val contrasena = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTRASENA))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEFONO))

                val emp = EmpModelClass(userId, userName, userEmail, cantidad, distancia, contrasena, telefono)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return empList
    }
    // Método para actualizar los datos de un empleado
    fun updateEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NAME, emp.userName)
            put(KEY_EMAIL, emp.userEmail)
            put(KEY_CANTIDAD, emp.cantidad)
            put(KEY_DISTANCIA, emp.distancia)
            put(KEY_CONTRASENA, emp.contrasena)
            put(KEY_TELEFONO, emp.telefono)
        }
        val success = db.update(TABLE_CONTACTS, contentValues, "$KEY_ID = ?", arrayOf(emp.userId.toString()))
        db.close()
        return success
    }


    // Método para eliminar un empleado
    fun deleteEmployee(empId: String): Long {
        val db = this.writableDatabase
        return db.delete(TABLE_CONTACTS, "$KEY_ID = ?", arrayOf(empId)).toLong()
    }
}
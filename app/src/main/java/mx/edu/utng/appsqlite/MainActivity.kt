package mx.edu.utng.appsqlite

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.widget.ListView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_UPDATE = 1
    }

    //Inicializar SearchView y RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    // Crear la lista de empleados y configurar el adaptador

    private lateinit var employeeAdapter: EmployeeAdapter
    private val employeeList = mutableListOf<Employee>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ahora que setContentView() ha sido llamado, puedes obtener las vistas
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        //

        //
        recyclerView.layoutManager = LinearLayoutManager(this)
        employeeAdapter = EmployeeAdapter(this, employeeList)
        recyclerView.adapter = employeeAdapter


        viewRecord(recyclerView)

        // Configurar el listener del SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                employeeAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                employeeAdapter.filter.filter(newText)
                return true
            }
        })
    }

    // Función para abrir la actividad de detalles del empleado
    fun openEmployeeDetailActivity(position: Int, employee: Employee) {
        val intent = Intent(this, EmployeeDetailActivity::class.java).apply {
            putExtra("EMP_ID", employee.id)
            putExtra("EMP_NAME", employee.name)
            putExtra("EMP_EMAIL", employee.email)
            putExtra("POSITION", position) // Enviar la posición
        }
        startActivityForResult(intent, REQUEST_CODE_UPDATE)
    }

    //method for saving records in database
    fun saveRecord(view: View){
        val id = findViewById<EditText>(R.id.u_id).text.toString()
        val name = findViewById<EditText>(R.id.u_name).text.toString()
        val email = findViewById<EditText>(R.id.u_email).text.toString()

        val databaseHandler: DatabaseHandler= DatabaseHandler(this)
        if(id.trim()!="" && name.trim()!="" && email.trim()!=""){
            val status = databaseHandler.addEmployee(EmpModelClass(Integer.parseInt(id),name, email))
            if(status > -1){
                Toast.makeText(applicationContext,"record save",Toast.LENGTH_LONG).show()
                findViewById<EditText>(R.id.u_id).text.clear()
                findViewById<EditText>(R.id.u_name).text.clear()
                findViewById<EditText>(R.id.u_email).text.clear()
            }
        }else{
            Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
        }
    }
    //method for read records from database in ListView
    fun viewRecord(view: View) {
        val databaseHandler = DatabaseHandler(this)

        val empList = databaseHandler.viewEmployee()
            .map { Employee(it.userId.toString(), it.userName, it.userEmail) }
            .toMutableList()
        Log.d("TIENE O NO","$empList")

        databaseHandler.close() // Cierra la base de datos para evitar fugas de memoria

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        if (::employeeAdapter.isInitialized) {
            employeeAdapter.updateList(empList)
        } else {
            employeeAdapter = EmployeeAdapter(this, empList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = employeeAdapter
        }
    }



    //method for updating records based on user id
    fun updateRecord(view: View){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.updateId) as EditText
        val edtName = dialogView.findViewById(R.id.updateName) as EditText
        val edtEmail = dialogView.findViewById(R.id.updateEmail) as EditText

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateId = edtId.text.toString()
            val updateName = edtName.text.toString()
            val updateEmail = edtEmail.text.toString()
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(updateId.trim()!="" && updateName.trim()!="" && updateEmail.trim()!=""){
                //calling the updateEmployee method of DatabaseHandler class to update record
                val status = databaseHandler.updateEmployee(EmpModelClass(Integer.parseInt(updateId),updateName, updateEmail))
                if(status > -1){
                    Toast.makeText(applicationContext,"record update",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }
    //method for deleting records based on id
    fun deleteRecord(view: View){
        //creating AlertDialog for taking user id
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val dltId = dialogView.findViewById(R.id.deleteId) as EditText
        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setMessage("Enter id below")
        dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->

            val deleteId = dltId.text.toString()
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(deleteId.trim()!=""){
                //calling the deleteEmployee method of DatabaseHandler class to delete record
                val status = databaseHandler.deleteEmployee(EmpModelClass(Integer.parseInt(deleteId),"",""))
                if(status > -1){
                    Toast.makeText(applicationContext,"record deleted",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    // Recibir el resultado de la actividad de detalles del empleado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_UPDATE) {
            // Obtener los datos actualizados
            val updatedId = data?.getStringExtra("UPDATED_ID")
            val updatedName = data?.getStringExtra("UPDATED_NAME")
            val updatedEmail = data?.getStringExtra("UPDATED_EMAIL")
            val position = data?.getIntExtra("POSITION", -1) ?: -1

            if (position != -1) {
                // Crear un nuevo objeto Employee con los datos actualizados
                val updatedEmployee = Employee(updatedId ?: "", updatedName ?: "", updatedEmail ?: "")

                // Actualizar la lista y notificar al adaptador
                employeeAdapter.updateEmployeeAtPosition(position, updatedEmployee)
            }
        }
    }

    fun onButtonClick(view: View) {
        // Llamar a saveRecord
        saveRecord(view)

        // Llamar a viewRecord
        viewRecord(view)
    }
}
package mx.edu.utng.appsqlite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmployeeDetailActivity : AppCompatActivity() {
    private lateinit var editTextId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var databaseHandler: DatabaseHandler

// Usa estos valores en tu vista


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_detail)

        // Inicializar la base de datos
        databaseHandler = DatabaseHandler(this)

        // Asignación de vistas
        editTextId = findViewById(R.id.editTextId)
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)

        // Obtener datos del intent
        val empId = intent.getStringExtra("EMP_ID") ?: ""
        val empName = intent.getStringExtra("EMP_NAME") ?: ""
        val empEmail = intent.getStringExtra("EMP_EMAIL") ?: ""

        // Cargar datos en los campos de texto
        editTextId.setText(empId)
        editTextName.setText(empName)
        editTextEmail.setText(empEmail)

        // Guardar cambios
        // Guardar cambios
        btnSave.setOnClickListener {
            val updatedId = editTextId.text.toString()
            val updatedName = editTextName.text.toString()
            val updatedEmail = editTextEmail.text.toString()

            // Actualiza la base de datos
            val dbHandler = DatabaseHandler(this)
            val employee = Employee(updatedId, updatedName, updatedEmail)
            dbHandler.updateEmployee(employee)

            // Devolver los datos actualizados a la actividad principal
            val resultIntent = Intent().apply {
                putExtra("UPDATED_ID", updatedId)
                putExtra("UPDATED_NAME", updatedName)
                putExtra("UPDATED_EMAIL", updatedEmail)
                putExtra("POSITION", intent.getIntExtra("POSITION", -1)) // Enviar la posición
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Cerrar la pantalla
        }





        // 📌 Eliminar empleado
        btnDelete.setOnClickListener {
            val empId = editTextId.text.toString()

            // Eliminar el registro de la base de datos
            val result = databaseHandler.deleteEmployee(empId)

            if (result > -1) {
                Toast.makeText(this, "Empleado eliminado", Toast.LENGTH_SHORT).show()
                // Notificar a la actividad anterior que el empleado ha sido eliminado
                val resultIntent = Intent().apply {
                    putExtra("DELETE_ID", empId)
                }
                setResult(Activity.RESULT_FIRST_USER, resultIntent)
                finish() // Cerrar la pantalla
            } else {
                Toast.makeText(this, "Error al eliminar el empleado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

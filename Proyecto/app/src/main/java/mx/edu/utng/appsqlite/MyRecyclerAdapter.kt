package mx.edu.utng.appsqlite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerAdapter(
    private val context: Context,
    private val empIds: Array<String>,
    private val empNames: Array<String>,
    private val empEmails: Array<String>,
    private val empList: List<EmpModelClass>,
) : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId: TextView = itemView.findViewById(R.id.textViewId)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = empList[position]

        holder.textViewId.text = empIds[position]
        holder.textViewName.text = empNames[position]
        holder.textViewEmail.text = empEmails[position]

        // 📌 Manejo del clic en el elemento
        holder.itemView.setOnClickListener {
            // Obtener los datos del empleado
            val empId = empIds[position]
            val empName = empNames[position]
            val empEmail = empEmails[position]

            // Crear un Intent para abrir la actividad de detalles del empleado
            val intent = Intent(context, EmployeeDetailActivity::class.java).apply {
                putExtra("EMP_ID", empId)
                putExtra("EMP_NAME", empName)
                putExtra("EMP_EMAIL", empEmail)
            }

            // Iniciar la actividad
            context.startActivity(intent)
        }

        fun updateEmployeeAtPosition(position: Int, updatedId: String, updatedName: String, updatedEmail: String) {
            empIds[position] = updatedId
            empNames[position] = updatedName
            empEmails[position] = updatedEmail
            notifyItemChanged(position) // Notifica que el item específico ha cambiado
        }
    }

    override fun getItemCount(): Int = empIds.size
}

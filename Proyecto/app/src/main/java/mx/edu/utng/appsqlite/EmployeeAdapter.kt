package mx.edu.utng.appsqlite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable


class EmployeeAdapter(
    private val context: Context,
    private var empList: MutableList<Employee>
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>(), Filterable {

    private var filteredEmployeeList: MutableList<Employee> = empList.toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewId: TextView = view.findViewById(R.id.textViewId)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return ViewHolder(view)
    }

    // Método para actualizar un empleado en la lista
    fun updateEmployeeAtPosition(position: Int, updatedEmployee: Employee) {
        if (position >= 0 && position < filteredEmployeeList.size) {
            filteredEmployeeList[position] = updatedEmployee // Actualiza el empleado en la lista filtrada
            notifyItemChanged(position) // Notifica que el ítem ha cambiado
        } else {
            Log.e("EmployeeAdapter", "Índice fuera de rango: $position")
        }
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = filteredEmployeeList[position]

        holder.textViewId.text = employee.id.toString()
        holder.textViewName.text = employee.name
        holder.textViewEmail.text = employee.email



        // 📌 Evita que el adaptador dependa de MainActivity
        holder.itemView.setOnClickListener {
            if (context is MainActivity) {
                context.openEmployeeDetailActivity(position, employee)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("ADAPTER", "Cantidad de elementos: ${filteredEmployeeList.size}")
        return filteredEmployeeList.size  // Usa la lista filtrada
    }

    fun updateList(newList: MutableList<Employee>) {
        empList = newList
        filteredEmployeeList = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: MutableList<Employee> = mutableListOf()
                Log.d("EmployeeAdapter", "Filtrando con: $constraint")

                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(empList)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    Log.d("EmployeeAdapter", "Tamaño de empList: ${empList.size}")
                    for (employee in empList) {
                        Log.d("EmployeeAdapter", "Nombre del empleado: ${employee.name}")
                        if (employee.name.lowercase().contains(filterPattern)) {
                            filteredList.add(employee)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredEmployeeList = (results?.values as List<Employee>).toMutableList()  // Convierte a MutableList
                Log.d("EmployeeAdapter", "Lista filtrada: $filteredEmployeeList")
                notifyDataSetChanged()
            }
        }
    }
}

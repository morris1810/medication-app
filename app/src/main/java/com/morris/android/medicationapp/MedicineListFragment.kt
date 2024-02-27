package com.morris.android.medicationapp

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import androidx.lifecycle.Observer
import java.util.*

private const val TAG = "MedicineListFragment"

class MedicineListFragment : Fragment() {

    interface Callbacks {
        fun onMedicineListItemSelected(medicineId: UUID)
    }

    private var callbacks: Callbacks? = null

    private var adapter: MedicineAdapter? = MedicineAdapter(emptyList())

    private lateinit var medicineRecyclerView: RecyclerView

    private lateinit var addMedicineButton: ImageButton

    private val medicineListViewModel: MedicineListViewModel by lazy {
        ViewModelProviders.of(this).get(MedicineListViewModel::class.java)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine_list, container, false)
        medicineRecyclerView = view.findViewById(R.id.medicine_recycle_list) as RecyclerView
        medicineRecyclerView.layoutManager = LinearLayoutManager(context)
        medicineRecyclerView.adapter = adapter
        addMedicineButton = view.findViewById(R.id.add_medicine) as ImageButton
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medicineListViewModel.medicineListLiveData.observe(
            viewLifecycleOwner,
            Observer { medicines ->
                medicines?.let { Log.i(TAG, "Total amount Medicine found: ${medicines.size}") }
                updateUI(medicines)
            })

    }

    override fun onStart() {
        super.onStart()
        addMedicineButton.setOnClickListener {
            val medicine = Medicine()
            medicineListViewModel.addMedicine(medicine)
            callbacks?.onMedicineListItemSelected(medicine.id)
            true
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(medicines: List<Medicine>) {
        adapter = MedicineAdapter(medicines)
        medicineRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): MedicineListFragment {
            return MedicineListFragment()
        }
    }

    private inner class MedicineHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var medicine: Medicine

        private val medicineNameTextView: TextView = itemView.findViewById(R.id.medicine_name) as TextView
        private val endDateTextView: TextView = itemView.findViewById(R.id.end_date) as TextView

        init {
            itemView.setOnClickListener(this)
        }


        fun bind(medicine: Medicine) {
            val dateFormat: DateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            this.medicine = medicine
            medicineNameTextView.text = medicine.name

            val todayDate = Date()
            val comparingDate = medicine.endDateTime.compareTo(todayDate)
            val isExpired = comparingDate < 0


            if (isExpired) {
                endDateTextView.setTextColor(Color.parseColor("#FF5555"))
                endDateTextView.setTypeface(null, Typeface.BOLD)
            }

            endDateTextView.text = dateFormat.format(medicine.endDateTime)
        }

        override fun onClick(v: View?) {
            callbacks?.onMedicineListItemSelected(medicine.id)
        }
    }

    private inner class MedicineAdapter(var medicines: List<Medicine>) :
        RecyclerView.Adapter<MedicineHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineHolder {
            val view = layoutInflater.inflate(R.layout.list_item_medicine, parent, false)
            return MedicineHolder(view)
        }

        override fun onBindViewHolder(holder: MedicineHolder, position: Int) {
            val medicine = medicines[position]
            holder.bind(medicine)
        }

        override fun getItemCount(): Int = medicines.size

    }


}
package com.morris.android.medicationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var medicineListButton: ImageButton

    private lateinit var medicineSchedule: RecyclerView

    private lateinit var todayDayOfWeekTextView: TextView

    private val medicineListViewModel: MedicineListViewModel by lazy {
        ViewModelProviders.of(this).get(MedicineListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        medicineListButton = findViewById(R.id.medicine_list_button)
        medicineSchedule = findViewById(R.id.schedule)
        todayDayOfWeekTextView = findViewById(R.id.today_day_of_week)

        todayDayOfWeekTextView.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().time)

        medicineListButton.setOnClickListener {
            val intent = Intent(this, MedicineActivity::class.java)
            startActivity(intent)
        }

        medicineSchedule.layoutManager = LinearLayoutManager(this)
        medicineListViewModel.medicineListLiveData.observe(
            this,
            Observer { medicines ->
                val currentDate = Date()
                val calendar = Calendar.getInstance()
                val todayRepeatOption = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> RepeatOption.MONDAY
                    Calendar.TUESDAY -> RepeatOption.TUESDAY
                    Calendar.WEDNESDAY -> RepeatOption.WEDNESDAY
                    Calendar.THURSDAY -> RepeatOption.THURSDAY
                    Calendar.FRIDAY -> RepeatOption.FRIDAY
                    Calendar.SATURDAY -> RepeatOption.SATURDAY
                    Calendar.SUNDAY -> RepeatOption.SUNDAY
                    else -> RepeatOption.NONE
                }

                val filterExpired = medicines.filter { medicine -> medicine.endDateTime.after(currentDate) }
                val filterDayOfWeek = filterExpired.filter { medicine ->
                    (medicine.repeatOption == todayRepeatOption) or (medicine.repeatOption == RepeatOption.EVERY_DAY)
                }

                // Change to same Date
                filterDayOfWeek.forEach { medicine ->
                    calendar.time = currentDate
                    val todayCalendar = Calendar.getInstance()

                    val medicineCalendar = Calendar.getInstance()
                    medicineCalendar.time = medicine.endDateTime

                    calendar.set(
                        todayCalendar.get(Calendar.YEAR),
                        todayCalendar.get(Calendar.MONTH),
                        todayCalendar.get(Calendar.DAY_OF_MONTH),
                        medicineCalendar.get(Calendar.HOUR_OF_DAY),
                        medicineCalendar.get(Calendar.MINUTE),
                    )
                    medicine.endDateTime = calendar.time
                }

                val sortMedicine = filterDayOfWeek.sortedWith(compareBy { it.endDateTime })

                updateSchedule(sortMedicine)

            })
    }

    private fun updateSchedule(medicines: List<Medicine>) {
        val arrayList = arrayListOf<Medicine>()
        arrayList.addAll(medicines)
        val scheduleAdapter = ScheduleAdapter(arrayList)
        medicineSchedule.adapter = scheduleAdapter
    }

    private inner class ScheduleHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var medicine: Medicine

        private val scheduleNameTextView: TextView = itemView.findViewById(R.id.schedule_name) as TextView
        private val scheduleTimeTextView: TextView = itemView.findViewById(R.id.schedule_time) as TextView

        init {
            itemView.setOnClickListener(this)
        }


        fun bind(medicine: Medicine) {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            this.medicine = medicine
            scheduleNameTextView.text = medicine.name
            scheduleTimeTextView.text = dateFormat.format(medicine.endDateTime)
        }

        override fun onClick(p0: View?) {}

    }


    private inner class ScheduleAdapter(var medicines: ArrayList<Medicine>) :
        Adapter<ScheduleHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
            val view = layoutInflater.inflate(R.layout.list_item_schedule, parent, false)
            return ScheduleHolder(view)
        }

        override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
            val medicine = medicines[position]
            holder.bind(medicine)
        }

        override fun getItemCount(): Int = medicines.size

    }

}
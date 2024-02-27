package com.morris.android.medicationapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.UUID

private const val ARG_MEDICINE_ID = "medicine_id"
private const val REQUEST_DATE = 0
private const val DIALOG_DATE = "DialogDate"



class MedicineFragment: Fragment(), DatePickerFragment.Callbacks {

    private lateinit var medicine: Medicine
    private lateinit var medicineNameInput: EditText

    private lateinit var datePickerButton: LinearLayout
    private lateinit var datePickerDay: TextView
    private lateinit var datePickerMonth: TextView
    private lateinit var datePickerYear: TextView

    private lateinit var timePickerButton: LinearLayout
    private lateinit var timePickerHour: TextView
    private lateinit var timePickerMinute: TextView

    private lateinit var repeatOptionInput: Spinner
    private lateinit var remarkInput: EditText
    private lateinit var removeButton: Button

    //Constructor
    private val medicineDetailViewModel: MedicineDetailViewModel by lazy {
        ViewModelProviders.of(this).get(MedicineDetailViewModel::class.java)
    }

    companion object {
        fun newInstance(medicineId: UUID): MedicineFragment {
            val args = Bundle().apply {
                putSerializable(ARG_MEDICINE_ID, medicineId)
            }
            return  MedicineFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medicine = Medicine()
        val medicineId: UUID = arguments?.getSerializable(ARG_MEDICINE_ID) as UUID
        medicineDetailViewModel.loadMedicine(medicineId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine_detail, container, false)
        medicineNameInput = view.findViewById(R.id.medicine_name_input) as  EditText

        datePickerButton = view.findViewById(R.id.date_picker_button) as  LinearLayout
        datePickerDay = view.findViewById(R.id.date_picker_day) as  TextView
        datePickerMonth = view.findViewById(R.id.date_picker_month) as  TextView
        datePickerYear = view.findViewById(R.id.date_picker_year) as  TextView

        timePickerButton = view.findViewById(R.id.time_picker_button) as  LinearLayout
        timePickerHour = view.findViewById(R.id.time_picker_hour) as  TextView
        timePickerMinute = view.findViewById(R.id.time_picker_minute) as  TextView

        repeatOptionInput = view.findViewById(R.id.repeat_option_input) as  Spinner
        remarkInput = view.findViewById(R.id.remark_input) as  EditText
        removeButton = view.findViewById(R.id.remove_button) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medicineDetailViewModel.medicineLiveData.observe(viewLifecycleOwner) {
            medicine -> medicine?.let {
                this.medicine = medicine
                updateUI()
            }
        }
    }

    private fun updateUI() {
        medicineNameInput.setText(medicine.name)
        datePickerDay.text = getStringFromDate(medicine.endDateTime, "dd")
        datePickerMonth.text = getStringFromDate(medicine.endDateTime, "MMM")
        datePickerYear.text = getStringFromDate(medicine.endDateTime, "yyyy")

        timePickerHour.text = getStringFromDate(medicine.endDateTime, "HH")
        timePickerMinute.text = getStringFromDate(medicine.endDateTime, "mm")

        when (medicine.repeatOption) {
            RepeatOption.MONDAY -> repeatOptionInput.setSelection(1)
            RepeatOption.TUESDAY -> repeatOptionInput.setSelection(2)
            RepeatOption.WEDNESDAY -> repeatOptionInput.setSelection(3)
            RepeatOption.THURSDAY -> repeatOptionInput.setSelection(4)
            RepeatOption.FRIDAY -> repeatOptionInput.setSelection(5)
            RepeatOption.SATURDAY -> repeatOptionInput.setSelection(6)
            RepeatOption.SUNDAY -> repeatOptionInput.setSelection(7)
            RepeatOption.EVERY_DAY -> repeatOptionInput.setSelection(8)

            else -> repeatOptionInput.setSelection(0)
        }
        remarkInput.setText(medicine.remark)
    }

    override fun onStart() {
        super.onStart()
        val medicineNameWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                medicine.name = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        val remarkWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                medicine.remark = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        datePickerButton.setOnClickListener {
            DatePickerFragment.newInstance(medicine.endDateTime).apply {
                setTargetFragment(this@MedicineFragment, REQUEST_DATE)
                show(this@MedicineFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }


        val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

                val originalDateTime = Calendar.getInstance()
                originalDateTime.time = medicine.endDateTime

                val oldYear = originalDateTime.get(Calendar.YEAR)
                val oldMonth = originalDateTime.get(Calendar.MONTH)
                val oldDay = originalDateTime.get(Calendar.DAY_OF_MONTH)

                medicine.endDateTime = GregorianCalendar(oldYear, oldMonth, oldDay, hourOfDay, minute).time

                updateUI()

            }


        medicineNameInput.addTextChangedListener(medicineNameWatcher)

        timePickerButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(
                    context,
                    timePickerDialogListener,
                    0,
                    0,
                    true
                )
                timePickerDialog.show()
        }

        repeatOptionInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val repeatOptionList: List<RepeatOption> = enumValues<RepeatOption>().toList()
                medicine.repeatOption = repeatOptionList[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        removeButton.setOnClickListener {
            medicineDetailViewModel.deleteMedicine(medicine)
            activity?.onBackPressed()
        }
        remarkInput.addTextChangedListener(remarkWatcher)
    }

    override fun onStop() {
        super.onStop()
        medicineDetailViewModel.saveMedicine(medicine)
    }

    private fun getStringFromDate(date: Date, pattern: String): String {
        val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
        return  dateFormatter.format(date)
    }

    override fun onDateSelected(date: Date) {
        medicine.endDateTime = date
        updateUI()
    }
}
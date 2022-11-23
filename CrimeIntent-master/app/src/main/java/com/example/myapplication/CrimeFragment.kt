package com.example.myapplication

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.lang.invoke.ConstantCallSite
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE ="DialogDate"
private const val REQUEST_DATE =0
private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeFragment(override val REQUEST_CONTACT: Any?) :Fragment(),DatePickerFrgament.Callbacks {
    private lateinit var crime: Crime
    private lateinit var  titleField :EditText
    private  lateinit var dateButton : Button
    private lateinit var  solvedCheckBox: CheckBox
    private lateinit var reportButton :Button
    private lateinit var suspectButton : Button

    private val crimeDetailViewModel :CrimeDetailViewModel by lazy{
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime =Crime()
        val crimeId: UUID =
            arguments?.getSerializable(ARG_CRIME_ID) as
                    UUID
        Log.d(TAG, "args bundle crime ID:$crimeId")
         crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField =view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        reportButton =view.findViewById(R.id.crime_report) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        return view

    }
    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view,
            savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime

                    updateUI()

                }
            })
    }
    override fun onStart() {
        super.onStart()
        val titleWatcher = object :TextWatcher{
            override fun beforeTextChanged(
                senquence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {

            }

            override fun onTextChanged(
                senquence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {
               crime.Title= senquence.toString()
            }

            override fun afterTextChanged(senquence: Editable?) {

            }

        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener {_, isChecked ->
                crime.isSolved = isChecked
            }
        }
        dateButton.setOnClickListener {
            DatePickerFrgament.newInstance(crime.Date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {intent ->
                val choserIntent = Intent.createChooser(intent, getString(R.string.sent_report))
                startActivity(choserIntent)
            }
        }
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_DATE)
            }
        }
        var pickContact = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        var packageManager = activity?.packageManager
        if(packageManager?.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            suspectButton.isEnabled = false
        }

    }
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }
    private fun updateUI() {
        titleField.setText(crime.Title)
        dateButton.text = crime.Date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onActivityResult(requestCode:
                                  Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK ->
                return
            requestCode == REQUEST_CONTACT &&
                    data != null -> {
                var contactUri: Uri? =
                    data.data

                val queryFields =
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor =
                    requireActivity().contentResolver
                        .query(
                            contactUri!!, queryFields, null, null, null
                        )
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect =

                        it.getString(0)
                    crime.suspect = suspect
                    suspectButton.text = suspect
                }
            }


        }

    }
    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)
        {
            getString(R.string.crime_report_solved)
        } else {



            getString(R.string.crime_report_unsolved) }

        val dateString =
            DateFormat.format(DATE_FORMAT,
                crime.Date).toString()
        var suspect = if
                              (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report,
            crime.Title, dateString,
            solvedString, suspect)
    }

    companion object {

        fun newInstance(crimeId: UUID):
                CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID,
                    crimeId)
            }
            return CrimeFragment(REQUEST_CONTACT = null).apply {
                arguments = args
            }
        }
    }
    override fun onDateSelected(date:Date){
        crime.Date=date
        updateUI()
    }
}
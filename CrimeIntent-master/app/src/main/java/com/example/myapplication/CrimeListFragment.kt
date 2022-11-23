package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import java.util.*


private const val TAG ="CrimeListFragment"
class CrimeListFragment : Fragment() {
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)

    }
    private var callbacks: Callbacks? = null

    private  lateinit var crimeRecyclerView : RecyclerView
    private  var adapter: CrimeAdapter? = CrimeAdapter(/*emptyList()*/)
    private  val crimeListViewModel : CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onAttach(context: Context) {
    super.onAttach(context)
    callbacks = context as Callbacks?
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_crime_list,container,false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
       crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
      //  updateUI()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crimes ->
                crimes?.let{ Log.i(TAG,"Got crimes ${crimes.size}")
                    //updateUI(crimes)
                    adapter?.submitList(it)
                }
            }
        )
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            R.id.new_crime ->{
                val crime =Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    private fun updateUI(crimes :List<Crime>){
        adapter = CrimeAdapter()
        crimeRecyclerView.adapter = adapter
    }
    private open inner  class CrimeHolder(view: View) :RecyclerView.ViewHolder(view),View.OnClickListener{
        private lateinit var crime : Crime
       private  val titleTextView : TextView = itemView.findViewById(R.id.crime_title)
       private   val dateTextView : TextView = itemView.findViewById(R.id.crime_date)

        fun bind(crime :Crime){
            this.crime = crime
            titleTextView.text=this.crime.Title
            dateTextView.text=this.crime.Date.toString()
        }

        override fun onClick(v: View) {
           callbacks?.onCrimeSelected(crime.id)
        }

    }
    private inner class CrimeWithPoliceHolder(view : View) : CrimeHolder(view) {
        val callPoliceButton : Button = itemView.findViewById(R.id.call_police_button)

        init {
            callPoliceButton.setOnClickListener { view : View ->
                Toast.makeText(context, "We are going to call the police!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private inner class CrimeAdapter(/*var crimes : List<Crime>*/) : //RecyclerView.Adapter<CrimeHolder>() {
        androidx.recyclerview.widget.ListAdapter<Crime, CrimeHolder>(CrimeDiffCallback()) {//эффективная перезагрузка списка, только новые изменения
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val view = when (viewType) {
            0 -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            1 -> layoutInflater.inflate(R.layout.list_item_crime_with_police, parent, false)
            else -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
        }
        if (viewType == 0) {
            return CrimeHolder(view)
        } else {
            return CrimeWithPoliceHolder(view)
        }
    }

        //тип элемента
        override fun getItemViewType(position: Int): Int {
            if (getItem(position).RequiresPolice) {
                return 1;
            }
            return 0
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)//crimes[position]
//            holder.apply {
//                titleTextView.text = crime.title
//                dateTextView.text = crime.date.toString()
//            }
            holder.bind(crime)
        }
    }
   /* private  inner  class CrimeAdapter(var crimes : List<Crime>) :RecyclerView.Adapter<CrimeHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = when (viewType) {
                0 -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                1 -> layoutInflater.inflate(R.layout.list_item_crime_with_police, parent, false)
                else -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            }
            if (viewType == 0) {
                return CrimeHolder(view)
            } else {
                return CrimeWithPoliceHolder(view)
            }


        }
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
           val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
          return crimes.size
        }

        *//*Определение для типа*//*
        override fun getItemViewType(position: Int): Int {
            if (crimes[position].RequiresPolice) {
                return 1;
            }
            return 0
        }


    }*/

    class CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }
    companion object{
        fun newInstance() : CrimeListFragment{
            return  CrimeListFragment()
        }
    }
}
package com.example.untilthelesson.ui.dashboard

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.untilthelesson.MainActivity
import com.example.untilthelesson.TimeTable
import com.example.untilthelesson.R
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        val delButton: Button =root.findViewById(R.id.button_del)
        delButton.visibility=View.INVISIBLE
        if (TimeTable.size==0) {
            dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
                textView.text = getString(R.string.no_time_table)
            })
        }
        var adapt= ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1, TimeTable.toMutableList())
        val listview:ListView=root.findViewById(R.id.listview)
        listview.adapter=adapt
        listview.setOnItemLongClickListener { adapterView, view, i, l ->
            adapt= ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_multiple_choice, TimeTable.toMutableList())
            listview.adapter=adapt
            listview.setItemChecked(i, true)
            delButton.visibility=View.VISIBLE
            listview.setOnItemClickListener { adapterView, view, i, l ->
                if(listview.checkedItemCount == 0) {
                    delButton.visibility=View.INVISIBLE
                    adapt = ArrayAdapter<String>(
                        context!!,
                        android.R.layout.simple_list_item_1,
                        TimeTable.toMutableList()
                    )
                    listview.adapter = adapt
                    listview.onItemClickListener=null
                }
            }
            true
        }
        delButton.setOnClickListener{
            delButton.visibility=View.INVISIBLE
            with(listview.checkedItemPositions){
                for (i in 0 until size()){
                    val deltime=listview.getItemAtPosition(keyAt(i)).toString()
                    Log.d("del", deltime)
                    updateTimeTable(deltime,false)
                }
            }
            adapt= ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1, TimeTable.toMutableList())
            listview.adapter=adapt
            if (TimeTable.size==0) {
                dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
                    textView.text = getString(R.string.no_time_table)
                })
            }

        }

        val but:Button = root.findViewById(R.id.button)
        but.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                val newTime=SimpleDateFormat("HH:mm").format(cal.time)
                textView.text = newTime
                this.updateTimeTable(newTime)
                adapt= ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1, TimeTable.toMutableList())
                listview.adapter=adapt
                dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
                    textView.text = it
                })
            }
            TimePickerDialog(this.activity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            // Вывод timePickerDialog
        }
        return root
    }
    fun updateTimeTable(newTime:String,type:Boolean=true){
        if (type) {
            TimeTable = TimeTable.plus(newTime).sortedWith(this.myCustomComparator).toSet()
        }
        else{
            TimeTable = TimeTable.minus(newTime).sortedWith(this.myCustomComparator).toSet()
        }
        try {
            activity!!.openFileOutput("TimeTableDataBase", Context.MODE_PRIVATE)
                .write(TimeTable.joinToString(separator = ",").toByteArray())
        }
        catch (t:Throwable) {
            Log.d("error",t.toString())
            Toast.makeText(activity,
                "Failed to save time. Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    val myCustomComparator =  Comparator<String> { a, b ->
        when {
            (a==""|| b=="" || a.split(":")[0].toInt()*60+a.split(":")[1].toInt()==b.split(":")[0].toInt()*60+b.split(":")[1].toInt()) -> 0
            (a.split(":")[0].toInt()*60+a.split(":")[1].toInt()<b.split(":")[0].toInt()*60+b.split(":")[1].toInt()) -> -1
            else -> 1
        }
    }
}
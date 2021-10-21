package com.example.untilthelesson.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.untilthelesson.R
import java.text.SimpleDateFormat
import com.example.untilthelesson.TimeTable
import android.os.Handler
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var timePlace:TextView
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = getString(R.string.unlit_the_lesson)
        })
        val time_place:TextView =root.findViewById(R.id.time_place)
        timePlace=time_place
        val sdf = SimpleDateFormat("HH:mm")
        val currentDate = sdf.format(Date())
        timePlace.text=currentDate
        return root
    }

    override fun onResume() {
        super.onResume()
        val sdf = SimpleDateFormat("HH:mm")
        val currentDate = sdf.format(Date())
        val cur_seconds:Int = currentDate.split(":")[0].toInt()*60+currentDate.split(":")[1].toInt()

        timePlace.text=getLeftTime(cur_seconds)
    }
     fun getLeftTime(cur_seconds:Int):String{
        var ansTime=""
        for(time in TimeTable){
            if (time!="") {
                val seconds: Int = time.split(":")[0].toInt() * 60 + time.split(":")[1].toInt()
                if (seconds > cur_seconds) {
                    val left_seconds = seconds - cur_seconds
                    val h = (left_seconds / 60).toString()
                    val s = (left_seconds % 60).toString()
                    ansTime = h + getString(R.string.hours)+" "+ s+getString(R.string.minuts)
                    break
                }
                else if(seconds==cur_seconds){
                    ansTime=getString(R.string.Now)
                    break
                }
            }
        }
        if (ansTime==""){
            return getString(R.string.no_more_calls)
        }
        else{
            return ansTime
        }
    }

}
package sahu.ritvik.recyclerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sahu.ritvik.recyclerdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    var binding:ActivityMainBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val adapter=MainAdapter(TaskList.taskList)
        binding?.rv?.adapter=adapter


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
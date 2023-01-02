package sahu.ritvik.recyclerdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sahu.ritvik.recyclerdemo.databinding.RvItemBinding

class MainAdapter(val taskList:List<Task>):RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


    inner class MainViewHolder(val itemBinding: RvItemBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(task:Task) {
            itemBinding.titleTv2.text = task.T
            itemBinding.titleTv4.text = task.DO
            itemBinding.titleTv6.text = task.pH
            itemBinding.titleTv8.text = task.TDS
            itemBinding.tvDevice.text=task.device
            itemBinding.tvTime.text=task.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
       return MainViewHolder(RvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val task= taskList[position]
        holder.bindItem(task)
    }

    override fun getItemCount(): Int {
       return taskList.size
    }


}
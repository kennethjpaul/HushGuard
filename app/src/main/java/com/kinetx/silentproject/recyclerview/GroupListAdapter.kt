package com.kinetx.silentproject.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.kinetx.silentproject.R
import com.kinetx.silentproject.dataclass.GroupsRecyclerDataClass

class GroupListAdapter() : RecyclerView.Adapter<GroupListAdapter.MyViewHolder>(){


    private var _list = emptyList<GroupsRecyclerDataClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = _list[position]
        holder.checkBox.text = currentItem.groupName
        holder.checkBox.isChecked = currentItem.isChecked
    }

    override fun getItemCount(): Int {
        return _list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkBox : CheckBox = itemView.findViewById(R.id.group_list_item_checkbox)

        init {
            checkBox.setOnClickListener()
            {
                val isChk = (it as CheckBox).isChecked
                _list[adapterPosition].isChecked = isChk
                notifyDataSetChanged()
            }
        }



    }

    fun setData( c: List<GroupsRecyclerDataClass>)
    {
        _list = c
        notifyDataSetChanged()
    }
}
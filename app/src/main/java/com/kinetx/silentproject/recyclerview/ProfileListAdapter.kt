package com.kinetx.silentproject.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.kinetx.silentproject.R
import com.kinetx.silentproject.dataclass.GroupsRecyclerDataClass
import com.kinetx.silentproject.dataclass.ProfileItemData
import com.kinetx.silentproject.helpers.Converters

class ProfileListAdapter(val listener: ProfileListAdapterInterface) : RecyclerView.Adapter<ProfileListAdapter.MyViewHolder>() {


    private var _list = emptyList<ProfileItemData>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val profileIcon : ImageView = itemView.findViewById(R.id.profile_list_item_image)
        val profileColor : ImageView = itemView.findViewById(R.id.profile_list_item_background)
        val profileName : TextView = itemView.findViewById(R.id.profile_list_item_name)
        val profileSwitch : SwitchCompat = itemView.findViewById(R.id.profile_list_item_switch)


        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position!=RecyclerView.NO_POSITION)
            {
                listener.profileListLongClick(position)
                return true
            }
            return false

        }

    }

    interface ProfileListAdapterInterface {
        fun profileListSwitchClick(position: Int)
        fun profileListLongClick(position: Int)
        fun removeFavorites(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = _list[position]
        holder.profileName.text = currentItem.profileName
        holder.profileColor.setBackgroundColor(currentItem.profileColor)
        holder.profileIcon.setImageResource(currentItem.profileIcon)
        holder.profileSwitch.isChecked = currentItem.profileChecked

        holder.profileSwitch.setOnClickListener()
        {
            if (!currentItem.profileChecked)
            {
                val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setPositiveButton("Yes")
            {
                    _,_ ->
                currentItem.profileChecked = !currentItem.profileChecked
                notifyItemChanged(position)


                _list.filter { it.profileChecked }.forEach {
                    val idx = _list.indexOf(it)
                    if (idx!=position) {
                        it.profileChecked = false
                        notifyItemChanged(idx)
                    }
                }
                if(currentItem.profileChecked) {
                    listener.profileListSwitchClick(position)
                }
            }
            builder.setNegativeButton("No")
            {
                    _,_ ->
                currentItem.profileChecked = false
                notifyItemChanged(position)
            }
            builder.setTitle("Do you want to activate this profile?")
            builder.setMessage("This action can take some time depending upon the device")
            builder.create().show()
            }
            else
            {
                currentItem.profileChecked = false
                notifyItemChanged(position)
                listener.removeFavorites(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return _list.size
    }

    fun setData( c: List<ProfileItemData>)
    {
        _list = c
        notifyDataSetChanged()
    }
}
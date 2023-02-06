package com.rizqanmr.jetpack.roomexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rizqanmr.jetpack.roomexample.R
import com.rizqanmr.jetpack.roomexample.databinding.ListSubscriberBinding
import com.rizqanmr.jetpack.roomexample.db.Subscriber

class SubscriberAdapter(private val clickListener: (Subscriber)->Unit) : RecyclerView.Adapter<SubscriberViewHolder>() {
    private val listSubscribers = ArrayList<Subscriber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListSubscriberBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_subscriber, parent,false)
        return SubscriberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriberViewHolder, position: Int) {
        holder.bind(listSubscribers[position], clickListener)
    }

    override fun getItemCount(): Int {
        return listSubscribers.size
    }

    fun setList(subscriber: List<Subscriber>){
        listSubscribers.clear()
        listSubscribers.addAll(subscriber)
    }
}

class SubscriberViewHolder(val binding: ListSubscriberBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(subscriber: Subscriber, clickListener: (Subscriber) -> Unit) {
        binding.tvName.text = subscriber.name
        binding.tvEmail.text = subscriber.email
        binding.listItemLayout.setOnClickListener {
            clickListener(subscriber)
        }
    }
}
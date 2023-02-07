package com.rizqanmr.jetpack.roomexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqanmr.jetpack.roomexample.adapter.SubscriberAdapter
import com.rizqanmr.jetpack.roomexample.databinding.ActivityMainBinding
import com.rizqanmr.jetpack.roomexample.db.Subscriber
import com.rizqanmr.jetpack.roomexample.db.SubscriberDatabase
import com.rizqanmr.jetpack.roomexample.db.SubscriberRepository
import com.rizqanmr.jetpack.roomexample.viewmodel.SubscriberViewModel
import com.rizqanmr.jetpack.roomexample.viewmodel.SubscriberViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: SubscriberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory)[SubscriberViewModel::class.java]
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        initRecyclerView()
        subscriberViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvSubscriber.layoutManager = LinearLayoutManager(this)
        adapter = SubscriberAdapter { selectedItem: Subscriber -> listItemClicked(selectedItem) }
        binding.rvSubscriber.adapter = adapter
        showSubscribersList()
    }

    private fun showSubscribersList() {
        subscriberViewModel.subscriber.observe(this) {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun listItemClicked(subscriber: Subscriber) {
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}
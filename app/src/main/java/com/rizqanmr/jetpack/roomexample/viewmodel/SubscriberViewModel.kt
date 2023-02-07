package com.rizqanmr.jetpack.roomexample.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqanmr.jetpack.roomexample.db.Subscriber
import com.rizqanmr.jetpack.roomexample.db.SubscriberRepository
import com.rizqanmr.jetpack.roomexample.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(){
    val subscriber = repository.subscribers

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()
    val inputPhone = MutableLiveData<String>()
    val saveOrUpdateButton = MutableLiveData<String>()
    val clearAllOrDeleteButton = MutableLiveData<String>()

    private var isUpdateOrDelete = false
    private lateinit var  subscriberToUpdateOrDelete: Subscriber

    private val statusMessage  = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
    get() = statusMessage

    init {
        saveOrUpdateButton.value = "Save"
        clearAllOrDeleteButton.value = "Clear All"
    }

    fun saveOrUpdate(){
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter subscriber's name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter subscriber's email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a correct email address")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value.orEmpty()
                subscriberToUpdateOrDelete.email = inputEmail.value.orEmpty()
                subscriberToUpdateOrDelete.phone = inputPhone.value.orEmpty()
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value.orEmpty()
                val email = inputEmail.value.orEmpty()
                val phone = inputPhone.value.orEmpty()
                insert(Subscriber(0, name, email, phone))
                inputName.value = ""
                inputEmail.value = ""
                inputPhone.value = ""
            }
        }
    }

    fun clearAllOrDelete(){
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val newRowId = repository.insert(subscriber)
        withContext(Dispatchers.Main) {
            if (newRowId>-1) {
                statusMessage.value = Event("New Subscriber Inserted Successfully! $newRowId")
            } else {
                statusMessage.value = Event("Error Occurred!")
            }

        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRows = repository.update(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRows > 0) {
                inputName.value = ""
                inputEmail.value = ""
                inputPhone.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButton.value = "Save"
                clearAllOrDeleteButton.value = "Clear All"
                statusMessage.value = Event("$numberOfRows Row Updated Successfully!")
            } else {
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.delete(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRowsDeleted > 0) {
                inputName.value = ""
                inputEmail.value = ""
                inputPhone.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButton.value = "Save"
                clearAllOrDeleteButton.value = "Clear All"
                statusMessage.value = Event("$numberOfRowsDeleted Rows Deleted Successfully!")
            } else {
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    private fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.deleteAll()
        withContext(Dispatchers.Main) {
            if (numberOfRowsDeleted > 0) {
                statusMessage.value = Event("$numberOfRowsDeleted Subscribers Deleted Successfully!")
            } else {
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        inputPhone.value = subscriber.phone
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButton.value = "Update"
        clearAllOrDeleteButton.value = "Delete"
    }
}
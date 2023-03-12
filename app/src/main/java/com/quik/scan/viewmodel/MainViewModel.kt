package com.quik.scan.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quik.scan.R
import com.quik.scan.generate.Component
import com.quik.scan.history.database.DatabaseObject
import com.quik.scan.history.database.DatabaseRepo
import com.quik.scan.history.database.History
import com.quik.scan.qrcode.analyzer.BarCodeTypes
import com.quik.scan.qrcode.sensors.MeasurableSensors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val lightSensors: MeasurableSensors
) : AndroidViewModel(application) {
    var isDark by mutableStateOf(false)
    var isSaved by mutableStateOf(false)
    var link by mutableStateOf<String?>(null)
    val list: MutableList<Component> = mutableListOf()
    val searchData: MutableState<List<History>> = mutableStateOf(listOf())
    val completeHistory: Flow<List<History>>
    val getSaved: Flow<List<History>>
    private val repository: DatabaseRepo

    init {
        val dB = DatabaseObject.getInstance(application)
        val dataDao = dB.hisDao()
        repository = DatabaseRepo(dataDao)
        completeHistory = repository.completeHistory
        getSaved = repository.getSaved
        list.add(
            Component(
                painter = R.drawable.email,
                text = "Email"
            )
        )
        list.add(
            Component(
                painter = R.drawable.contacts,
                text = "Contacts"
            )
        )
        list.add(
            Component(
                painter = R.drawable.phone,
                text = "Phone Number"
            )
        )
        list.add(
            Component(
                painter = R.drawable.url,
                text = "Url"
            )
        )
        lightSensors.startListening()
        lightSensors.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            println("The values are $lux")
            isDark = lux <= 0f
            println("what is is Dark $isDark")
        }
    }

    fun insertHistory(
        content: String,
        date: Long,
        type: BarCodeTypes,
        isScanned: Boolean,
        isShared: Boolean = false,
        sharedFrom: String? = null
    ){
        viewModelScope.launch {
            repository.insertHistory(
                History(
                    content = content,
                    date = date,
                    type = type,
                    isScanned = isScanned,
                    isShared = isShared,
                    sharedFrom = sharedFrom
                )
            )
        }
    }
    fun updateHistory(
        content: String,
        date: Long,
        type: BarCodeTypes,
        isScanned: Boolean,
        name: String?,
        isSaved: Boolean,
        isShared: Boolean,
        sharedFrom: String?
    ){
        viewModelScope.launch {
            repository.updateHistory(
                History(
                    content = content,
                    date = date,
                    type = type,
                    isScanned = isScanned,
                    name = name,
                    isSaved = isSaved,
                    isShared = isShared,
                    sharedFrom = sharedFrom
                )
            )
        }
    }

    fun searchQuery(Query: String) = repository.searchQuery(Query)


}
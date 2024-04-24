package com.dicoding.asclepius.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.repository.HistoryRepository

class HistoryViewModel(application: Application) : ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    fun insert(history: History){
        mHistoryRepository.insert(history)
    }

    fun getAllHistory(): LiveData<List<History>> = mHistoryRepository.getAllHistory()
}
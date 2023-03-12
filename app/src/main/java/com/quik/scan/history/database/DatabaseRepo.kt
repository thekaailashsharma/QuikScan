package com.quik.scan.history.database


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DatabaseRepo(private val historyDao: HistoryDao) {
    val completeHistory: Flow<List<History>> = historyDao.getHistory()
    val getSaved = historyDao.getSaved()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    fun insertHistory(history: History) {
        coroutineScope.launch {
            historyDao.insertHistory(history)
        }
    }

    fun searchQuery(Query: String) = historyDao.search(query = Query)

    fun updateHistory(history: History) {
        coroutineScope.launch {
            historyDao.updateHistory(history)
        }
    }

    fun deleteHistory() {
        coroutineScope.launch {
            historyDao.deleteTable()
        }
    }
}

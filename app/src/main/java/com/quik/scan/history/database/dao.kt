package com.quik.scan.history.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History)

    @Update
    suspend fun updateHistory(history: History)

    @Query("Delete from History")
    suspend fun deleteTable()

    @Query("select * from History")
    fun getHistory(): Flow<List<History>>

    @Query("SELECT * from History where isSaved == true")
    fun getSaved(): Flow<List<History>>

   @Query(
       """
           SELECT * from history as his 
           where his.content like '%' || :query || '%' 
           or his.date like '%' || :query || '%' 
           or his.type like '%' || :query || '%' 
           or his.name like '%' || :query || '%' 
           or his.sharedFrom like '%' || :query || '%' 
       """
   )
    fun search(query: String): Flow<List<History>>
}
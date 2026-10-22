package com.cleversoftware.contactcardqr.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {

	@Query("SELECT * FROM qr_cards ORDER BY createdAt DESC")
	fun getAllCards(): Flow<List<QRCodeCard>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertCard(card: QRCodeCard)

	@Delete
	suspend fun deleteCard(card: QRCodeCard)

	@Query("DELETE FROM qr_cards WHERE id = :cardId")
	suspend fun deleteCardById(cardId: String)
}

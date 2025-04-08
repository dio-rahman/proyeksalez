package com.salez.kasir.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.salez.kasir.data.local.MemberEntity
@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE memberId = :memberId")
    suspend fun getMemberById(memberId: String): MemberEntity?

    @Query("SELECT * FROM members WHERE phone = :phone")
    suspend fun getMemberByPhone(phone: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Query("UPDATE members SET totalSpent = totalSpent + :amount, totalOrders = totalOrders + 1 WHERE memberId = :memberId")
    suspend fun updateMemberStats(memberId: String, amount: Double)
}

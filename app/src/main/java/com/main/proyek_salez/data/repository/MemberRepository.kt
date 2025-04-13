package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.local.daos.MemberDao
import com.main.proyek_salez.data.local.MemberEntity
import com.main.proyek_salez.data.models.Member
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberDao: MemberDao
) {
    suspend fun registerMember(member: Member): Result<Member> {
        return try {
            // Check if member with same phone already exists
            val existingMember = getMemberByPhone(member.phone).getOrNull()
            if (existingMember != null) {
                return Result.failure(Exception("Member with this phone number already exists"))
            }

            // Generate ID if not provided
            val memberId = member.memberId.ifEmpty {
                UUID.randomUUID().toString()
            }

            val newMember = member.copy(memberId = memberId)

            // Convert to entity and insert
            val memberEntity = MemberEntity(
                memberId = newMember.memberId,
                name = newMember.name,
                phone = newMember.phone,
                email = newMember.email,
                joinDate = newMember.joinDate,
                totalSpent = newMember.totalSpent,
                totalOrders = newMember.totalOrders,
                discountPercentage = newMember.discountPercentage
            )

            memberDao.insertMember(memberEntity)
            Result.success(newMember)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMemberById(memberId: String): Result<Member> {
        return try {
            val memberEntity = memberDao.getMemberById(memberId)
                ?: return Result.failure(Exception("Member not found"))

            // Convert entity to model
            val member = Member(
                memberId = memberEntity.memberId,
                name = memberEntity.name,
                phone = memberEntity.phone,
                email = memberEntity.email,
                joinDate = memberEntity.joinDate,
                totalSpent = memberEntity.totalSpent,
                totalOrders = memberEntity.totalOrders,
                discountPercentage = memberEntity.discountPercentage
            )

            Result.success(member)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMemberByPhone(phone: String): Result<Member> {
        return try {
            val memberEntity = memberDao.getMemberByPhone(phone)
                ?: return Result.failure(Exception("Member not found"))

            // Convert entity to model
            val member = Member(
                memberId = memberEntity.memberId,
                name = memberEntity.name,
                phone = memberEntity.phone,
                email = memberEntity.email,
                joinDate = memberEntity.joinDate,
                totalSpent = memberEntity.totalSpent,
                totalOrders = memberEntity.totalOrders,
                discountPercentage = memberEntity.discountPercentage
            )

            Result.success(member)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMemberStats(memberId: String, orderAmount: Double): Result<Member> {
        return try {
            // Get current member data
            val member = getMemberById(memberId).getOrNull()
                ?: return Result.failure(Exception("Member not found"))

            // Update stats in database
            memberDao.updateMemberStats(memberId, orderAmount)

            // Return updated member
            val updatedMember = member.copy(
                totalSpent = member.totalSpent + orderAmount,
                totalOrders = member.totalOrders + 1
            )

            Result.success(updatedMember)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
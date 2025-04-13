package com.main.proyek_salez.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.main.proyek_salez.data.CartItemEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val gson = Gson()
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromCartItemList(value: List<CartItemEntity>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCartItemList(value: String?): List<CartItemEntity>? {
        val listType = object : TypeToken<List<CartItemEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
}

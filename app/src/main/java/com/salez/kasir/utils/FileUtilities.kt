package com.salez.kasir.utils

import com.salez.kasir.data.DataEntity
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import kotlin.String

fun extractDataFromExcel(file: File): List<DataEntity> {
    val dataEntities = mutableListOf<DataEntity>()
    val workbook = XSSFWorkbook(FileInputStream(file))
    val sheet = workbook.getSheetAt(0)

    for (rowIndex in 1..sheet.lastRowNum) {
        val row = sheet.getRow(rowIndex) ?: continue

        if (row.getCell(0) == null) continue

        try {
            val nama_menu = row.getCell(0)?.stringCellValue ?: ""

            val harga_menu = when(row.getCell(1)?.cellType) {
                CellType.NUMERIC -> row.getCell(1)?.numericCellValue?.toInt() ?: 0
                CellType.STRING -> row.getCell(1)?.stringCellValue?.toIntOrNull() ?: 0
                else -> 0
            }

            val jenis_menu = row.getCell(5)?.stringCellValue ?: ""

            val entity = DataEntity(
                val nama_menu: String,
                val harga_menu: Int,
                val jenis_menu: Int,
            )

            dataEntities.add(entity)
        } catch (e: Exception) {
            continue
        }
    }

    workbook.close()
    return dataEntities
}
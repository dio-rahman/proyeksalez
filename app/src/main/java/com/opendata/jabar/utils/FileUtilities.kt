package com.opendata.jabar.utils

import com.opendata.jabar.data.DataEntity
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

// Function to extract data from Excel files and convert to DataEntity objects
fun extractDataFromExcel(file: File): List<DataEntity> {
    val dataEntities = mutableListOf<DataEntity>()
    val workbook = XSSFWorkbook(FileInputStream(file))
    val sheet = workbook.getSheetAt(0)

    // Start from row 2 (index 1) to skip header row
    for (rowIndex in 1..sheet.lastRowNum) {
        val row = sheet.getRow(rowIndex) ?: continue

        // Skip empty rows
        if (row.getCell(0) == null) continue

        try {
            val kodeProvinsi = when(row.getCell(0)?.cellType) {
                CellType.NUMERIC -> row.getCell(0)?.numericCellValue?.toInt() ?: 0
                CellType.STRING -> row.getCell(0)?.stringCellValue?.toIntOrNull() ?: 0
                else -> 0
            }

            val namaProvinsi = row.getCell(1)?.stringCellValue ?: ""

            val kodeKabupatenKota = when(row.getCell(2)?.cellType) {
                CellType.NUMERIC -> row.getCell(2)?.numericCellValue?.toInt() ?: 0
                CellType.STRING -> row.getCell(2)?.stringCellValue?.toIntOrNull() ?: 0
                else -> 0
            }

            val namaKabupatenKota = row.getCell(3)?.stringCellValue ?: ""

            val rataRataLamaSekolah = when(row.getCell(4)?.cellType) {
                CellType.NUMERIC -> row.getCell(4)?.numericCellValue ?: 0.0
                CellType.STRING -> row.getCell(4)?.stringCellValue?.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }

            val satuan = row.getCell(5)?.stringCellValue ?: ""

            val tahun = when(row.getCell(6)?.cellType) {
                CellType.NUMERIC -> row.getCell(6)?.numericCellValue?.toInt() ?: 0
                CellType.STRING -> row.getCell(6)?.stringCellValue?.toIntOrNull() ?: 0
                else -> 0
            }

            val entity = DataEntity(
                kode_provinsi = kodeProvinsi,
                nama_provinsi = namaProvinsi,
                kode_kabupaten_kota = kodeKabupatenKota,
                nama_kabupaten_kota = namaKabupatenKota,
                rata_rata_lama_sekolah = rataRataLamaSekolah,
                satuan = satuan,
                tahun = tahun
            )

            dataEntities.add(entity)
        } catch (e: Exception) {
            // Skip rows with errors
            continue
        }
    }

    workbook.close()
    return dataEntities
}
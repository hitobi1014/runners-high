package io.runnershigh.backend.training.domain.enum

enum class DistanceUnit(val symbol: String, val conversionFactor: Double) {
    METER("m", 1.0),
    KILOMETER("km", 1000.0);

    fun convertTo(value: Double, targetUnit: DistanceUnit): Double {
        val valueInMeters = value * this.conversionFactor
        return valueInMeters / targetUnit.conversionFactor
    }

    companion object {
        fun fromString(symbol: String): DistanceUnit {
            return entries.find { it.symbol.equals(symbol, ignoreCase = true) }
                ?: throw IllegalArgumentException("지원하지 않는 거리 단위입니다: $symbol")
        }
    }
}
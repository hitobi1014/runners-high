package io.runnershigh.backend.training.entity.enum

enum class TrainingColor(val hexCode: String, val displayName: String) {
    LAVENDER("#D8B5FF", "라벤더"),
    MINT("#B5EAD7", "민트"),
    PINK("#FFB7C5", "핑크"),
    YELLOW("#FFEAA7", "옐로우"),
    BLUE("#A2D2FF", "블루"),
    CORAL("#FFAAA5", "코랄"),
    PURPLE("#C3AED6", "퍼플"),
    PEACH("#FFD3B6", "피치"),
    GREEN("#C7F0BD", "그린"),
    SKY("#BDE0FE", "스카이");

    companion object {
        fun fromHexCode(hexCode: String): TrainingColor? {
            return TrainingColor.entries.find { it.hexCode == hexCode }
        }
    }
}

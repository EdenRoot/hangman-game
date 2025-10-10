package dev.kiryao.core.model

enum class PathFile(val value: String) {
    DICTIONARY_NATURE("wordNature.txt"),
    DICTIONARY_HOBBY("wordHobby.txt"),
    DICTIONARY_FOOD("wordFood.txt"),
    DICTIONARY_ANIMAL("wordAnimal.txt"),
    DICTIONARY_CLOTHES("wordClothes.txt");

    companion object {
        fun random(): PathFile = values().random()
    }
}
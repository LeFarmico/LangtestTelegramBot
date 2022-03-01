package ability.langTestAbility

data class TestData(
    val wordToTranslate: String,
    val answer: String,
    val falseAnswers: List<String>
)

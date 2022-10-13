package sid.com.quotelyindia.models.data

data class QuotesMeta(
    val year: Int = 0,
    val movie: String = "",
    val quotes: String = "",
    val saidBy: String = "",
    val quoteId: String = "",
    val likes: Int = 0,
    val tag: List<String> = mutableListOf()
)

package sid.com.quotelyindia.models.data

data class QuotesCardMeta(
    val movie: String = "",
    val tag: String = "",
    val quoteId: String = "",
    val likes: Int = 0,
    val inframe: List<String> = mutableListOf()
)
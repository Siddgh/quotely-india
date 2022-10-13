package sid.com.quotelyindia.utils

import java.util.*

object StringUtils {

    fun displayLikes(noOfLikes: Int): String {
        return if (noOfLikes == 1) "$noOfLikes Like" else "$noOfLikes Likes"
    }

    fun String.movieNameAsId(): String {
        return this.replace("\\s".toRegex(), "")
            .toLowerCase(Locale.ROOT)
            .replace("(", "")
            .replace(")", "")
            .replace("-", "")
            .replace("_", "")
            .replace(".", "")
            .replace("*", "")
            .replace("&", "")
            .replace("%", "")
            .replace("$", "")
            .replace("#", "")
            .replace("@", "")
            .replace("!", "")
            .replace("+", "")
            .replace(":", "")
            .replace("'", "")
            .replace("?", "")
    }

    fun String.quote(): String {
        return this.replace("... ", "\n\n")
    }

    fun quotesFromHeader(movie: String): String {
        return "Quotes from $movie"
    }

    fun bottomSheetMoreOptionsTitlesTags(tags: String): String {
        return "$tags Quotes"
    }

    fun quotesTagsHeader(category: String): String {
        return "Listing $category Quotes"
    }

    fun searchingForMessage(s: String): String {
        return "Searching for ${s.toString()}"
    }

    fun quoteCardTagsHeader(exploreTo: String): String {
        return "Listing All $exploreTo Quotes Cards"
    }

    fun getJPGFileName(id: String): String {
        return "$id.jpg"
    }

    fun getJPEGFileName(id: String): String {
        return "$id.jpeg"
    }

    fun getPNGFileName(fileName: String): String {
        return "$fileName.png"
    }

    fun getDownloadNotificationTitle(movie: String): String {
        return "Downloading Quote Card from $movie"
    }

    fun welcomeMessage(name: String): String {
        return "Welcome to Quotely India, $name"
    }

}
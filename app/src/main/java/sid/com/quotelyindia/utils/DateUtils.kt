package sid.com.quotelyindia.utils

import sid.com.quotelyindia.constants.MessagesConstants
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    val currentDate: String =
        SimpleDateFormat(
            MessagesConstants.dateFormat,
            Locale.ENGLISH
        ).format(Calendar.getInstance().time)


}
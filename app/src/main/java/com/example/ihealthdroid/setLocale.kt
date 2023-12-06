import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

fun setLocale(activity: Activity, langCode: String) {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}
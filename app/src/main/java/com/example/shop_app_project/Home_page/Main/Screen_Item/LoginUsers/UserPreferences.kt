import UserPreferences.Companion.PREF_NAME
import android.content.Context
import android.content.SharedPreferences

class UserPreferences private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_PHONE = "phone"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
        }
    }

    fun saveUser(context: Context, phone: String) {
        sharedPreferences.edit().apply {
            putString(KEY_PHONE, phone)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserPhone(): String? {
        return sharedPreferences.getString(KEY_PHONE, null)
    }

    fun clearUser() {
        sharedPreferences.edit().apply {
            remove(KEY_PHONE)
            remove(KEY_IS_LOGGED_IN)
            apply()
        }
    }
}
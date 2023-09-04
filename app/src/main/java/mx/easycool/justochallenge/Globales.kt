package mx.easycool.justochallenge
import android.app.Application


class Globales : Application() {

  fun wsUrl(): String? {
   return "https://randomuser.me/api/";
  }
}
package mx.easycool.justochallenge


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.easycool.com.Dialogos
import mx.pinturasosel.com.WebServiceCall
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

var globales :  Globales? = null
var d: Dialogos? = null
var tv_nombre: TextView? = null
var tv_edad: TextView? = null
var img: ImageView? = null
var tv_titulo: TextView? = null
var tv_correo: TextView? = null
var tv_celular: TextView? = null
var tv_direccion: TextView? = null
var bottomSheet: InicioActivity.BottomSheetDialog? = null
var direccion: JSONObject? = null

var mapFragment: SupportMapFragment? = null
var map: GoogleMap? = null
var direccion_texto: String? = ""


class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val view = toolbar.rootView
        tv_titulo = view.findViewById<TextView>(R.id.txt_bar)

        tv_titulo!!.setText(getString(R.string.app_name))

        globales = application as Globales
        d = Dialogos()
        tv_nombre = findViewById(R.id.lbl_nombre)
        tv_edad = findViewById(R.id.lbl_edad)
        tv_correo = findViewById(R.id.lbl_correo)
        tv_celular = findViewById(R.id.lbl_celular)
        tv_direccion = findViewById(R.id.lbl_direccion)
        img = findViewById(R.id.img)

        RecuperaDatosUsuario();
    }

    fun llena_perfil(usuario: JSONArray){
        d!!.progressShow(this,getString(R.string.str_espere))
        direccion_texto = usuario.getJSONObject(0).getJSONObject("location").getJSONObject("street").getString("name")+
                " "+ usuario.getJSONObject(0).getJSONObject("location").getJSONObject("street").getString("number") +
                " "+ usuario.getJSONObject(0).getJSONObject("location").getString("city") +
                ", "+ usuario.getJSONObject(0).getJSONObject("location").getString("state")+
                ", "+ usuario.getJSONObject(0).getJSONObject("location").getString("country")
        DownloadAsyncTask().execute(usuario.getJSONObject(0).getJSONObject("picture").getString("large"))
        tv_nombre!!.setText(usuario.getJSONObject(0).getJSONObject("name").getString("title").toString() +
                " "+ usuario.getJSONObject(0).getJSONObject("name").getString("first").toString() +
                " "+ usuario.getJSONObject(0).getJSONObject("name").getString("last").toString())
        tv_edad!!.setText(usuario.getJSONObject(0).getJSONObject("dob").getString("age"))
        tv_correo!!.setText(usuario.getJSONObject(0).getString("email"))
        tv_celular!!.setText(usuario.getJSONObject(0).getString("phone"))
        tv_direccion!!.setText(direccion_texto)

        direccion =usuario.getJSONObject(0).getJSONObject("location").getJSONObject("coordinates")
    }

    fun verUbicacion(view: View){
        try {
            if (direccion!!.getString("latitude") != "" && direccion!!.getString(
                    "longitude"
                ) != ""
            ) {
                bottomSheet = BottomSheetDialog()
                bottomSheet!!.show(supportFragmentManager,
                    "ModalBottomSheet"
                )
            } else {
                d!!.alert(
                    "",
                    getString(R.string.str_error_ubicacion),
                    this
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    class BottomSheetDialog : BottomSheetDialogFragment(), OnMapReadyCallback {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val v: View = inflater.inflate(R.layout.alert_mapa_ubicacion, container, false)
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            var tv_direccion = v.findViewById<TextView>(R.id.tv_direccion)
            tv_direccion.setText(direccion_texto)
            mapFragment?.getMapAsync(this)
            return v
        }

        override fun onDestroyView() {
            super.onDestroyView()
            val fragment = requireFragmentManager().findFragmentById(R.id.map)
            if (fragment != null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
               map = null
            }
        }

        override fun onMapReady(googleMap: GoogleMap) {
            if (map == null) {
                map = googleMap
            }
            try {
                val point = LatLng(
                    direccion!!.getDouble("latitude"),
                    direccion!!.getDouble("longitude")
                )
                map!!.addMarker(
                    MarkerOptions().position(point)
                )
                map!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        point,
                        10f
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun cerrarMapa(view: View?) {
        if (!bottomSheet!!.isHidden) {
            bottomSheet!!.dismiss()
            bottomSheet!!.onDestroy()
        }
    }

    fun RecuperaDatosUsuario(){
        ws().execute(globales!!.wsUrl() ,"")
    }

    @Throws(JSONException::class)
    fun abrirNavegacion(view: View?) {
        val gmmIntentUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query="+ direccion!!.getDouble("latitude")  +"%2C"+
                     + direccion!!.getDouble("longitude")
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(mapIntent)
    }

    inner class ws: AsyncTask<String?, String?, JSONObject>() {
        override fun onPreExecute() {
            super.onPreExecute()
            d!!.progressShow(this@InicioActivity, getString(R.string.str_espere))
        }

        override fun doInBackground(vararg params: String?): JSONObject? {
            val ws = WebServiceCall()
            return ws.WebServiceGetCall(params[0], params[1]!!)
        }

        override fun onPostExecute(result: JSONObject) {
            try {
                d!!.progressHide()
                if(result.length() != 0) {
                    var usuario = result.getJSONArray("results")
                    llena_perfil(usuario)
                }
                else{
                    d!!.alert("", getString(R.string.str_sin_datos), this@InicioActivity)
                }
            } catch (e: Exception) {
                d!!.progressHide()
                d!!.alert("", e.message, this@InicioActivity)
            }
        }
    }


    class DownloadAsyncTask :AsyncTask<String?, Void?, Bitmap?>() {
         override fun doInBackground(vararg params: String?): Bitmap? {
            var bmp_area: Bitmap? = null
            try {
                val imageURL = URL(params[0])
                bmp_area = BitmapFactory.decodeStream(imageURL.openStream())
            } catch (e: IOException) {
                // TODO: handle exception
                Log.e("error", "Downloading Image Failed" + e.message)
                //viewHolder.bmp_area = null;
            }
            return bmp_area
        }

        override fun onPostExecute(result: Bitmap?) {
            d!!.progressHide()
            img!!.setImageBitmap(result)
            //Bitmap res = result;
        }
    }
}
package mx.easycool.com

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import mx.easycool.justochallenge.R

class Dialogos {
    var alert: AlertDialog? = null

    fun alert(title: String?, message: String?, context: Context) {
        val d = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        d.setView(
            LayoutInflater.from(context).inflate(R.layout.layout_alert_aviso, null, false)
        )
        val di = d.create()
        di.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        di.show()
        di.setCancelable(false)
        val txt_mensaje = di.findViewById<View>(R.id.text_msj) as TextView
        val btn_aceptar = di.findViewById<View>(R.id.btn_ok) as Button

        btn_aceptar.setOnClickListener {
            try {
                di.hide()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        txt_mensaje.text = message
        txt_mensaje.maxLines = 6
        txt_mensaje.textSize = 14f
        /*d.setTitle(title)
        d.setMessage(message)
        d.setNegativeButton(R.string.str_ok, null)
        val di = d.create()
        di.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        di.show()*/
        val metrics = context.resources.displayMetrics
        val w = metrics.widthPixels
        val wr = w - 100
        di.window!!.setLayout(wr, di.window!!.attributes.height)
    }

    fun alertAviso(title: String?, message: String?, context: Context) {
        val d = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        d.setTitle(title)
        d.setMessage(message)
        d.setCancelable(false)
        val di = d.create()
        di.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        di.show()
        val metrics = context.resources.displayMetrics
        val w = metrics.widthPixels
        val wr = w - 100
        di.window!!.setLayout(wr, di.window!!.attributes.height)
    }

    fun progressShow(context: Context, mensaje: String?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.layout_progressbar, null)
        val texto = itemView.findViewById<TextView>(R.id.progressBarMessage)
        texto.text = mensaje
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(itemView)
        dialogBuilder.setCancelable(false)
        alert = dialogBuilder.create()
        alert!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert!!.show()
        val metrics = context.resources.displayMetrics
        val w = metrics.widthPixels
        val wr = w - 100
        alert!!.getWindow()!!.setLayout(wr, alert!!.getWindow()!!.attributes.height)
    }

    fun progressHide() {
        if (alert != null) {
            alert!!.dismiss()
        }
    }
}
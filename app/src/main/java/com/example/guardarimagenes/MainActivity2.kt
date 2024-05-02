package com.example.guardarimagenes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity2 : AppCompatActivity() {
    private lateinit var img1: ImageView
    private lateinit var spn1: Spinner
    private lateinit var txt2: TextView
    private lateinit var requestQueue: RequestQueue

    lateinit var stringRequest: StringRequest

    lateinit var objetoJSON: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        img1 = findViewById(R.id.image1)
        txt2 = findViewById(R.id.textView2)
        spn1 = findViewById(R.id.spn1)

        requestQueue = Volley.newRequestQueue(this)

        val url = "http://mipaginafg.atwebpages.com/consultarImagen.php"

        Log.d("URL", "URL de la solicitud: $url")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val nombresList = ArrayList<String>()
                for (i in 0 until response.length()) {
                    val nombre = response.getString(i)
                    nombresList.add(nombre)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spn1.adapter = adapter

                spn1.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedName = parent?.getItemAtPosition(position).toString()
                        mostrarImagen(selectedName) // llama a la función para mostrar la imagen
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // no se selecciona nada en el Spinner
                    }
                })
            },
            { error ->
                error.printStackTrace()
            }
        )

        // Agregar la solicitud Volley al RequestQueue
        requestQueue.add(jsonArrayRequest)

        Log.d("Solicitud Volley", "Solicitud Volley ejecutada")
    }

    private fun mostrarImagen(nombre: String) {
        val urlImagen = "http://mipaginafg.atwebpages.com/obtenerImagen.php"

        requestQueue = Volley.newRequestQueue(this@MainActivity2)

        stringRequest = object : StringRequest(Method.POST, urlImagen,
            Response.Listener { response ->
                objetoJSON = JSONObject(response)
                val imgString = objetoJSON.getJSONArray("datos").getJSONObject(0).getString("img")
                val bitmap = StringToBitMap(imgString)
                img1.setImageBitmap(bitmap)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros["campo1"] = nombre
                return parametros
            }
        }

        requestQueue.add(stringRequest)
    }

    companion object {
        fun StringToBitMap(encodedString: String?): Bitmap? {
            try {
                val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message
                return null
            }
        }
    }
}



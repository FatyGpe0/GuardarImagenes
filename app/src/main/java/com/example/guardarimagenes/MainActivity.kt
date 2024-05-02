package com.example.guardarimagenes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var caja1: EditText
    lateinit var btnGuarda: Button
    lateinit var btn2: Button
    lateinit var img1: ImageView
    lateinit var requestQueue: RequestQueue
    lateinit var stringRequest: StringRequest

    lateinit var objetoJSON:JSONObject

    var url: String = "http://mipaginafg.atwebpages.com/guardarImagen.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        caja1 = findViewById(R.id.editText1)
        btnGuarda = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        img1 = findViewById(R.id.imageView1)

        img1.setOnClickListener {
            openGallery()
        }

        btn2.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }


        btnGuarda.setOnClickListener {
            val bmpDrawable: BitmapDrawable
            bmpDrawable = img1.drawable as BitmapDrawable

            val imgBMP: Bitmap

            //reedimensionar de imagen
            imgBMP = redimensionar(bmpDrawable.bitmap)

            //cambiar formato de compresion
            val imgComprimida = cambiarformatodecompresion(imgBMP)

            var imgString = bitMapToString(imgBMP)

            requestQueue = Volley.newRequestQueue(this@MainActivity)

            stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener {
                    objetoJSON = JSONObject(it)

                    if(objetoJSON.has("datos")){
                        val msg = objetoJSON.getJSONArray("datos").getJSONObject(1).getJSONObject("0").getString("mensaje3.2").toString()
                        //val msg = objetoJSON.getJSONArray("datos").getJSONObject(1).getJSONObject("0").getJSONObject("0").getString("mensaje3.1.1").toString()
                        //val msg = objetoJSON.getJSONArray("datos").getJSONObject(0).getString("mensajeUNO").toString()
                        //val msg = objetoJSON.getString("mensajeUNO").toString()


                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MainActivity, "Error al Guardar la Imagen", Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {

                }){
                override fun getParams(): HashMap<String, String>? {
                    var parametros: HashMap<String, String> = HashMap()

                    parametros["campo1"] = caja1.text.toString()
                    parametros["imagen1"] = imgString.toString()

                    return parametros
                }
            }

            requestQueue.add(stringRequest)
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data

            img1.setImageURI(selectedImage)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        fun bitMapToString(bitmap: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun cambiarformatodecompresion(bitmap: Bitmap?): Bitmap?{
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val bytesOriginales = outputStream.toByteArray()

            val bitmapNuevo = BitmapFactory.decodeByteArray(bytesOriginales, 0, bytesOriginales.size)

            return bitmapNuevo

        }

        fun redimensionar(bitmap: Bitmap): Bitmap {

            /*val ratio = if (bitmap.width > bitmap.height) {
                bitmap.width / 250
            } else {
                bitmap.height / 250
            }

            val imageWith = bitmap.width / ratio
            val imageHeight = bitmap.height / ratio*/

            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            var width: Int
            var height: Int

            if (aspectRatio > 1) {
                width = 200
                height = (width / aspectRatio).toInt()
            }
            else {
                height = 200
                width = (height * aspectRatio).toInt()
            }


            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }
}
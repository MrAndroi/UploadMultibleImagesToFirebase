package com.shorman.firebasekotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var storageReference:StorageReference
    var uri:MutableList<Uri> = ArrayList()
    val REQUEST_CODE_IMAGE_PICK = 1
    var imageRef = Firebase.storage.reference
    var dataBseRef = Firebase.database.reference
    lateinit var progressDialog:ProgressDialog
    var  url:MutableList<String>  = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this@MainActivity)


        storageReference = FirebaseStorage.getInstance().getReference("photos")

        openGallaryButton.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        uploadImages.setOnClickListener {
           uploadImagesToFireBase()
        }

        upleadToDatabase.setOnClickListener {
            val msg = msg(url,"dsadsa")
            dataBseRef.child("sami").setValue(msg)
        }

    }

    private fun uploadImagesToFireBase() = CoroutineScope(Dispatchers.IO).launch {
        try {
            for (i in 0..uri.size+1){
                imageRef.child("Photos").child(uri[i].lastPathSegment.toString()).putFile(uri[i]).addOnProgressListener {
                    progressDialog.setTitle("Kotlin Progress Bar")
                    progressDialog.setMessage("${it.totalByteCount}+${it.bytesTransferred}")
                    progressDialog.show()

                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                        url.add(it.result.toString())
                    }

                }.addOnCompleteListener {
                    progressDialog.dismiss()

                }.await()

                Log.e("x=",url.size.toString())
        }

        }catch (e:Exception){
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK){

            if (data?.clipData != null){
                val count = data.clipData!!.itemCount
                for (i in 0..count-1){
                    uri.add(data.clipData!!.getItemAt(i).uri)
                }
                imageView1.setImageURI(uri[1])

            }
        }
    }

}
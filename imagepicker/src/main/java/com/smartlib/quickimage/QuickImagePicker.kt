package com.smartlib.quickimage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

// Created by monu meena (5 january 2021)

object QuickImagePicker {
    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    fun getWithoutCompressImage(context: Context, uri: Uri): String? {
        var uri = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getCompressImageGallery(uriForGallery: Uri?, foldername: String = "Download", context: Context): File {
        val IMAGE_DIRECTORY = "/${foldername}"
        val myBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uriForGallery)
        var file: File? = null
        val bytes = ByteArrayOutputStream()
        myBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            file = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
            file!!.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context, arrayOf(file!!.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.e("cdnbchjd", file!!.absolutePath)
            // return file!!.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return file!!
    }

    private fun getCompressImageCamera(uriForCamera: Bitmap?, foldername: String = "Download", context: Context): File {
        val IMAGE_DIRECTORY = "/${foldername}"
        val myBitmap = uriForCamera
        var file: File? = null
        val bytes = ByteArrayOutputStream()
        myBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            file = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
            file!!.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context, arrayOf(file!!.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.e("cdnbchjd", file!!.absolutePath)
            // return file!!.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return file!!
    }

    fun getCompressImg(uriOfGallery: Uri?, uriOfCamera: Any?, foldername: String = "Download", context: Context): File {
        var file: File? = null
        if (uriOfGallery == null) {
            file = getCompressImageCamera(uriOfCamera as Bitmap?, foldername, context)
        } else {
            file = getCompressImageGallery(uriOfGallery, foldername, context)
        }
        return file
    }

    fun singleImageDialog(context: Context, requestCode: Int) {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
                "Select photo from gallery",
                "Capture photo from camera"
        )
        pictureDialog.setItems(
                pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary(context, requestCode)
                1 -> takePhotoFromCamera(context, requestCode)
            }
        }
        pictureDialog.show()
    }

    fun multiImageDialog(context: Context, requestCode: Int) {
        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent?.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            (context as Activity).startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), requestCode)


        } else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            (context as Activity).startActivityForResult(intent, requestCode);
        }

    }

    fun getListOfFiles(fileList: ClipData?, context: Context, imgLimit: Int): ArrayList<File> {
        val listOfFiles = arrayListOf<File>()
        var count = fileList?.itemCount

        if (count != null) {
            if (count > imgLimit) {
                Toast.makeText(context, "Failed: you can only select max $imgLimit images", Toast.LENGTH_SHORT).show()
            } else {
                for (i in 0 until count) {
                    var imageUri: Uri = fileList?.getItemAt(i)!!.uri
                   // listOfFiles.add(getCompressImg(imageUri, uriOfCamera = null, "", context))
                }
            }
        }

        return listOfFiles


    }


    //************************************below all are the background calling functions   ***************************


    private fun choosePhotoFromGallary(context: Context, requestCode: Int) {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        (context as Activity).startActivityForResult(galleryIntent, requestCode)

    }

    private fun takePhotoFromCamera(context: Context, requestCode: Int) {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        (context as Activity).startActivityForResult(intentCamera, requestCode)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


}
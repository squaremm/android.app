package com.square.android.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import com.square.android.R
import java.io.File
import java.util.ArrayList
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


const val CHOOSE_FILE_RC = 666
const val IMAGE_PICKER_RC = 667

object FileUtils {

    fun getOutputFileUri(context: Context) =
            Uri.fromFile(File(context.externalCacheDir?.path, "pickImageResult.jpeg"))

    fun startFilePickRequest(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        val mimetypes = arrayOf("image/jpeg", "image/pjpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.action = Intent.ACTION_GET_CONTENT
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.choose_photo)), CHOOSE_FILE_RC)
    }

    fun startImagePicker(activity: Activity) {
        activity.startActivityForResult(
                getPickImageChooserIntent(activity), IMAGE_PICKER_RC)
    }

    private fun getPickImageChooserIntent(context: Context): Intent {
        return getPickImageChooserIntent(
                context, context.getString(R.string.choose_photo), includeDocuments = false, includeCamera = true)
    }

    private fun getPickImageChooserIntent(
            context: Context,
            title: CharSequence,
            includeDocuments: Boolean,
            includeCamera: Boolean): Intent {

        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager

        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager))
        }

        var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
        if (galleryIntents.isEmpty()) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
        }
        allIntents.addAll(galleryIntents)

        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }

        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

        return chooserIntent
    }

    private fun isExplicitCameraPermissionRequired(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && hasPermissionInManifest(context, "android.permission.CAMERA")
                && context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
    }

    private fun hasPermissionInManifest(
            context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermissions = packageInfo.requestedPermissions
            if (declaredPermissions != null && declaredPermissions.isNotEmpty()) {
                for (p in declaredPermissions) {
                    if (p.equals(permissionName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }

    fun getCameraIntent(context: Context): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputFileUri(context))
        return intent
    }

    private fun getCameraIntents(context: Context, packageManager: PackageManager): List<Intent> {

        val allIntents = ArrayList<Intent>()

        // Determine Uri of camera image to  save.

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            getOutputFileUri(context)?.run {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
            }
            allIntents.add(intent)
        }

        return allIntents
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     */
    private fun getGalleryIntents(
            packageManager: PackageManager, action: String, includeDocuments: Boolean): List<Intent> {
        val intents = ArrayList<Intent>()
        val galleryIntent = if (action === Intent.ACTION_GET_CONTENT)
            Intent(action)
        else
            Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }

        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent
                                .component!!
                                .className == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        return intents
    }

    /**
     * Get the URI of the selected image from [.getPickImageChooserIntent].<br></br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param data the returned data of the activity result
     */
    fun getPickImageResultUri(context: Context, data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || data!!.data == null) getOutputFileUri(context) else data.data
    }

}
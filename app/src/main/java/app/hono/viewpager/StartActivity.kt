package app.hono.viewpager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_start.*
import java.io.IOException
import java.util.*


class StartActivity : AppCompatActivity() {

    private var filePath: String? = null


    private val realmConfig = RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build()

    private val realm: Realm by lazy {
        Realm.getInstance(realmConfig)
    }

    companion object {
        const val REQUEST_CODE_PHOTO: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //ダイアログ表示
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                // 許可ダイアログで今後表示しないにチェックされていない場合
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1000
                )
            }
        }

        //ギャラリー呼び出し
        galleryButton.setOnClickListener {
            val intent: Intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        //保存ボタンを押したとき
        saveButton.setOnClickListener {
            realm.executeTransaction {
                val diary = it.createObject(Diary::class.java, UUID.randomUUID().toString())
                diary.imageId = filePath.toString()
                diary.menuContent = menuEditText.text.toString()
                diary.memoContent = memoEditText.text.toString()
                diary.date = Date()
                //realm.copyToRealm(diary)
            }

            val intent: Intent = Intent(application, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK) {
            val strDocId: String = DocumentsContract.getDocumentId(data?.data)
            val strSplittedDocId: Array<String> = strDocId.split(":").toTypedArray()
            val strId: String? = strSplittedDocId[strSplittedDocId.size - 1]
            val crsCursor: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.DATA),
                "_id=?",
                arrayOf(strId),
                null
            )
            crsCursor?.moveToFirst()
            filePath = crsCursor?.getString(0)

            val uri: Uri? = data?.data
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

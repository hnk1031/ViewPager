package app.hono.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.hono.viewpager.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults


class MainActivity : AppCompatActivity() {

    private val realmConfig = RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build()

    private val realm: Realm by lazy {
        Realm.getInstance(realmConfig)
    }

    lateinit private var binding: ActivityMainBinding
    /*private val images:IntArray = intArrayOf(
        R.drawable.image01,
        R.drawable.image02,
        R.drawable.image03
    ) */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val diaryList = readAll()

        val diaryMutableList = diaryList.toMutableList()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.adapter =
            CustomPagerAdapter(this, diaryMutableList)

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


    fun create(imageId: String, menuContext: String, memoContext: String) {

    }

    fun readAll(): RealmResults<Diary> {
        return realm.where(Diary::class.java).findAll()
    }

    /*fun deleteAll() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }

    fun delete(diary: Diary){
        realm.executeTransaction {
            diary.deleteFromRealm()
        }
    } */
}

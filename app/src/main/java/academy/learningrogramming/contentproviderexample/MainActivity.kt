package academy.learningrogramming.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadyContactPermission = ContextCompat.checkSelfPermission(this,READ_CONTACTS)
        Log.d(TAG, ".onCreate: checkSelfPermission returned $hasReadyContactPermission")

        if (hasReadyContactPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, ".onCreate: permission granted")
            readGranted = true  // TODO don't do this!!
        } else {
            Log.d(TAG, ".onCreate: requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick: starts")
            val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            if (readGranted) {
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()  // create a list to hold our contacts
                cursor?.use {
                    // loop through the cursor
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }

                val adapter =
                    ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
                contact_names.adapter = adapter
            } else {
                Snackbar.make(view, "Please grant access to your Contacts", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            Log.d(TAG, "fab onClick: ends")
        }
        Log.d(TAG, ".onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, ".onRequestPermissionsResult: starts")
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, ".onRequestPermissionsResult: permission granted")
                    true
                } else {
                    // permission denied
                    Log.d(TAG, ".onRequestPermissionsResult: permission reused")
                    false
                }

//                fab.isEnabled = readGranted
            }
        }
        Log.d(TAG, ".onRequestPermissionsResult: ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

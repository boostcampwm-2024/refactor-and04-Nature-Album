package com.and04.naturealbum

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.getDatabase(applicationContext)
        getAll(db)

//        val onclick = { addUser(userDao) }

        setContent {
//            TestButton(onclick)
//            GetButton {
//                Log.d("Room DB test", "${userDao.getAll()}")
//                userDao.getAll()
//            }
        }

    }

    override fun onResume() {
        super.onResume()
    }
}

private fun getAll(db: AppDatabase) = CoroutineScope(Dispatchers.IO).launch {
    val userDao = db.userDao()
    val users: List<User> = userDao.getAll()
    Log.d("MainActivity", "onCreate: $users")
}

private fun addUser(userDao: UserDao){
    val user = User(1, "lh99j", "hyungjun")
    userDao.insertAll(user)
}


@Composable
fun TestButton(onClick: () -> Unit) {
    Button(onClick = onClick){
        Text("test")
    }
}

@Composable
fun GetButton(onClick: () -> Unit){
    Button(onClick = onClick){
        Text("Get")
    }
}
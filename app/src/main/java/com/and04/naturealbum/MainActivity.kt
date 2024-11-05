package com.and04.naturealbum

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.room.Room

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        val userDao = db.userDao()
        val users: List<User> = userDao.getAll()

        val onclick = { addUser(userDao) }

        setContent {
            TestButton(onclick)
            GetButton {
                Log.d("Room DB test", "${userDao.getAll()}")
                userDao.getAll()
            }
        }

    }
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
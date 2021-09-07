package xyz.fcr.sberrunner.data.repository.firestorage

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.data.model.RunEntity

interface IStorageRepository {
    fun savePicture()
}
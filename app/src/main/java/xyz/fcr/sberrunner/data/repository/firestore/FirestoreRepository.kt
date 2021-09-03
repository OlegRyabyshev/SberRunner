package xyz.fcr.sberrunner.data.repository.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val runDao: RunDao,
    private val schedulersProvider: ISchedulersProvider
) : IFirestoreRepository {

    override fun syncWithCloud() {
        Single.fromCallable { runDao.getAllRuns() }
            .subscribeOn(schedulersProvider.io())
            .subscribe({ internalListOfRuns ->

                Single.fromCallable { getListFromCloud() }
                    .subscribeOn(schedulersProvider.io())
                    .subscribe({ externalListOfRuns ->
                        performSync(internalListOfRuns, externalListOfRuns)
                    }, {
                        throw it
                    })

            }, {
                throw it
            })
    }

    private fun getListFromCloud(): List<Run> {
//
//        val document = fireStore.collection(Constants.USER).document(firebaseAuth.uid!!)
//
//        docRef.get().addOnSuccessListener { doc ->
//            if (doc != null) {
//
//            } else {
//
//            }
//        }
//            .addOnFailureListener { exception ->
//                throw exception
//            }

        return emptyList()
    }

    private fun performSync(internalListOfRuns: List<Run>?, externalListOfRuns: List<Run>?) {

    }

    private fun removeFromRoom() {

    }

    private fun addToRoom() {

    }

    private fun removeFromFirestore() {

    }

    private fun addToFirestore() {

    }
}
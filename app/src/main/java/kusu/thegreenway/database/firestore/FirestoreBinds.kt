package kusu.thegreenway.database.firestore

import dagger.Binds
import dagger.Module
import kusu.thegreenway.database.Database

@Module
abstract class FirestoreBinds {

    @Binds abstract fun bindFirestore(firestoreDB: FirestoreDatabase): Database
}
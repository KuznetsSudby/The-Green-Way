package kusu.thegreenway.ui.main

import androidx.lifecycle.ViewModel
import kusu.thegreenway.database.DataBaseManager

class MainViewModel : ViewModel() {

    // TODO: Implement the ViewModel

    init {
        if (!DataBaseManager.isLoaded){
            DataBaseManager.loadData()
        }
    }
}

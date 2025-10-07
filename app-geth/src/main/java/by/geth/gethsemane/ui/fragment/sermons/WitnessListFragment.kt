package by.geth.gethsemane.ui.fragment.sermons

import androidx.lifecycle.ViewModelProvider
import by.geth.gethsemane.data.Witness

class WitnessListFragment: BaseSermonListFragment<Witness>() {
    override fun getViewModel(): BaseSermonListViewModel<Witness> {
        return ViewModelProvider(this).get(WitnessListViewModel::class.java)
    }
}

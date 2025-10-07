package by.geth.gethsemane.ui.fragment.sermons

import androidx.lifecycle.ViewModelProvider
import by.geth.gethsemane.data.Sermon

class SermonListFragment: BaseSermonListFragment<Sermon>() {
    override fun getViewModel(): BaseSermonListViewModel<Sermon> {
        return ViewModelProvider(this).get(SermonListViewModel::class.java)
    }
}

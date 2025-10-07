package by.geth.gethsemane.ui.fragment.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.geth.gethsemane.R
import by.geth.gethsemane.databinding.FragmentDownloadsBinding
import by.geth.gethsemane.ui.fragment.base.AudioFragment
import com.google.android.material.tabs.TabLayout

class DownloadsFragment: AudioFragment() {
    companion object {
        fun newInstance(): DownloadsFragment {
            val fragment = DownloadsFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARGS_AB_TITLE_RES_ID, R.string.downloads)
                putBoolean(ARGS_AB_HAS_ELEVATION, false)
                putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP)
            }
            return fragment
        }
    }

    private lateinit var viewBinding: FragmentDownloadsBinding

    private val sermonsFragment = DownloadsPageFragment.newInstance(DownloadsPageFragment.DownloadsType.SERMON)
    private val witnessesFragment = DownloadsPageFragment.newInstance(DownloadsPageFragment.DownloadsType.WITNESS)
    private val songsFragment = DownloadsPageFragment.newInstance(DownloadsPageFragment.DownloadsType.SONG)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewPager.adapter = PagerAdapter()

        viewBinding.viewPager.registerOnPageChangeCallback(object:
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                viewBinding.tabLayout.getTabAt(position)?.select()
            }
        })

        viewBinding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewBinding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun updateAudioUI() {
        sermonsFragment.updateUiState()
        witnessesFragment.updateUiState()
        songsFragment.updateUiState()
    }

    override fun updateDownloadUI() {
        sermonsFragment.updateUiState()
        witnessesFragment.updateUiState()
        songsFragment.updateUiState()
    }

    private inner class PagerAdapter: FragmentStateAdapter(this) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> sermonsFragment
                1 -> witnessesFragment
                2 -> songsFragment
                else -> throw IllegalArgumentException()
            }
        }
    }
}

package tehnolog.jubim.view.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tehnolog.jubim.BuildConfig
import tehnolog.jubim.R
import tehnolog.jubim.databinding.FragmentAboutBinding
import tehnolog.jubim.view.base.BaseFragment

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel>() {
    override val viewModel: AboutViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        appVersion.text = getString(
            R.string.text_app_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
    }

    private fun launchBrowser(url: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
            startActivity(it)
        }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAboutBinding.inflate(inflater, container, false)
}

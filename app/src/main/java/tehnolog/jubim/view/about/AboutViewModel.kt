package tehnolog.jubim.view.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AboutViewModel : ViewModel() {
    private val _url = MutableStateFlow("https://github.com/merdanch")
    val url: StateFlow<String> = _url

}

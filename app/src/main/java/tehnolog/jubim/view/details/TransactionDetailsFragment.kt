package tehnolog.jubim.view.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cleanTextContent
import dagger.hilt.android.AndroidEntryPoint
import hide
import turkmenManat
import tehnolog.jubim.model.Transaction
import tehnolog.jubim.utils.saveBitmap
import tehnolog.jubim.utils.viewState.DetailState
import tehnolog.jubim.view.base.BaseFragment
import tehnolog.jubim.view.main.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collect
import show
import tehnolog.jubim.R
import tehnolog.jubim.databinding.FragmentTransactionDetailsBinding
import turkmenManatDetail

@AndroidEntryPoint
class TransactionDetailsFragment : BaseFragment<FragmentTransactionDetailsBinding, TransactionViewModel>() {
    private val args: TransactionDetailsFragmentArgs by navArgs()
    override val viewModel: TransactionViewModel by activityViewModels()

    // handle permission dialog
    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) shareImage() else showErrorDialog()
        }

    private fun showErrorDialog() =
        findNavController().navigate(
            TransactionDetailsFragmentDirections.actionTransactionDetailsFragmentToErrorDialog(
                "Image share failed!",
                "You have to enable storage permission to share transaction as Image"
            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transaction = args.transaction
        getTransaction(transaction.id)
        observeTransaction()
    }

    private fun getTransaction(id: Int) {
        viewModel.getByID(id)
    }

    private fun observeTransaction() = lifecycleScope.launchWhenCreated {

        viewModel.detailState.collect { detailState ->

            when (detailState) {
                DetailState.Loading -> {
                }
                is DetailState.Success -> {
                    onDetailsLoaded(detailState.transaction)
                }
                is DetailState.Error -> {
                    toast("Error forever")
                }
                DetailState.Empty -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun onDetailsLoaded(transaction: Transaction) = with(binding.transactionDetails) {
        title.text = transaction.title
//        amount.text = "${transaction.amount} TMT"
        amount.text = turkmenManatDetail(transaction.amount).cleanTextContent + " TMT"
        type.text = transaction.transactionType
        tag.text = transaction.tag
        date.text = transaction.date
        note.text = transaction.note
        createdAt.text = transaction.createdAtDateFormat

        binding.editTransaction.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("transaction", transaction)
            }
            findNavController().navigate(
                R.id.action_transactionDetailsFragment_to_editTransactionFragment,
                bundle
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteByID(args.transaction.id)
                    .run {
                        findNavController().navigateUp()
                    }
            }
            R.id.action_share_text -> shareText()
            R.id.action_share_image -> shareImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareImage() {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        // unHide the app logo and name
        showAppNameAndLogo()
        val imageURI = binding.transactionDetails.detailView.drawToBitmap().let { bitmap ->
            hideAppNameAndLogo()
            saveBitmap(requireActivity(), bitmap)
        } ?: run {
            toast("Error occurred!")
            return
        }

        val intent = ShareCompat.IntentBuilder(requireActivity())
            .setType("image/jpeg")
            .setStream(imageURI)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }

    private fun showAppNameAndLogo() = with(binding.transactionDetails) {
        appIconForShare.show()
        appNameForShare.show()
    }

    private fun hideAppNameAndLogo() = with(binding.transactionDetails) {
        appIconForShare.hide()
        appNameForShare.hide()
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("StringFormatMatches")
    private fun shareText() = with(binding) {
        val shareMsg = getString(
            R.string.share_message,
            transactionDetails.title.text.toString(),
            transactionDetails.amount.text.toString(),
            transactionDetails.type.text.toString(),
            transactionDetails.tag.text.toString(),
            transactionDetails.date.text.toString(),
            transactionDetails.note.text.toString(),
            transactionDetails.createdAt.text.toString()
        )

        val intent = ShareCompat.IntentBuilder(requireActivity())
            .setType("text/plain")
            .setText(shareMsg)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
}

package tl.bnctl.banking.ui.banking.fragments.cards.credit_card_statement

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.model.Card
import tl.bnctl.banking.data.cards.model.CreditCardStatement
import tl.bnctl.banking.databinding.FragmentCreditCardStatementBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import tl.bnctl.banking.ui.banking.fragments.statements.filter.StatementsFilterFragmentDirections
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.DialogFactory
import java.io.*
import java.util.*

class CreditCardStatementFragment : BaseFragment() {
    companion object {
        const val TAG = "CreditCardStatements"
        const val CARD_ARGUMENT = "card"
        const val CHANNEL_ID = "creditCardStatementsDownloadNotificationChannel"
    }

    private var _binding: FragmentCreditCardStatementBinding? = null
    private val binding get() = _binding!!

    private val creditCardStatementViewModel: CreditCardStatementViewModel by viewModels { CreditCardStatementViewModelFactory() }
    private var isLoadingCardStatement = false

    private lateinit var statementViewAdapter: CreditCardStatementViewAdapter
    private lateinit var card: Card

    // Download notification stuff
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditCardStatementBinding.inflate(layoutInflater)

        card = requireArguments().get(CARD_ARGUMENT) as Card
        statementViewAdapter =
            CreditCardStatementViewAdapter(
                creditCardStatementViewModel.creditCardStatement,
                onDownloadStatementClick
            )
        initUI()
        setUpObservers()
        setUpStatementsFilter()
        isLoadingCardStatement = true
        return binding.root
    }

    private val onDownloadStatementClick: (CreditCardStatement) -> Unit = {
        if (creditCardStatementViewModel.downloadInProgress.value!!) {
            Log.d(
                TAG,
                "Trying to download while download is still in progress. We don't do that here"
            )
        } else {
            creditCardStatementViewModel.setChosenCardStatement(it);
            launchStatementFileChooser()
        }
    }

    private val pickStatementFileLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    showDownloadingStatementMessage();
                    creditCardStatementViewModel.setStatementFileDestination(result.data?.data)
                    creditCardStatementViewModel.downloadCreditCardStatement()
                }
            }
        }

    private fun setUpStatementsFilter() {
        binding.fragmentStatementsFilterButton.setOnClickListener {
            requireView().findNavController()
                .navigate(
                    StatementsFilterFragmentDirections.actionGlobalNavFragmentStatementsFilter(
                        CreditCardStatementFragment::class.simpleName.toString(),
                        creditCardStatementViewModel.statementsFilter.value
                    )
                )
        }
        parentFragmentManager.setFragmentResultListener(
            CreditCardStatementFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            result.getParcelable<StatementsFilterData>("filter")?.let {
                creditCardStatementViewModel.setFilter(it)
                creditCardStatementViewModel.raiseShouldFetchStatementsFlag()
            }
        }
        creditCardStatementViewModel.statementsFilter.observe(viewLifecycleOwner) {
            val startDate = DateUtils.formatDate(requireContext(), it.startDate)
            val endDate = DateUtils.formatDate(requireContext(), it.endDate)
            binding.fragmentStatementsFilterRange.text = requireContext().resources
                .getString(R.string.statements_range).format(
                    startDate,
                    endDate
                )
        }
    }

    private fun initUI() {
        binding.fragmentCreditCardStatementsRecyclerView.adapter = statementViewAdapter

        binding.toolbarTitle.text = requireContext().resources.getString(
            R.string.credit_cards_statements_title,
            card.cardNumber!!.substring(card.cardNumber!!.length - 7)
        )

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        setupDownloadNotification()
    }

    private fun setUpObservers() {
        creditCardStatementViewModel.creditCardStatement.observe(viewLifecycleOwner) {
            hideLoading()
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_credit_card_statements)
            }
            if (it is Result.Success) {
                val statementsData = it.data
                if (statementsData.isEmpty()) {
                    binding.fragmentStatementsNoItemsMessage.visibility = View.VISIBLE
                    binding.fragmentCreditCardStatementsRecyclerView.visibility = View.GONE
                } else {
                    statementViewAdapter.notifyItemRangeChanged(0, statementsData.size)
                    binding.fragmentStatementsNoItemsMessage.visibility = View.GONE
                    binding.fragmentCreditCardStatementsRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        creditCardStatementViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                val card = requireArguments().get(CARD_ARGUMENT) as Card
                val cardNumber = card.cardNumber!!
                isLoadingCardStatement = true
                val filter = creditCardStatementViewModel.statementsFilter.value!!
                creditCardStatementViewModel.getCreditCardStatement(
                    cardNumber,
                    filter.startDate,
                    filter.endDate
                )
                showLoading()
                creditCardStatementViewModel.dropShouldFetchStatementsFlag()
            }
        }

        // This is changed after the file is returned from the backend
        creditCardStatementViewModel.creditCardStatementFileInputStream.observe(viewLifecycleOwner) { it ->
            /*
             * First download the statement to a temporary file, then copy it to the location chosen by the user.
             * The logic behind this is that Allianz's backend is unpredictable at best and it might return an error at any point.
             * By saving the statement as a temporary file, we guarantee that it's completely downloaded
             */
            if (it is Result.Success) {
                val tempStatementFile =
                    File.createTempFile("alz", ".creditCardStatement", requireContext().cacheDir)
                val statementFileUri = tempStatementFile.toUri()
                var tempFileOutputStream: OutputStream? = null
                val downloadedFileInputStream: InputStream = it.data

                // Set the temp file to be deleted on exit, so we dont clutter stuff
                tempStatementFile.deleteOnExit()

                try {
                    tempFileOutputStream = requireContext().contentResolver.openOutputStream(
                        statementFileUri
                    )
                    // Write downloaded file to the temp file
                    val tempBuffer = ByteArray(1024)
                    var readLength: Int

                    while (downloadedFileInputStream.read(tempBuffer)
                            .also { readLength = it } > 0
                    ) {
                        tempFileOutputStream?.write(tempBuffer, 0, readLength)
                    }

                    creditCardStatementViewModel.setStatementTempFileDestination(statementFileUri)
                    // Statement is downloaded and written to the temp file. Now launch the activity to chose where to save the file
                } catch (e: Exception) {
                    Log.e(TAG, "Error downloading statement: $e")
                    e.printStackTrace()
                    showDownloadingStatementError()
                } finally {
                    try {
                        downloadedFileInputStream.close()
                        tempFileOutputStream?.close()
                    } catch (e: IOException) {
                        // Always look on the bright side of life
                    }
                }
            } else if (it is Result.Error) {
                handleResultError(it, R.string.error_downloading_credit_card_statement)
                showDownloadingStatementError()
            }

        }

        creditCardStatementViewModel.statementTempFileDestination.observe(viewLifecycleOwner) {
            if (it != null) {
                val statementFileUri = creditCardStatementViewModel.statementFileDestination.value!!
                val tempFile: File =
                    creditCardStatementViewModel.statementTempFileDestination.value!!.toFile()
                var outputStream: OutputStream? = null
                var inputStream: InputStream? = null
                try {
                    Log.d(TAG, "Writing statement file")
                    outputStream = requireContext().contentResolver.openOutputStream(
                        statementFileUri
                    )
                    inputStream = FileInputStream(tempFile)
                    inputStream.copyTo(outputStream!!)
                    inputStream.close()
                    showDownloadingStatementDoneMessage()
                } catch (e: Exception) {
                    showDownloadingStatementError()
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                    creditCardStatementViewModel.setStatementTempFileDestination(null)
                    creditCardStatementViewModel.setStatementFileDestination(null)
                }
            }

        }
    }

    private fun showDownloadingStatementError() {
        DialogFactory.createCancellableDialog(
            requireContext(),
            R.string.error_downloading_credit_card_statement
        ).show()
        dismissDownloadNotification()
    }

    private fun launchStatementFileChooser() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
            putExtra(
                Intent.EXTRA_TITLE,
                creditCardStatementViewModel.chosenCardStatement.value!!.fileName
            )
        }
        pickStatementFileLocation.launch(intent)
    }

    private fun dismissDownloadNotification() {
        with(NotificationManagerCompat.from(requireContext())) {
            // notificationId is a unique int for each notification that you must define
            cancelAll()
        }
    }

    private fun showDownloadingStatementDoneMessage() {
        Toast.makeText(
            requireContext(),
            R.string.credit_card_statement_downloading_file_complete,
            Toast.LENGTH_LONG
        ).show()
        dismissDownloadNotification()
    }

    private fun showDownloadingStatementMessage() {
        with(NotificationManagerCompat.from(requireContext())) {
            notify(getDownloadNotificationId(), notificationBuilder!!.build())
        }
    }

    /**
     * Each notification needs a unique id, but currently we're only handling a single file download
     */
    private fun getDownloadNotificationId(): Int {
        if (notificationId == null) {
            notificationId = Random().nextInt()
        }
        return notificationId!!
    }

    private fun setupDownloadNotification() {
        notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_drop_down_arrow)
            .setContentTitle(getString(R.string.credit_card_statement_downloading_statement))
            .setContentText(getString(R.string.credit_card_statement_downloading_please_wait))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, 0, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVibrate(LongArray(0))
        createDownloadNotificationChannel()
    }

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library
     */
    private fun createDownloadNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name =
                getString(R.string.credit_card_statement_downloading_file_notification_channel_name)
            val descriptionText =
                getString(R.string.credit_card_statement_downloading_file_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

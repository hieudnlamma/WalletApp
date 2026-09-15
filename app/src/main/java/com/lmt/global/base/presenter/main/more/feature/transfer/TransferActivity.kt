package com.lmt.global.base.presenter.main.more.feature.transfer

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.data.mapper.RecipientMapper
import com.lmt.global.base.databinding.ActivityTransferBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.matchesSearchQuery
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.onSearchQueryChanged
import com.lmt.global.base.model.Recipient
import com.lmt.global.base.presenter.main.more.adapter.RecipientsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferActivity : IActivity<ActivityTransferBinding, TransferViewModel>() {
    private val frequentRecipientsAdapter = RecipientsAdapter(::onRecipientClick)
    private val allRecipientsAdapter = RecipientsAdapter(::onRecipientClick)
    private var frequentRecipients = emptyList<Recipient>()
    private var allRecipients = emptyList<Recipient>()
    private var searchQuery = ""

    override fun provideViewModel() = viewModel<TransferViewModel>()
    override fun provideLayout() = R.layout.activity_transfer

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
        setupRecipients()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewBinding.imvAdd.onDebounceClick {
            startActivity(Intent(this, TransferToBeneficiaryActivity::class.java))
        }
        viewBinding.edtSearchContact.onSearchQueryChanged { query ->
            searchQuery = query
            renderRecipients()
        }
    }

    override fun initObservers() {
        super.initObservers()
        collectLatestRepeatOnLifecycle(viewModel.frequentRecipients) { recipients ->
            frequentRecipients = recipients.map(RecipientMapper::toModel)
            renderRecipients()
        }
        collectLatestRepeatOnLifecycle(viewModel.allRecipients) { recipients ->
            allRecipients = recipients.map(RecipientMapper::toModel)
            renderRecipients()
        }
    }

    private fun setupRecipients() {
        viewBinding.rcvListFrequentContacts.adapter = frequentRecipientsAdapter
        viewBinding.rcvListAllContacts.adapter = allRecipientsAdapter

        frequentRecipientsAdapter.submitList(emptyList())
        allRecipientsAdapter.submitList(emptyList())
    }

    private fun renderRecipients() = with(viewBinding) {
        val isSearching = searchQuery.isNotBlank()
        tvFrequentContacts.isVisible = !isSearching
        rcvListFrequentContacts.isVisible = !isSearching
        tvAllContacts.isVisible = !isSearching

        frequentRecipientsAdapter.submitList(if (isSearching) emptyList() else frequentRecipients)
        allRecipientsAdapter.submitList(
            if (isSearching) {
                allRecipients.filter { recipient -> recipient.matchesSearchQuery(searchQuery) }
            } else {
                allRecipients
            }
        )
    }

    private fun onRecipientClick(recipient: Recipient) {
        startActivity(
            Intent(this, TransferToBeneficiaryActivity::class.java).apply {
                putExtra(TransferToBeneficiaryActivity.EXTRA_RECIPIENT_NAME, recipient.name)
                putExtra(TransferToBeneficiaryActivity.EXTRA_RECIPIENT_PHONE, recipient.phoneNumber)
            }
        )
    }
}

package com.lmt.global.base.presenter.main.more.feature

import android.content.Intent
import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityTransferBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.model.Contact
import com.lmt.global.base.presenter.main.more.MoreViewModel
import com.lmt.global.base.presenter.main.more.adapter.ContactsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferActivity : IActivity<ActivityTransferBinding, MoreViewModel>() {
    private val frequentContactsAdapter = ContactsAdapter(::onContactClick)
    private val allContactsAdapter = ContactsAdapter(::onContactClick)

    override fun provideViewModel() = viewModel<MoreViewModel>()
    override fun provideLayout() = R.layout.activity_transfer

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
        setupContacts()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupContacts() {
        viewBinding.rcvListFrequentContacts.adapter = frequentContactsAdapter
        viewBinding.rcvListAllContacts.adapter = allContactsAdapter

        val contacts = listOf(
            createSampleContact(1L),
            createSampleContact(2L),
            createSampleContact(3L),
        )
        frequentContactsAdapter.submitList(contacts.take(2))
        allContactsAdapter.submitList(contacts)
    }

    private fun createSampleContact(id: Long) = Contact(
        id = id,
        name = getString(R.string.ali_ahmed),
        phoneNumber = getString(R.string._1_300_555_0161),
        avatarRes = R.drawable.icn_avatar_default,
    )

    private fun onContactClick(contact: Contact) {
        startActivity(Intent(this, TransferToBeneficiaryActivity::class.java))
    }
}

package kusu.thegreenway.ui.main.account

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_account.*
import kusu.thegreenway.BuildConfig
import kusu.thegreenway.R
import javax.inject.Inject


class AccountFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by activityViewModels<AccountViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionLabel.text = getString(R.string.ui_version_label, BuildConfig.VERSION_NAME.filter { it.isDigit() || it == '.' })
        yandexButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yandex_url))))
        }
        shareButton.setOnClickListener {
            val shareBody = getString(R.string.ui_share_text, BuildConfig.APPLICATION_ID)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    getString(R.string.ui_share_button)
                )
            )
        }
        supportButton.setOnClickListener {
            val shareBody = "\n\n\n" +
                    "Model ${Build.BRAND} ${Build.MODEL}. Android SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}). App version ${BuildConfig.VERSION_NAME}."
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "plain/text"
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(
                Intent.createChooser(
                    sendIntent,
                    getString(R.string.ui_support_button)
                )
            )
        }
        authButton.setOnClickListener { auth() }
        exitButton.setOnClickListener { exit() }

        viewModel.isAuth.observe(viewLifecycleOwner, Observer { isAuth ->
            if (isAuth) {
                authButton.visibility = GONE
                exitButton.visibility = VISIBLE
            } else {
                authButton.visibility = VISIBLE
                exitButton.visibility = GONE
            }
        })

        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            userName.text = user?.displayName ?: getString(R.string.ui_noname_user)
            if (user?.email != null) {
                userEmail.visibility = VISIBLE
                userEmail.text = user.email
            } else {
                userEmail.visibility = GONE
            }
            if (user?.photoUrl != null){
                Picasso.get().load(user.photoUrl).error(R.drawable.ic_no_photo).into(userPhoto)
            } else {
                userPhoto.setImageResource(R.drawable.ic_no_photo)
            }
        })
    }

    private fun exit() {
        AuthUI.getInstance().signOut(requireContext())
    }

    fun auth() {
        val providers = arrayListOf(
            AuthUI
                .IdpConfig
                .GoogleBuilder()
                .build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 1
    }
}

package tech.decentro.udyamwebview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import tech.decentro.udyamwebview.ui.theme.UdyamWebViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdyamWebViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    UdyamRegistrationScreen(
                        sessionUrl = "YOUR_SESSION_URL",
                        // Optional timeouts (in seconds)
                        userInactivityTimeout = 300,
                        pageLoadTimeout = 300,
                        onSessionInitiate = { decentroResponseJsonStr ->
                            Log.d(
                                "UdyamRegistrationScreen",
                                "OTP initiated. Use the following response: $decentroResponseJsonStr to continue the flow"
                            )
                        },
                        onUserInactivity = {
                            Log.d(
                                "UdyamRegistrationScreen",
                                "User has been inactive on the Udyam registration page. Please try again."
                            )
                        },
                        onConnectionError = {
                            Log.d(
                                "UdyamRegistrationScreen",
                                "Udyam Registration page threw connection error. Please try again."
                            )
                        },
                        onPageLoadTimeout = {
                            Log.d(
                                "UdyamRegistrationScreen",
                                "Udyam Registration page has timed out. Please try again."
                            )
                        }
                    )
                }
            }
        }
    }
}
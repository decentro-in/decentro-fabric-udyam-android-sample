# Udyam WebView Integration

This project demonstrates how to use the `UdyamRegistrationScreen` composable to handle Udyam registration via a WebView in an Android Compose application.
The response received in `onSessionInitiate` is the same as documented in the [Initiate Session Decentro API](https://docs.decentro.tech/reference/kyc-and-onboarding-api-reference-identities-verification-services-business-verification-udyam-suite-udyam-registration-initiate-session).

## Usage

### MainActivity.kt

The main entry point of the application, setting up the UI theme and displaying the `UdyamRegistrationScreen`.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdyamWebViewTheme {
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
```

## UdyamRegistrationScreen Composable

The UdyamRegistrationScreen composable displays a WebView that manages the Udyam registration process.

The response received in onSessionInitiate follows the format documented in the Decentro API Reference.

Use this response to continue the Udyam registration flow.

## Running the Project

- Clone this repository.
- Open the project in Android Studio.
- Use the [Initiate Session Decentro API](https://docs.decentro.tech/reference/kyc-and-onboarding-api-reference-identities-verification-services-business-verification-udyam-suite-udyam-registration-initiate-session) without `aadhaar` and `name_on_aadhaar` to generate UIStream session url
- Update the sessionUrl in MainActivity.kt with your actual session URL.
- Run the application on an emulator or a physical device.

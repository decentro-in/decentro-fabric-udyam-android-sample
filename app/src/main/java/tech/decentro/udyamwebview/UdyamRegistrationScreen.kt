package tech.decentro.udyamwebview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun UdyamRegistrationScreen(sessionUrl: String, onSessionInitiate: (decentroResponseJsonStr: String) -> Unit) {

    val udyamUrl = Uri.parse(sessionUrl).getQueryParameter("redirect_url")

    // State variables to track the current URL and loading status
    var currentUrl by remember { mutableStateOf(sessionUrl) }
    var isLoading by remember { mutableStateOf(true) }

    // JavaScript interface
    class UIStreamJsInterface(val onMessage: (message: String) -> Unit) {
        private val payloadMap: MutableMap<String, String> = mutableMapOf()

        @JavascriptInterface
        fun recordPayload(method: String, url: String, payload: String) {
            payloadMap["$method-$url"] = payload
        }

        @JavascriptInterface
        fun postMessage(message: String, targetOrigin: String) {
            onMessage(message)
        }

        fun getPayload(method: String, url: String): String? = payloadMap["$method-$url"]
    }

    val jsInterface = UIStreamJsInterface(onSessionInitiate)

    val webViewClient = remember {
        object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                currentUrl = request?.url.toString()
                view?.post {
                    view.loadUrl(currentUrl)
                }
                return true
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                request?.let {
                    if (request.url.toString() == udyamUrl) {
                        val data = jsInterface.getPayload(request.method, request.url.toString())
                        data?.let {
                            if (data.isNotEmpty()) {
                                view?.post {
                                    view.loadUrl("$sessionUrl&${data.split("__VIEWSTATE").last()}")
                                }
                                return null
                            }
                        }
                    }
                }

                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
                injectJavaScript(view, url)
            }

            private fun injectJavaScript(view: WebView?, url: String?) {
                view?.post {
                    view.evaluateJavascript(
                        """
                        window.parent.postMessage = function(message, targetOrigin) {
                            jsInterface.postMessage(message, targetOrigin);
                        };
                        XMLHttpRequest.prototype.origOpen = XMLHttpRequest.prototype.open;
                        XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
                            this.recordedMethod = method;
                            this.recordedUrl = url;
                            this.origOpen(method, url, async, user, password);
                        };
                        XMLHttpRequest.prototype.origSend = XMLHttpRequest.prototype.send;
                        XMLHttpRequest.prototype.send = function(body) {
                            if(body) jsInterface.recordPayload(this.recordedMethod, this.recordedUrl, body);
                            this.origSend(body);
                        };
                        """.trimIndent(), null)
                }
                view?.addJavascriptInterface(jsInterface, "jsInterface")
            }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                this.webViewClient = webViewClient
                loadUrl(sessionUrl)
            }
        }
    )

    LaunchedEffect(currentUrl) {
        println("URL changed: $currentUrl")
    }

    if (isLoading) {
        // Show loading indicator if isLoading is true
        // ... your loading indicator composable ...
    }
}

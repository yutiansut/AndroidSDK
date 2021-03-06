package it.trade.android.sdk.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import org.junit.jupiter.api.*
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItCryptoOrderParcelableSpec {

    private val linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable = mock()
    private val order = TradeItCryptoOrderParcelable(
        linkedBrokerAccount,
        SymbolPairParcelable("BTC", "USD")
    )

    @BeforeEach
    fun init() {
        whenever(linkedBrokerAccount.tradeItApiClient).thenReturn(mock())
        whenever(linkedBrokerAccount.accountNumber).thenReturn("My account number")
        whenever(linkedBrokerAccount.accountName).thenReturn("My account name")
    }

    @Nested
    inner class PreviewCryptoOrderTestCases {
        @Test
        fun `previewCryptoOrder handles a successful response from trade it`() {
            // given a successful response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            var userDisabledMarginFlag = true
            order.action = TradeItOrderAction.BUY
            order.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
            order.quantity = BigDecimal(1.0)

            whenever(linkedBrokerAccount.tradeItApiClient!!.previewCryptoOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPreviewCryptoOrderResponse>>(1)
                val tradeItPreviewCryptoOrderResponse = TradeItPreviewCryptoOrderResponse()
                tradeItPreviewCryptoOrderResponse.sessionToken = "My session token"
                tradeItPreviewCryptoOrderResponse.longMessages = null
                tradeItPreviewCryptoOrderResponse.status = TradeItResponseStatus.REVIEW_ORDER
                tradeItPreviewCryptoOrderResponse.orderId = "My Order Id"
                tradeItPreviewCryptoOrderResponse.orderDetails = CryptoPreviewOrderDetails()
                tradeItPreviewCryptoOrderResponse.orderDetails.orderAction = "buy"
                tradeItPreviewCryptoOrderResponse.orderDetails.orderPair = "My symbol"
                tradeItPreviewCryptoOrderResponse.orderDetails.orderExpiration = "day"
                tradeItPreviewCryptoOrderResponse.orderDetails.orderQuantity = 1.0
                tradeItPreviewCryptoOrderResponse.orderDetails.orderPriceType = "market"
                tradeItPreviewCryptoOrderResponse.orderDetails.estimatedTotalValue = 25.0
                tradeItPreviewCryptoOrderResponse.orderDetails.orderCommissionLabel = "MyOrderCommissionLabel"
                tradeItPreviewCryptoOrderResponse.orderDetails.orderQuantityType = "MyOrderQuantityType"
                callback.onSuccess(tradeItPreviewCryptoOrderResponse);
            }

            // when calling preview order
            var previewResponse: TradeItPreviewCryptoOrderResponseParcelable? = null
            order.previewCryptoOrder(object : TradeItCallback<TradeItPreviewCryptoOrderResponseParcelable> {
                override fun onSuccess(response: TradeItPreviewCryptoOrderResponseParcelable) {
                    previewResponse = response
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expect the sucess callback called
            Assertions.assertEquals(successfulCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // and the preview response is correctly filled
            previewResponse!!.orderId == "My Order Id"
            previewResponse!!.orderDetails!!.orderAction == "buy"
            previewResponse!!.orderDetails!!.orderPair == "My symbol"
            previewResponse!!.orderDetails!!.orderExpiration == "day"
            previewResponse!!.orderDetails!!.orderQuantity == 1.0
            previewResponse!!.orderDetails!!.orderPriceType == "market"
            previewResponse!!.orderDetails!!.estimatedTotalValue == 25.0
            previewResponse!!.orderDetails!!.orderCommissionLabel == "MyOrderCommissionLabel"
            previewResponse!!.orderDetails!!.orderQuantityType == "MyOrderQuantityType"
        }

        @Test
        fun `previewCryptoOrder handles an error response from trade it`() {
            // given an error response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            order.action = TradeItOrderAction.BUY
            order.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
            order.quantity = BigDecimal(1.0)

            whenever(linkedBrokerAccount.tradeItApiClient!!.previewCryptoOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPreviewCryptoOrderResponse>>(1)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.BROKER_ACCOUNT_ERROR,
                        "My short error message",
                        arrayListOf("My long error message")
                    )
                )
            }

            // when calling preview order
            var errorResult: TradeItErrorResult? = null
            order.previewCryptoOrder(object : TradeItCallback<TradeItPreviewCryptoOrderResponseParcelable> {
                override fun onSuccess(type: TradeItPreviewCryptoOrderResponseParcelable) {
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

            // then expect the success callback to be called
            Assertions.assertEquals(successfulCallbackCount, 0)
            Assertions.assertEquals(errorCallbackCount, 1)

            // and the error is correctly populated
            Assertions.assertEquals(errorResult!!.errorCode, TradeItErrorCode.BROKER_ACCOUNT_ERROR)
            Assertions.assertEquals(errorResult!!.shortMessage, "My short error message")
            Assertions.assertEquals(errorResult!!.longMessages, arrayListOf("My long error message"))
            Assertions.assertEquals(errorResult!!.httpCode, 200)
        }
    }

    @Nested
    inner class PlaceCryptoOrderTestCases {
        @Test
        fun `placeCryptoOrder handles a successful response from trade it`() {
            // given a successful response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0

            whenever(linkedBrokerAccount.tradeItApiClient!!.placeCryptoOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPlaceCryptoOrderResponse>>(1)
                val tradeItPlaceCryptoOrderResponse = TradeItPlaceCryptoOrderResponse()
                tradeItPlaceCryptoOrderResponse.sessionToken = "My session token"
                tradeItPlaceCryptoOrderResponse.longMessages = null
                tradeItPlaceCryptoOrderResponse.status = TradeItResponseStatus.SUCCESS
                tradeItPlaceCryptoOrderResponse.orderNumber = "My Order Id"
                tradeItPlaceCryptoOrderResponse.orderDetails = CryptoTradeOrderDetails()
                tradeItPlaceCryptoOrderResponse.orderDetails.orderAction = "buy"
                tradeItPlaceCryptoOrderResponse.orderDetails.orderPair = "My symbol"
                tradeItPlaceCryptoOrderResponse.orderDetails.orderExpiration = "day"
                tradeItPlaceCryptoOrderResponse.orderDetails.orderQuantity = 1.0
                tradeItPlaceCryptoOrderResponse.orderDetails.orderQuantityType = "MyOrderQuantityType"
                tradeItPlaceCryptoOrderResponse.orderDetails.orderPriceType = "market"
                tradeItPlaceCryptoOrderResponse.confirmationMessage = "Confirmation"
                tradeItPlaceCryptoOrderResponse.broker = "MyBroker"
                tradeItPlaceCryptoOrderResponse.accountBaseCurrency = "MyAccountBaseCurrency"
                tradeItPlaceCryptoOrderResponse.timestamp = "MyTimeStamp"
                callback.onSuccess(tradeItPlaceCryptoOrderResponse);
            }

            // when calling place order
            var placeOrderResponse: TradeItPlaceCryptoOrderResponseParcelable? = null
            order.placeCryptoOrder("My Order Id", object : TradeItCallback<TradeItPlaceCryptoOrderResponseParcelable> {
                override fun onSuccess(response: TradeItPlaceCryptoOrderResponseParcelable) {
                    placeOrderResponse = response
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expect the sucess callback called
            Assertions.assertEquals(successfulCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // and the place order response is correctly filled
            Assertions.assertEquals(placeOrderResponse!!.orderNumber, "My Order Id")
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderAction, "buy")
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderPair, "My symbol")
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderExpiration, "day")
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderQuantity, 1.0)
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderQuantityType, "MyOrderQuantityType")
            Assertions.assertEquals(placeOrderResponse!!.orderDetails!!.orderPriceType, "market")
            Assertions.assertEquals(placeOrderResponse!!.confirmationMessage, "Confirmation")
            Assertions.assertEquals(placeOrderResponse!!.broker, "MyBroker")
            Assertions.assertEquals(placeOrderResponse!!.accountBaseCurrency, "MyAccountBaseCurrency")
            Assertions.assertEquals(placeOrderResponse!!.timestamp, "MyTimeStamp")
        }

        @Test
        fun `placeCryptoOrder handles an error response from trade it`() {
            // given an error response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            whenever(linkedBrokerAccount.tradeItApiClient!!.placeCryptoOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPlaceCryptoOrderResponse>>(1)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.PARAMETER_ERROR,
                        "My short error message",
                        arrayListOf("My long error message")
                    )
                )
            }

            // when: calling preview order
            var errorResult: TradeItErrorResult? = null
            order.placeCryptoOrder("My Order Id", object : TradeItCallback<TradeItPlaceCryptoOrderResponseParcelable> {
                override fun onSuccess(type: TradeItPlaceCryptoOrderResponseParcelable) {
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

            // then expect the sucess callback called
            Assertions.assertEquals(successfulCallbackCount, 0)
            Assertions.assertEquals(errorCallbackCount, 1)

            // and the error is correctly populated
            Assertions.assertEquals(errorResult!!.errorCode, TradeItErrorCode.PARAMETER_ERROR)
            Assertions.assertEquals(errorResult!!.shortMessage, "My short error message")
            Assertions.assertEquals(errorResult!!.longMessages, arrayListOf("My long error message"))
            Assertions.assertEquals(errorResult!!.httpCode, 200)
        }
    }
}
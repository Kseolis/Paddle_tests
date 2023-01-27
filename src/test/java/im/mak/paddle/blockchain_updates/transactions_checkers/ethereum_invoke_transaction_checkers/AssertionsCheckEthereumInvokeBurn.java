package im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers;

import com.wavesplatform.transactions.invocation.Function;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;

import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Constants.BINARY_BASE58;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AssertionsCheckEthereumInvokeBurn {
    private final PrepareInvokeTestsData testData;
    private final EthereumInvokeTransactionSender txSender;
    private final String senderAddressString;
    private final String assetIdStr;
    private final int txIndex;
    private final String assetDAppAddressString;
    private final Function dAppCallFunction;

    public AssertionsCheckEthereumInvokeBurn(PrepareInvokeTestsData testData, EthereumInvokeTransactionSender txSender, int txIndex) {
        this.testData = testData;
        this.txSender = txSender;
        this.txIndex = txIndex;
        this.dAppCallFunction = testData.getDAppCall().getFunction();
        this.assetIdStr = testData.getAssetId().toString();
        this.senderAddressString = txSender.getEthTx().sender().address().toString();
        this.assetDAppAddressString = testData.getAssetDAppAccount().address().toString();
    }

    public final void assertionsCheckEthereumInvokeBurn(InvokeCalculationsBalancesAfterTx calcBalances) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, assetDAppAddressString, dAppCallFunction),
                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, getIssueAssetData()),
                () -> checkEthereumInvokeBurnMetadata(txIndex, 0, testData.getAssetAmount()),
                () -> checkStateUpdateBalance(txIndex, 0,
                        senderAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 1,
                        senderAddressString,
                        assetIdStr,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(), 0),
                () -> checkStateUpdateBalance(txIndex, 2,
                        assetDAppAddressString,
                        null,
                        0,
                        testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(txIndex, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(txIndex, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}

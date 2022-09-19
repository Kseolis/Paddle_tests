package im.mak.paddle.blockchain_updates;

import static com.wavesplatform.transactions.CreateAliasTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.AliasTransactionHandler.getAliasFromAliasTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionFeeAmount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcTransactionsCheckers {
    private final int txIndex;
    private final String address;
    private final String publicKey;
    private final String aliasTxId;

    public GrpcTransactionsCheckers(int txIndex, String address, String publicKey, String aliasTxId) {
        this.txIndex = txIndex;
        this.address = address;
        this.publicKey = publicKey;
        this.aliasTxId = aliasTxId;
    }

    public void checkAliasGrpc(String newAlias, long amountBefore, long amountAfter, long fee) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(publicKey),
                () -> assertThat(getAliasFromAliasTransaction(txIndex)).isEqualTo(newAlias),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                () -> assertThat(getTxId(txIndex)).isEqualTo(aliasTxId)
        );
    }
}

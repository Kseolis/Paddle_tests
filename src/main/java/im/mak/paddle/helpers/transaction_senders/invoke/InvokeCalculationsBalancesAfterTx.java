package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import java.util.List;

import static im.mak.paddle.Node.node;

public class InvokeCalculationsBalancesAfterTx {
    private final PrepareInvokeTestsData testData;
    private long callerBalanceWavesBeforeTransaction;
    private long callerBalanceIssuedAssetsBeforeTransaction;
    private long callerBalanceWavesAfterTransaction;
    private long callerBalanceIssuedAssetsAfterTransaction;

    private long dAppBalanceWavesBeforeTransaction;
    private long dAppBalanceIssuedAssetsBeforeTransaction;
    private long dAppBalanceWavesAfterTransaction;
    private long dAppBalanceIssuedAssetsAfterTransaction;

    private long otherDAppBalanceWavesBeforeTransaction;
    private long otherDAppBalanceWavesAfterTransaction;
    private long otherDAppBalanceIssuedAssetsAfterTransaction;

    private long accBalanceWavesBeforeTransaction;
    private long accBalanceIssuedAssetsBeforeTransaction;
    private long accBalanceWavesAfterTransaction;
    private long accBalanceIssuedAssetsAfterTransaction;

    private String invokeResultData;

    public InvokeCalculationsBalancesAfterTx(PrepareInvokeTestsData testData) {
        this.testData = testData;
    }

    public void balancesAfterPaymentInvoke(Address caller, Address dApp, List<Amount> amounts, AssetId id) {
        prepareBalances(caller, dApp, id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction += a.value();
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterBurnAssetInvoke(Address caller, Address dApp, List<Amount> amounts, AssetId id) {
        prepareBalances(caller, dApp, id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterReissueAssetInvoke(Address caller, Address dApp, List<Amount> amounts, AssetId id) {
        prepareBalances(caller, dApp, id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterCallerScriptTransfer(Address caller, Address dApp, Address acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction -= a.value() * 2;
                            accBalanceWavesAfterTransaction += a.value();
                            callerBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterDAppToDApp(Address caller, Address dApp, Address acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction += a.value();
                            accBalanceWavesAfterTransaction -= a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterDoubleNestedForCaller(Address caller, Address dApp, Address otherDApp, Address acc, List<Amount> amounts, AssetId id) {
        long maxFirstWavesAmountValue = 10000;
        prepareFourAccBalances(caller, dApp, acc, otherDApp, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(a -> {
                if (a.assetId().isWaves() && a.value() <= maxFirstWavesAmountValue) {
                    dAppBalanceWavesAfterTransaction += a.value();
                    accBalanceWavesAfterTransaction -= a.value();
                } else if (a.assetId().isWaves() && a.value() > maxFirstWavesAmountValue) {
                    accBalanceWavesAfterTransaction += a.value();
                    otherDAppBalanceWavesAfterTransaction -= a.value();
                } else if (a.assetId().equals(id)) {
                    dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                    accBalanceIssuedAssetsAfterTransaction += a.value();
                }
            });
        }
    }

    public void balancesAfterDoubleNestedForOriginCaller(Address caller, Address dApp, Address otherDApp, Address acc, List<Amount> amounts, AssetId id) {
        long maxFirstWavesAmountValue = 10000;
        prepareFourAccBalances(caller, dApp, acc, otherDApp, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(a -> {
                if (a.assetId().isWaves() && a.value() <= maxFirstWavesAmountValue) {
                    accBalanceWavesAfterTransaction -= a.value();
                    callerBalanceWavesAfterTransaction += a.value();
                } else if (a.assetId().isWaves() && a.value() > maxFirstWavesAmountValue) {
                    otherDAppBalanceWavesAfterTransaction -= a.value();
                    callerBalanceWavesAfterTransaction += a.value();
                }

                if (a.assetId().equals(id)) {
                    dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                    accBalanceIssuedAssetsAfterTransaction += a.value();
                }
            });
        }
    }

    public void balancesAfterEthereumSponsorFeeInvoke(Address caller, Address dApp, List<Amount> amounts, AssetId id) {
        prepareBalances(caller, dApp, id);
        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            dAppBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterEthereumDAppToDApp(Address caller, Address dApp, Address acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);
        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value() * 2;
                            accBalanceWavesAfterTransaction -= a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesAfterEthereumReissueAssetInvoke(Address caller, Address dApp, List<Amount> amounts, AssetId id, int transferCounts) {
        prepareBalances(caller, dApp, id);
        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            dAppBalanceIssuedAssetsAfterTransaction += a.value() * transferCounts;
                        }
                    }
            );
        }
    }

    public void balancesEthereumAfterCallerScriptTransfer(Address caller, Address dApp, Address acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);
        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction -= a.value();
                            accBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public void balancesEthereumAfterDoubleNestedForCaller(Address caller, Address dApp, Address otherDApp, Address acc, List<Amount> amounts, AssetId id) {
        long maxFirstWavesAmountValue = 10000;
        prepareFourAccBalances(caller, dApp, acc, otherDApp, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(a -> {
                if (a.assetId().isWaves() && a.value() <= maxFirstWavesAmountValue) {
                    callerBalanceWavesAfterTransaction -= a.value();
                    accBalanceWavesAfterTransaction -= a.value();
                    dAppBalanceWavesAfterTransaction += a.value() * 2;
                } else if (a.assetId().isWaves() && a.value() > maxFirstWavesAmountValue) {
                    callerBalanceWavesAfterTransaction -= a.value();
                    otherDAppBalanceWavesAfterTransaction -= a.value();
                    accBalanceWavesAfterTransaction += a.value();
                    dAppBalanceWavesAfterTransaction += a.value();
                } else if (a.assetId().equals(id)) {
                    callerBalanceIssuedAssetsAfterTransaction -= a.value();
                    accBalanceIssuedAssetsAfterTransaction += a.value();
                }
            });
        }
    }

    public void balancesEthereumAfterDoubleNestedForOriginCaller(Address caller, Address dApp, Address otherDApp, Address acc, List<Amount> amounts, AssetId id) {
        long maxFirstWavesAmountValue = 10000;
        prepareFourAccBalances(caller, dApp, acc, otherDApp, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(a -> {
                if (a.assetId().isWaves() && a.value() <= maxFirstWavesAmountValue) {
                    accBalanceWavesAfterTransaction -= a.value();
                    dAppBalanceWavesAfterTransaction += a.value();
                } else if (a.assetId().isWaves() && a.value() > maxFirstWavesAmountValue) {
                    otherDAppBalanceWavesAfterTransaction -= a.value();
                    dAppBalanceWavesAfterTransaction += a.value();
                }
                if (a.assetId().equals(id)) {
                    callerBalanceIssuedAssetsAfterTransaction -= a.value();
                    accBalanceIssuedAssetsAfterTransaction += a.value();
                }
            });
        }
    }

    public long getCallerBalanceWavesBeforeTransaction() {
        return callerBalanceWavesBeforeTransaction;
    }

    public long getDAppBalanceWavesBeforeTransaction() {
        return dAppBalanceWavesBeforeTransaction;
    }

    public long getDAppBalanceIssuedAssetsBeforeTransaction() {
        return dAppBalanceIssuedAssetsBeforeTransaction;
    }

    public long getAccBalanceWavesBeforeTransaction() {
        return accBalanceWavesBeforeTransaction;
    }

    public long getAccBalanceIssuedAssetsBeforeTransaction() {
        return accBalanceIssuedAssetsBeforeTransaction;
    }

    public long getCallerBalanceWavesAfterTransaction() {
        return callerBalanceWavesAfterTransaction;
    }

    public long getCallerBalanceIssuedAssetsAfterTransaction() {
        return callerBalanceIssuedAssetsAfterTransaction;
    }

    public long getDAppBalanceWavesAfterTransaction() {
        return dAppBalanceWavesAfterTransaction;
    }

    public long getDAppBalanceIssuedAssetsAfterTransaction() {
        return dAppBalanceIssuedAssetsAfterTransaction;
    }

    public long getAccBalanceWavesAfterTransaction() {
        return accBalanceWavesAfterTransaction;
    }

    public long getAccBalanceIssuedAssetsAfterTransaction() {
        return accBalanceIssuedAssetsAfterTransaction;
    }

    public long getOtherDAppBalanceWavesBeforeTransaction() {
        return otherDAppBalanceWavesBeforeTransaction;
    }

    public long getOtherDAppBalanceWavesAfterTransaction() {
        return otherDAppBalanceWavesAfterTransaction;
    }

    public long getOtherDAppBalanceIssuedAssetsAfterTransaction() {
        return otherDAppBalanceIssuedAssetsAfterTransaction;
    }

    public long getCallerBalanceIssuedAssetsBeforeTransaction() {
        return callerBalanceIssuedAssetsBeforeTransaction;
    }

    public String getInvokeResultData() {
        return invokeResultData;
    }

    private void prepareBalances(Address caller, Address dApp, AssetId id) {
        callerBalanceWavesBeforeTransaction = node().getBalance(caller);
        dAppBalanceWavesBeforeTransaction = node().getBalance(dApp);
        callerBalanceIssuedAssetsBeforeTransaction = node().getAssetBalance(caller, id);
        dAppBalanceIssuedAssetsBeforeTransaction = node().getAssetBalance(dApp, id);

        callerBalanceWavesAfterTransaction = callerBalanceWavesBeforeTransaction - testData.getInvokeFee();
        callerBalanceIssuedAssetsAfterTransaction = callerBalanceIssuedAssetsBeforeTransaction;
        dAppBalanceWavesAfterTransaction = dAppBalanceWavesBeforeTransaction;
        dAppBalanceIssuedAssetsAfterTransaction = dAppBalanceIssuedAssetsBeforeTransaction;
    }

    private void prepareThreeAccBalances(Address caller, Address dApp, Address acc, AssetId id) {
        prepareBalances(caller, dApp, id);

        accBalanceWavesBeforeTransaction = node().getBalance(acc);
        accBalanceIssuedAssetsBeforeTransaction = node().getAssetBalance(acc, id);
        accBalanceWavesAfterTransaction = accBalanceWavesBeforeTransaction;
        accBalanceIssuedAssetsAfterTransaction = accBalanceIssuedAssetsBeforeTransaction;
    }

    private void prepareFourAccBalances(Address caller, Address dApp, Address acc, Address dApp2, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);
        otherDAppBalanceWavesBeforeTransaction = node().getBalance(dApp2);
        otherDAppBalanceWavesAfterTransaction = node().getBalance(dApp2);
        otherDAppBalanceIssuedAssetsAfterTransaction = node().getAssetBalance(dApp2, id);
    }
}

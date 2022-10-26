package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import java.util.List;

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
    private long otherDAppBalanceIssuedAssetsBeforeTransaction;
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

    public void balancesAfterPaymentInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterBurnAssetInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterReissueAssetInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterCallerInvokeAsset(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
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


    public void balancesAfterCallerScriptTransfer
            (Account caller, Account dApp, Account acc, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterDAppToDApp(Account caller, Account dApp, Account acc, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterDoubleNestedForCaller
            (Account caller, Account dApp, Account otherDApp, Account acc, List<Amount> amounts, AssetId id) {
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

    public void balancesAfterDoubleNestedForOriginCaller
            (Account caller, Account dApp, Account otherDApp, Account acc, List<Amount> amounts, AssetId id) {
        long maxFirstWavesAmountValue = 10000;
        prepareFourAccBalances(caller, dApp, acc, otherDApp, id);
        invokeResultData = String.valueOf(testData.getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(a -> {
                if (a.assetId().isWaves() && a.value() <= maxFirstWavesAmountValue) {
                    dAppBalanceWavesAfterTransaction += a.value();
                    accBalanceWavesAfterTransaction -= a.value();
                } else if (a.assetId().isWaves() && a.value() > maxFirstWavesAmountValue) {
                    otherDAppBalanceWavesAfterTransaction -= a.value();
                } else if (a.assetId().equals(id)) {
                    dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                    accBalanceIssuedAssetsAfterTransaction += a.value();
                }
            });
        }
    }

    public long getCallerBalanceWavesBeforeTransaction() {
        return callerBalanceWavesBeforeTransaction;
    }

    public long getCallerBalanceIssuedAssetsBeforeTransaction() {
        return callerBalanceIssuedAssetsBeforeTransaction;
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

    public long getOtherDAppBalanceIssuedAssetsBeforeTransaction() {
        return otherDAppBalanceIssuedAssetsBeforeTransaction;
    }

    public long getOtherDAppBalanceWavesAfterTransaction() {
        return otherDAppBalanceWavesAfterTransaction;
    }

    public long getOtherDAppBalanceIssuedAssetsAfterTransaction() {
        return otherDAppBalanceIssuedAssetsAfterTransaction;
    }

    public String getInvokeResultData() {
        return invokeResultData;
    }

    private void prepareBalances(Account caller, Account dApp, AssetId id) {
        callerBalanceWavesBeforeTransaction = caller.getWavesBalance();
        dAppBalanceWavesBeforeTransaction = dApp.getWavesBalance();
        callerBalanceIssuedAssetsBeforeTransaction = caller.getBalance(id);
        dAppBalanceIssuedAssetsBeforeTransaction = dApp.getBalance(id);

        callerBalanceWavesAfterTransaction = callerBalanceWavesBeforeTransaction - testData.getInvokeFee();
        callerBalanceIssuedAssetsAfterTransaction = callerBalanceIssuedAssetsBeforeTransaction;
        dAppBalanceWavesAfterTransaction = dAppBalanceWavesBeforeTransaction;
        dAppBalanceIssuedAssetsAfterTransaction = dAppBalanceIssuedAssetsBeforeTransaction;
    }

    private void prepareThreeAccBalances(Account caller, Account dApp, Account acc, AssetId id) {
        prepareBalances(caller, dApp, id);

        accBalanceWavesBeforeTransaction = acc.getWavesBalance();
        accBalanceIssuedAssetsBeforeTransaction = acc.getBalance(id);
        accBalanceWavesAfterTransaction = accBalanceWavesBeforeTransaction;
        accBalanceIssuedAssetsAfterTransaction = accBalanceIssuedAssetsBeforeTransaction;
    }

    private void prepareFourAccBalances(Account caller, Account dApp, Account acc, Account dApp2, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);

        otherDAppBalanceWavesAfterTransaction = dApp2.getWavesBalance();
        otherDAppBalanceIssuedAssetsAfterTransaction = dApp2.getBalance(id);
    }
}

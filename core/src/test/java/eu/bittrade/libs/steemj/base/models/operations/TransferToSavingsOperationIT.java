package eu.bittrade.libs.steemj.base.models.operations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.bittrade.libs.steemj.IntegrationTest;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Asset;
import eu.bittrade.libs.steemj.base.models.BaseTransactionalIntegrationTest;
import eu.bittrade.libs.steemj.enums.AssetSymbolType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;

/**
 * Verify the functionality of the "transfer to savings operation" under the use
 * of real api calls.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class TransferToSavingsOperationIT extends BaseTransactionalIntegrationTest {
    private static final String EXPECTED_TRANSACTION_HEX = "f68585abf4dce7c80457012007646"
            + "57a3133333706737465656d6a54dd00000000000003535445454d00000000011b64310c9be"
            + "405a03053f120109e01557a51b33858f84d96068d208b22c1c0e0352073e075f6dbb5c057a"
            + "be8639fc8a7ed8fe539237e8d22e6e921226c4fd4d1ab";

    /**
     * <b>Attention:</b> This test class requires a valid active key of the used
     * "from" - account. If no active key is provided or the active key is not
     * valid an Exception will be thrown. The active key is passed as a -D
     * parameter during test execution.
     * 
     * @throws Exception
     *             If something went wrong.
     */
    @BeforeClass()
    public static void prepareTestClass() throws Exception {
        setupIntegrationTestEnvironmentForTransactionalTests();

        AccountName from = new AccountName("dez1337");
        AccountName to = new AccountName("steemj");
        String memo = "";

        Asset amount = new Asset();
        amount.setAmount(56660L);
        amount.setSymbol(AssetSymbolType.STEEM);

        TransferToSavingsOperation transferFromSavingsOperation = new TransferToSavingsOperation(from, to, amount,
                memo);

        ArrayList<Operation> operations = new ArrayList<>();
        operations.add(transferFromSavingsOperation);

        signedTransaction.setOperations(operations);

        sign();
    }

    @Category({ IntegrationTest.class })
    @Test
    public void testOperationParsing() throws SteemCommunicationException {
        // TODO: Implement.
    }

    @Category({ IntegrationTest.class })
    @Test
    public void verifyTransaction() throws Exception {
        assertThat(steemJ.verifyAuthority(signedTransaction), equalTo(true));
    }

    @Category({ IntegrationTest.class })
    @Test
    public void getTransactionHex() throws Exception {
        assertThat(steemJ.getTransactionHex(signedTransaction), equalTo(EXPECTED_TRANSACTION_HEX));
    }
}

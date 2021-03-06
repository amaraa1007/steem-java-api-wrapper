package eu.bittrade.libs.steemj.base.models.operations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.BaseTransactionalUnitTest;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;

/**
 * Test the transformation of a Steem "prove authority operation".
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class ProveAuthorityOperationTest extends BaseTransactionalUnitTest {
    final String EXPECTED_BYTE_REPRESENTATION_WITH_OWNER = "170764657a3133333701";
    final String EXPECTED_BYTE_REPRESENTATION_WITH_ACTIVE = "170764657a3133333700";
    final String EXPECTED_TRANSACTION_HASH = "8eab8cc82a1c18605b048db55eaaef5ccf7e7e9a257b2ecc07e83b0a494b61b3";
    final String EXPECTED_TRANSACTION_SERIALIZATION = "0000000000000000000000000000000000000000000000000000000"
            + "000000000f68585abf4dce9c8045702170764657a3133333701170764657a313333370000";

    private static ProveAuthorityOperation proveAuthorityOperationWithOwnerKey;
    private static ProveAuthorityOperation proveAuthorityOperationWithActiveKey;

    /**
     * Prepare the environment for this specific test.
     * 
     * @throws Exception
     *             If something went wrong.
     */
    @BeforeClass()
    public static void prepareTestClass() throws Exception {
        setupUnitTestEnvironmentForTransactionalTests();

        AccountName challengedAccount = new AccountName("dez1337");

        proveAuthorityOperationWithOwnerKey = new ProveAuthorityOperation(challengedAccount, true);
        proveAuthorityOperationWithActiveKey = new ProveAuthorityOperation(challengedAccount);

        ArrayList<Operation> operations = new ArrayList<>();
        operations.add(proveAuthorityOperationWithOwnerKey);
        operations.add(proveAuthorityOperationWithActiveKey);

        signedTransaction.setOperations(operations);
    }

    @Override
    @Test
    public void testOperationToByteArray() throws UnsupportedEncodingException, SteemInvalidTransactionException {
        assertThat("Expect that the operation has the given byte representation.",
                Utils.HEX.encode(proveAuthorityOperationWithOwnerKey.toByteArray()),
                equalTo(EXPECTED_BYTE_REPRESENTATION_WITH_OWNER));
        assertThat("Expect that the operation has the given byte representation.",
                Utils.HEX.encode(proveAuthorityOperationWithActiveKey.toByteArray()),
                equalTo(EXPECTED_BYTE_REPRESENTATION_WITH_ACTIVE));
    }

    @Override
    @Test
    public void testTransactionWithOperationToHex()
            throws UnsupportedEncodingException, SteemInvalidTransactionException {
        // Sign the transaction.
        sign();

        assertThat("The serialized transaction should look like expected.",
                Utils.HEX.encode(signedTransaction.toByteArray()), equalTo(EXPECTED_TRANSACTION_SERIALIZATION));
        assertThat("Expect that the serialized transaction results in the given hex.",
                Utils.HEX.encode(Sha256Hash.wrap(Sha256Hash.hash(signedTransaction.toByteArray())).getBytes()),
                equalTo(EXPECTED_TRANSACTION_HASH));
    }
}

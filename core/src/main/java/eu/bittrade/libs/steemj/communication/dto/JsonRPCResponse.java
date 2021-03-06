package eu.bittrade.libs.steemj.communication.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

/**
 * This class contains a Json RPC response and offers additional utility
 * methods.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class JsonRPCResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRPCResponse.class);
    /** The field name of the JSON RPC "error" field. */
    public static final String ERROR_FIELD_NAME = "error";
    /** The field name of the JSON RPC "id" field. */
    public static final String ID_FIELD_NAME = "id";
    /** The field name of the JSON RPC "result" field. */
    public static final String RESULT_FIELD_NAME = "result";

    /** The raw JSON String returned by a node. */
    private JsonNode rawJsonResponse;

    /**
     * Create a new {@link JsonRPCResponse} instance.
     * 
     * @param rawJsonResponse
     *            The raw JSON response that should be wrapped by this
     *            {@link JsonRPCResponse} instance.
     */
    public JsonRPCResponse(JsonNode rawJsonResponse) {
        this.rawJsonResponse = rawJsonResponse;
    }

    /**
     * Get the raw JSON response that is wrapped by this {@link JsonRPCResponse}
     * instance.
     * 
     * @return The raw JSON response that is wrapped by this
     *         {@link JsonRPCResponse} instance.
     */
    public JsonNode getRawJsonResponse() {
        return rawJsonResponse;
    }

    // #########################################################################
    // ## HANDLE RESULTS #######################################################
    // #########################################################################

    /**
     * Check if the given <code>response</code> has the expected <code> id.
     * 
     * &#64;param id The request id to compare with.
     * &#64;param response The JSON returned from the node.
     * @return <code>true</code> if the <code>response</code> contains the
     *         <code>id</code> or <code>false</code> if not.
     */
    private boolean hasExpectedId(long id, ObjectNode response) {
        return response.has(ID_FIELD_NAME) && response.get(ID_FIELD_NAME) != null
                && response.get(ID_FIELD_NAME).asLong() == id;
    }

    /**
     * Check if the JSON response wrapped by this {@link JsonRPCResponse}
     * instance contains a result field.
     * 
     * @return <code>true</code> if this is the case or <code>false</code> if
     *         not.
     */
    private boolean isResult() {
        return rawJsonResponse.has(RESULT_FIELD_NAME);
    }

    /**
     * Check if the JSON response wrapped by this {@link JsonRPCResponse}
     * instance is empty.
     * 
     * @return <code>true</code> if this is the case or <code>false</code> if
     *         not.
     */
    private boolean isResultEmpty() {
        if (rawJsonResponse.get(RESULT_FIELD_NAME) == null || rawJsonResponse.get(RESULT_FIELD_NAME).isNull())
            return true;

        LOGGER.debug("The response is empty.");
        return false;
    }

    /**
     * Check if the JSON response wrapped by this {@link JsonRPCResponse}
     * instance has the expected JSON structure.
     * 
     * @return <code>true</code> if this is the case or <code>false</code> if
     *         not.
     */
    private boolean isResponseValid() {
        if (rawJsonResponse.isObject())
            return true;

        LOGGER.error("The response is not an object.");
        return false;
    }

    /**
     * This method checks if the JSON response wrapped by this
     * {@link JsonRPCResponse} instance has the expected <code>id</code> and
     * will try to transform the JSON into the given <code>type</code>.
     * 
     * @param type
     *            The type to transform the JSON to.
     * @param id
     *            The expected id of the response.
     * @return A list of of <code>type</code> instances.
     * @throws SteemResponseException
     *             If the response does not contain the expected <code>id</code>
     *             or if the response could not be transformed into the expected
     *             <code>type</code>.
     */
    public <T> List<T> handleResult(JavaType type, long id) throws SteemResponseException {
        if (isResponseValid()) {
            if (!isResult()) {
                throw new SteemResponseException(
                        "The result does not contain the required " + RESULT_FIELD_NAME + " field.");
            } else {
                ObjectNode responseAsObject = ObjectNode.class.cast(rawJsonResponse);

                if (!hasExpectedId(id, responseAsObject)) {
                    throw new SteemResponseException(
                            "The id of this response does not match the expected id. This can cause an unexpected behavior.");
                }

                if (!isResultEmpty())
                    return CommunicationHandler.getObjectMapper().convertValue(responseAsObject.get(RESULT_FIELD_NAME),
                            type);
            }
        }

        return new ArrayList<>();
    }

    // #########################################################################
    // ## HANDLE CALLBACKS #####################################################
    // #########################################################################

    /**
     * Check if the {@link #getRawJsonResponse()} contains the expected fields
     * of a callback response.
     * 
     * @return <code>true</code> if the response contains the expected fields,
     *         <code>false</code> otherwise.
     */
    public boolean isCallback() {
        // TODO: Implement
        return false;
    }

    // #########################################################################
    // ## HANDLE ERRORS ########################################################
    // #########################################################################

    /**
     * Check if the {@link #getRawJsonResponse()} contains an error.
     * 
     * @return <code>true</code> if the response contains an error,
     *         <code>false</code> otherwise.
     */
    public boolean isError() {
        return rawJsonResponse.has(ERROR_FIELD_NAME) && rawJsonResponse.get(ERROR_FIELD_NAME) != null
                && !rawJsonResponse.get(ERROR_FIELD_NAME).isNull();
    }

    /**
     * Create a new {@link Throwable} based on the Json response.
     * 
     * @return A {@link Throwable} based on the Json response.
     */
    public Throwable createThrowable() {
        // TODO: Implement
        return new Throwable();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

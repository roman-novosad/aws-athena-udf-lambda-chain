/*-
 * #%L
 * ProtectorsFederationUDFHandler
 * %%
 * Copyright (C) 2019 - 2020 Amazon Web Services
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package com.amazonaws.athena.udf.textanalytics;

import com.amazonaws.athena.connector.lambda.data.BlockAllocator;
import com.amazonaws.athena.connector.lambda.handlers.IdentityAwareUserDefinedFunctionHandler;
import com.amazonaws.athena.connector.lambda.security.FederatedIdentity;
import com.amazonaws.athena.connector.lambda.udf.UserDefinedFunctionRequest;
import com.amazonaws.athena.connector.lambda.udf.UserDefinedFunctionResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ProtectorsFederationUDFHandler extends IdentityAwareUserDefinedFunctionHandler {

    private static final String SOURCE_TYPE = "athena_protectors_caller";
    private LambdaInvoker lambdaInvokerClient;

    private Context context;
    private LambdaLogger log;
    private FederatedIdentity identity;

    public ProtectorsFederationUDFHandler(final String sourceType, final Context context) {
        super(sourceType);
        this.context = context;
    }

    public ProtectorsFederationUDFHandler() {
        super(SOURCE_TYPE);
    }

    private LambdaInvoker lambdaInvokerClient() {
        if (this.lambdaInvokerClient == null) {
            this.lambdaInvokerClient = new LambdaInvoker();
        }
        return this.lambdaInvokerClient;
    }


    @Override
    protected UserDefinedFunctionResponse processScalarFunction(BlockAllocator allocator, UserDefinedFunctionRequest req)
            throws Exception {

        this.identity = req.getIdentity();
        return super.processScalarFunction(allocator, req);
    }

    public String decrypt(String message) throws Exception {
        return this.lambdaInvokerClient()
                .decrypt(new ProtectorsDecryptMessageRequest(this.identity, message));
    }

}
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

import com.amazonaws.athena.connector.lambda.handlers.UserDefinedFunctionHandler;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.logging.Logger;

public class ProtectorsFederationUDFHandler extends UserDefinedFunctionHandler {
    private static final String SOURCE_TYPE = "athena_protectors_caller";
    public static int maxTextBytes = 5000;  //utf8 bytes
    public static int maxBatchSize = 25;

    private LambdaInvoker lambdaInvokerClient;

    private Context context;
    private Logger log;

    public ProtectorsFederationUDFHandler(final String sourceType, final Context context) {
        super(sourceType);
        this.context = context;
    }

    public ProtectorsFederationUDFHandler() {
        super(SOURCE_TYPE);
    }

    private LambdaInvoker lambdaInvokerClient() {
        if (this.lambdaInvokerClient == null) {
            System.out.println("Creating Lambda Invoker Client");
            this.lambdaInvokerClient = new LambdaInvoker();
        }
        return this.lambdaInvokerClient;
    }

    public String decrypt(String message) throws Exception {
        this.context.getLogger().log("user context " + this.context);
        return this.lambdaInvokerClient().decrypt(new ProtectorsDecryptMessageRequest(message));
    }

}
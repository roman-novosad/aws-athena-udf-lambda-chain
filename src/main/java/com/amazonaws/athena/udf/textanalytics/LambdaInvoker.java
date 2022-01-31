/*-
 * #%L
 * textanalyticsudfs
 * %%
 * Copyright (C) 2019 - 2022 Amazon Web Services
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

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class LambdaInvoker {

    static final Logger logger = Logger.getLogger(LambdaInvoker.class.getName());
    static final String LambdaFunctionName = "aws-athena-protectors-handler";

    private class AsyncLambdaHandler implements AsyncHandler<InvokeRequest, InvokeResult> {
        public void onSuccess(InvokeRequest req, InvokeResult res) {
            logger.fine("\nLambda function returned:");
            ByteBuffer response_payload = res.getPayload();
            logger.fine(new String(response_payload.array()));
        }

        public void onError(Exception e) {
            logger.fine(e.getMessage());
        }
    }

    public String decrypt(ProtectorsDecryptMessageRequest protectorsMessageToDecrypt) {
        Gson gson = new Gson();

        try {
            //issue: aws region is not set to debug-time. solution for eclipse:
            //environment variable is set by lambda container or eclipse ide environment variables
            //use instead for eclipse debugging: project -> Run as -> Run Configurations -> Environment -> Add variable: "AWS_REGION": "eu-central-1"
            AWSLambdaAsync lambda = AWSLambdaAsyncClientBuilder.defaultClient(); //Default async client using the DefaultAWSCredentialsProviderChain and DefaultAwsRegionProviderChain chain
            InvokeRequest req = new InvokeRequest()
                    .withFunctionName(LambdaFunctionName)
                    .withPayload(gson.toJson(protectorsMessageToDecrypt));

            Future<InvokeResult> future_res = lambda.invokeAsync(req, new AsyncLambdaHandler());

            logger.fine("Waiting for async callback");
            while (!future_res.isDone() && !future_res.isCancelled()) {
                // perform some other tasks...
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.fine("Thread.sleep() was interrupted!");
                }
                System.out.print(".");
            }
            String result = new String(future_res.get().getPayload().array(), "utf-8");
            System.out.print("For request " + protectorsMessageToDecrypt.getMessage() + " is response " + result);
            return result;
        } catch (Exception e) {
            logger.finest("Execute async lambda function: " + LambdaFunctionName + " failed: " + e.getMessage());
        }
        return null;
    }
}


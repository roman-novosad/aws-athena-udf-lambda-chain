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

import com.amazonaws.athena.connector.lambda.security.FederatedIdentity;

import java.util.UUID;

public class ProtectorsDecryptMessageRequest {

    private final String message;
    private final UUID uuid;
    private final FederatedIdentity identity;

    public ProtectorsDecryptMessageRequest(final FederatedIdentity identity, final String message) {
        this(UUID.randomUUID(), identity, message);
    }

    public ProtectorsDecryptMessageRequest(final UUID uuid, FederatedIdentity identity, final String message) {
        this.message = message;
        this.identity = identity;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public FederatedIdentity getIdentity() {
        return identity;
    }
}

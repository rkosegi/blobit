/*
 Licensed to Diennea S.r.l. under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. Diennea S.r.l. licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

 */
package org.blobit.core.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This is the entry point of Blobit
 *
 * @author enrico.olivelli
 */
public interface ObjectManager extends AutoCloseable {

    /**
     * Writes an object. This function is async, you have to check the result of the Future in order to get the ID of
     * the stored Object
     *
     * @param bucketId this is the ID of the bucket. The system will allocate resources keeping buckets data isolated
     * @param data
     * @return the value returned from the future will be an opaque 'printable' id useful for retrival
     */
    public PutPromise put(String bucketId, byte[] data);

    /**
     * Writes an object. This function is async, you have to check the result of the Future in order to get the ID of
     * the stored Object
     *
     * @param bucketId bucketId this is the ID of the bucket. The system will allocate resources keeping buckets data
     * isolated
     * @param data array of bytes
     * @param offset offset from which to start writing bytes
     * @param len number of bytes to write
     * @return the value returned from the future will be an opaque 'printable' id useful for retrival
     */
    public PutPromise put(String bucketId, byte[] data, int offset, int len);

    /**
     * Retrieves the contents of an object. This function is async, you have to check the result of the Future in order
     * to get the effective value. If a null value is returned as byte[] it means that the object does not exits
     *
     * @param bucketId the value of bucketId
     * @param objectId
     * @return the java.util.concurrent.Future<byte[]>
     */
    public CompletableFuture<byte[]> get(String bucketId, String objectId);

    /**
     * Marks an object for deletion. Space will not be released immediately.
     *
     * @param bucketId
     * @param objectId
     * @return
     * @see #gc()
     * @see #gc(java.lang.String)
     */
    public CompletableFuture<Void> delete(String bucketId, String objectId);

    /**
     * Creates a new bucket to store data
     *
     * @param bucketId
     * @param tablespaceName
     * @param configuration
     *
     * @throws ObjectManagerException
     */
    public CompletableFuture<BucketMetadata> createBucket(String bucketId, String tablespaceName, BucketConfiguration configuration);

    /**
     * Marks an existing bucket for deletion. Space will not be released immediately.
     *
     * @param bucketId
     * @throws ObjectManagerException
     */
    public CompletableFuture<?> deleteBucket(String bucketId);

    /**
     * List every existing bucket.
     *
     * @param consumer
     * @return
     * @throws ObjectManagerException
     */
    public void listBuckets(Consumer<BucketMetadata> consumer) throws ObjectManagerException;

    /**
     * Access metadata of a single bucket
     *
     * @return
     * @throws ObjectManagerException
     */
    public BucketMetadata getBucketMetadata(String bucketId) throws ObjectManagerException;

    /**
     * Release resources allocated by a bucket but no more in use
     *
     * @param bucketId
     */
    public void gc(String bucketId);

    /**
     * Loops over every bucket and performs {@link #gc(java.lang.String) }
     *
     * @see #gc(java.lang.String)
     */
    public void gc();

    /**
     * Tries to cleaup and save resources due to deletion of buckets
     *
     * @throws ObjectManagerException
     */
    public void cleanup() throws ObjectManagerException;

    public void start() throws ObjectManagerException;

    @Override
    public void close();

}

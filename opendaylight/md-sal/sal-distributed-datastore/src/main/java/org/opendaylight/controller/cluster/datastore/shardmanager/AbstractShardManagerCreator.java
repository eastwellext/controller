/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore.shardmanager;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import akka.actor.Props;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.cluster.datastore.AbstractDataStore;
import org.opendaylight.controller.cluster.datastore.ClusterWrapper;
import org.opendaylight.controller.cluster.datastore.DatastoreContextFactory;
import org.opendaylight.controller.cluster.datastore.config.Configuration;
import org.opendaylight.controller.cluster.datastore.persisted.DatastoreSnapshot;
import org.opendaylight.controller.cluster.datastore.utils.PrimaryShardInfoFutureCache;

public abstract class AbstractShardManagerCreator<T extends AbstractShardManagerCreator<T>> {
    private SettableFuture<Void> readinessFuture;
    private ClusterWrapper cluster;
    private Configuration configuration;
    private DatastoreContextFactory datastoreContextFactory;
    private AbstractDataStore distributedDataStore;
    private PrimaryShardInfoFutureCache primaryShardInfoCache;
    private DatastoreSnapshot restoreFromSnapshot;
    private volatile boolean sealed;

    AbstractShardManagerCreator() {
        // Prevent outside instantiation
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    protected final void checkSealed() {
        checkState(!sealed, "Builder is already sealed - further modifications are not allowed");
    }

    ClusterWrapper getCluster() {
        return cluster;
    }

    public T cluster(final ClusterWrapper newCluster) {
        checkSealed();
        this.cluster = newCluster;
        return self();
    }

    Configuration getConfiguration() {
        return configuration;
    }

    public T configuration(final Configuration newConfiguration) {
        checkSealed();
        this.configuration = newConfiguration;
        return self();
    }

    DatastoreContextFactory getDatastoreContextFactory() {
        return datastoreContextFactory;
    }

    public T datastoreContextFactory(final DatastoreContextFactory newDatastoreContextFactory) {
        checkSealed();
        this.datastoreContextFactory = requireNonNull(newDatastoreContextFactory);
        return self();
    }

    AbstractDataStore getDistributedDataStore() {
        return distributedDataStore;
    }

    public T distributedDataStore(final AbstractDataStore newDistributedDataStore) {
        checkSealed();
        this.distributedDataStore = newDistributedDataStore;
        return self();
    }

    SettableFuture<Void> getReadinessFuture() {
        return readinessFuture;
    }

    public T readinessFuture(final SettableFuture<Void> newReadinessFuture) {
        checkSealed();
        this.readinessFuture = newReadinessFuture;
        return self();
    }

    PrimaryShardInfoFutureCache getPrimaryShardInfoCache() {
        return primaryShardInfoCache;
    }

    public T primaryShardInfoCache(final PrimaryShardInfoFutureCache newPrimaryShardInfoCache) {
        checkSealed();
        this.primaryShardInfoCache = newPrimaryShardInfoCache;
        return self();
    }

    DatastoreSnapshot getRestoreFromSnapshot() {
        return restoreFromSnapshot;
    }

    public T restoreFromSnapshot(final DatastoreSnapshot newRestoreFromSnapshot) {
        checkSealed();
        this.restoreFromSnapshot = newRestoreFromSnapshot;
        return self();
    }

    protected void verify() {
        sealed = true;
        requireNonNull(cluster, "cluster should not be null");
        requireNonNull(configuration, "configuration should not be null");
        requireNonNull(datastoreContextFactory, "datastoreContextFactory should not be null");
        requireNonNull(distributedDataStore, "distributedDataStore should not be null");
        requireNonNull(readinessFuture, "readinessFuture should not be null");
        requireNonNull(primaryShardInfoCache, "primaryShardInfoCache should not be null");
    }

    public Props props() {
        verify();
        return Props.create(ShardManager.class, this);
    }
}

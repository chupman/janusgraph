// Copyright 2017 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.janusgraph.graphdb;

import org.janusgraph.core.JanusGraphException;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.configuration.JanusGraphConstants;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_BACKEND;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.UNIQUE_INSTANCE_ID_HOSTNAME;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.INITIAL_JANUSGRAPH_VERSION;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.INITIAL_STORAGE_VERSION;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.ALLOW_UPGRADE;

import org.apache.commons.configuration.MapConfiguration;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class GraphDatabaseConfigurationUpgradeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void graphShouldNotUpgrade() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(UNIQUE_INSTANCE_ID_HOSTNAME.toStringWithoutRoot(), true);
        map.put(INITIAL_STORAGE_VERSION.toStringWithoutRoot(), "1");
        map.put(INITIAL_JANUSGRAPH_VERSION.toStringWithoutRoot(), "0.2.0");
        final MapConfiguration config = new MapConfiguration(map);
        final boolean freeze = true;
        // change call below
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfiguration(new CommonsConfiguration(config), freeze));
        assertEquals(graph.openManagement().get("graph.storage-version"), "1");
        assertEquals(graph.openManagement().get("graph.janusgraph-version"), "0.2.1");
        graph.close();
    }

    @Test
    public void graphShouldUpgradeDownlevelStorageVersion() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(UNIQUE_INSTANCE_ID_HOSTNAME.toStringWithoutRoot(), true);
        map.put(ALLOW_UPGRADE.toStringWithoutRoot(), true);
        map.put(INITIAL_STORAGE_VERSION.toStringWithoutRoot(), "1");
        map.put(INITIAL_JANUSGRAPH_VERSION.toStringWithoutRoot(), "0.2.1");
        final MapConfiguration config = new MapConfiguration(map);
        final boolean freeze = true;
        // change call below
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfiguration(new CommonsConfiguration(config), freeze));
        assertEquals(graph.openManagement().get("graph.storage-version"), JanusGraphConstants.STORAGE_VERSION);
        assertEquals(graph.openManagement().get("graph.janusgraph-version"), JanusGraphConstants.VERSION);
        assertEquals(graph.openManagement().get("graph.titan-version"), JanusGraphConstants.VERSION);
        graph.close();
    }

    @Test
    public void graphShouldUpgradeUnsetStorageVersion() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(UNIQUE_INSTANCE_ID_HOSTNAME.toStringWithoutRoot(), true);
        map.put(ALLOW_UPGRADE.toStringWithoutRoot(), true);
        map.put(INITIAL_JANUSGRAPH_VERSION.toStringWithoutRoot(), "0.2.0");
        final MapConfiguration config = new MapConfiguration(map);
        final boolean freeze = true;
        // change call below
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfiguration(new CommonsConfiguration(config), freeze));
        assertEquals(graph.openManagement().get("graph.storage-version"), JanusGraphConstants.STORAGE_VERSION);
        assertEquals(graph.openManagement().get("graph.janusgraph-version"), JanusGraphConstants.VERSION);
        assertEquals(graph.openManagement().get("graph.titan-version"), JanusGraphConstants.VERSION);
        graph.close();
    }

}

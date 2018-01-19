package ee.es.api

import ee.common.ext.toInetAddress
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient


fun transportClient(clusterName: String, nodes: Collection<TransportAddress>, settings: Map<String, String>): Client {
    return transportClient(clusterName, nodes) {
        settings.entries.forEach {
            put(it.key, it.value)
        }
    }
}

fun transportClient(clusterName: String, nodes: Collection<TransportAddress>, init: Settings.Builder.() -> Unit): Client {
    val settings = Settings.builder()
            .put("cluster.name", clusterName)
            .put("client.transport.sniff", false)
            .apply(init)
            .build()
    val client = PreBuiltTransportClient(settings)
    nodes.forEach {
        client.addTransportAddress(it)
    }
    return client
}

fun transportClient(clusterName: String, hosts: Collection<String>, port: Int, settings: Map<String, String>): Client {
    return transportClient(clusterName, hosts.map { TransportAddress(it.toInetAddress(), port) }, settings)
}

fun transportClient(clusterName: String, hosts: Collection<String>, port: Int, init: Settings.Builder.() -> Unit): Client {
    return transportClient(clusterName, hosts.map { TransportAddress(it.toInetAddress(), port) }, init)
}
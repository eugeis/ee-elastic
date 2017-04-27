package ee.es

import org.elasticsearch.client.Client
import org.elasticsearch.common.logging.ESLoggerFactory
import org.elasticsearch.common.transport.InetSocketTransportAddress
import uy.klutter.elasticsearch.esTransportClient
import java.net.InetAddress
import java.nio.file.Paths
import java.util.*

class ExportController(val config: ExportConfig) {
    fun export() {
        val client = client()
        val exporter = Exporter(client)
        exporter.export(config.indexes, config.searchSource, Paths.get(config.file), config.fields, config.separator)
        client.close()
    }

    protected fun client(): Client {
        val addresses = ArrayList<InetSocketTransportAddress>()
        config.hosts.forEach { addresses.add(InetSocketTransportAddress(inetAddress(it), config.port)) }
        val client = esTransportClient(config.clusterName, addresses, config.settings)
        return client
    }

    private fun inetAddress(host: String) = InetAddress.getByName(host)
}

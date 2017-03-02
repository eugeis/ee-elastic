package ee.es

import ee.es.api.transportClient
import org.elasticsearch.client.Client
import java.nio.file.Paths

open class ExportController(val config: ExportConfig) {
    fun export() {
        val client = client()
        val exporter = Exporter(client)
        exporter.export(config.indexes, config.searchSource, Paths.get(config.file), config.fields, config.separator)
        client.close()
    }

    protected fun client(): Client = transportClient(config.clusterName, config.hosts, config.port, config.settings)
}

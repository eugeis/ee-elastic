package ee.es

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class ExportControllerTest {

    @Disabled
    @Test
    fun testWriteExportConfig() {
        val config = exportConfig()
        val objectMapper: ObjectMapper = jacksonObjectMapper()
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(exportConfigFile(), config)
    }

    @Disabled
    @Test
    fun testReadExportConfig() {
        val config = exportConfig()
        val objectMapper: ObjectMapper = jacksonObjectMapper()
        val loadedConfig = objectMapper.readValue(exportConfigFile(), ExportConfig::class.java)
        assertEquals(config, loadedConfig)
    }

    //@Disabled
    @Test
    fun testExport() {
        val config = exportConfig()

        val controller = ExportController(config)
        controller.export()
    }

    private fun exportConfig(): ExportConfig {
        val config = ExportConfig("D:/TC_CACHE/logs/export/tid1.log")
        config.indexes = arrayOf("logstash-2017.05.18")
        config.fields = arrayOf("logdate", "type", "level", "logger", "dur", "kind", "message")

        val thread: String = "0:ffff0a7f642d:912b6af:591c0cff:207dff"
        /*config.searchSource = SearchSourceBuilder.searchSource().query(match_phrase {
            "thread" to { query = thread }
        }).sort("@logdate", SortOrder.ASC).sort("sequence", SortOrder.ASC).toString()

        println(config.searchSource)
        */
        return config
    }

    private fun exportConfigFile() = File("D:/TC_CACHE/logs/export/exportConfig.json")
}

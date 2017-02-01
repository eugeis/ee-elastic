package ee.es

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import java.io.File

class ExportControllerTest {

    @Ignore
    @Test fun testWriteExportConfig() {
        val config = exportConfig()
        val objectMapper: ObjectMapper = jacksonObjectMapper()
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(exportConfigFile(), config)
    }

    @Ignore
    @Test fun testReadExportConfig() {
        val config = exportConfig()
        val objectMapper: ObjectMapper = jacksonObjectMapper()
        val loadedConfig = objectMapper.readValue(exportConfigFile(), ExportConfig::class.java)
        assertEquals(config, loadedConfig)
    }

    @Ignore
    @Test fun testExport() {
        val config = exportConfig()

        val controller = ExportController(config)
        controller.export()
    }

    private fun exportConfig(): ExportConfig {
        val config = ExportConfig("D:/views/share/logs/tid1.log")
        config.indexes = arrayOf("logstash-2016.09.02")
        config.fields = arrayOf("logdate", "sequence", "type", "level", "logger", "dur", "kind", "message")

        val thread: String = "tid=0:ffffc164a801:-69feb870:57c82412:4e586${'$'}u=anonymous"
        config.searchSource = """{
                  "query" : {
                    "match" : {
                      "thread" : {
                        "query" : "$thread",
                        "type" : "phrase"
                      }
                    }
                  },
                  "sort" : [ {
                    "@logdate" : {
                      "order" : "asc"
                    }
                  }, {
                    "sequence" : {
                      "order" : "asc"
                    }
                  } ]
                }"""
        return config
    }

    private fun exportConfigFile() = File("D:/views/share/logs/exportConfig.json")
}

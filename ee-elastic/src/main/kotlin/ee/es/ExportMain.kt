package ee.es

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

fun main(args: Array<String>) {
    val config = loadConfig(args)
    val controller = ExportController(config)
    controller.export()
}

fun loadConfig(args: Array<String>): ExportConfig {
    if (args.size != 1) {
        help()
        throw IllegalArgumentException("Please provide export config json file")
    }

    val file = File(args[0])
    if (file.exists()) {
        try {
            val objectMapper: ObjectMapper = jacksonObjectMapper()
            val config = objectMapper.readValue(file, ExportConfig::class.java)
            return config
        } catch (e: Exception) {
            println("Loading export config file not possible because of: $e")
            help()
            throw e
        }
    } else {
        help()
        throw IllegalArgumentException("The export config json file does not exists: $file")
    }

}

fun help() {
    println("Usage: \n ExportMainKt exportConfig.json")
}


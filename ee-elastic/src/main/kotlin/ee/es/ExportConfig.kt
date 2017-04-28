package ee.es

data class ExportConfig(var file: String = "") {
    var clusterName: String = ""
    var hosts: List<String> = listOf("localhost")
    var port: Int = 9300
    var settings: Map<String, String> = emptyMap()

    var fields: Array<String> = arrayOf("_all")
    var separator: String = " "

    var indexes: Array<String> = emptyArray()
    var searchSource: String = ""
}
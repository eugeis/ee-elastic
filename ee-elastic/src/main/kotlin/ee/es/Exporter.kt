package ee.es

import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import java.nio.file.Path

class Exporter(val client: Client) {
    fun export(index: Array<String>, searchSource: String, targetPath: Path, fields: Array<String>, separator: String = " ") {
        val scroll = TimeValue(60000)
        var scrollResp = client.prepareSearch(*index)
                .setSource(searchSource)
                .setScroll(scroll)
                .execute().actionGet()

        targetPath.toFile().bufferedWriter().use { out ->
            println("Export started to $targetPath, please wait...")
            while (scrollResp.hits.hits.size > 0) {
                scrollResp.hits.forEach { hit ->
                    val s = hit.source
                    fields.forEach { field ->
                        if (s.containsKey(field)) {
                            out.write(s[field].toString().removeSuffix("\n").removeSuffix("\r"))
                        } else {
                            out.write(" ")
                        }
                        out.write(separator)
                    }
                    out.write("\n")
                }
                scrollResp = client.prepareSearchScroll(scrollResp.scrollId).setScroll(scroll).execute().actionGet()
            }
            println("Export done to $targetPath.")
        }
        client.close()
    }
}
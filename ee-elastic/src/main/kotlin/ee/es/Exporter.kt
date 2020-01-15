package ee.es

import org.elasticsearch.client.Client
import org.elasticsearch.cluster.ClusterModule
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.NamedXContentRegistry
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.search.builder.SearchSourceBuilder
import java.nio.file.Path

open class Exporter(val client: Client) {
    fun export(index: Array<String>, searchSource: String, targetPath: Path, fields: Array<String>,
        separator: String = " ") {

        val scroll = TimeValue(60000)
        var scrollResp =
            client.prepareSearch(*index).setSource(searchSourceBuilder(searchSource)).setScroll(scroll).execute()
                .actionGet()

        targetPath.toFile().bufferedWriter().use { out ->
            println("Export started to $targetPath, please wait...")
            while (scrollResp.hits.hits.isNotEmpty()) {
                scrollResp.hits.forEach { hit ->
                    val s = hit.sourceAsMap
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

    protected fun searchSourceBuilder(searchSource: String): SearchSourceBuilder {

        /*
// from Map to XContent
        XContentBuilder builder = ... // see above
// from XContent to JSON
        String json = new String(builder.getBytes(), "UTF-8");
// use JSON to populate SearchSourceBuilder
        JsonXContent parser = createParser(JsonXContent.jsonXContent, json));
        sourceBuilder.parseXContent(new QueryParseContext(parser));
        */

        //val parser = XContentFactory.xContent(XContentType.JSON).createParser(namedXContentRegistry(), searchSource)
        val searchSourceBuilder = SearchSourceBuilder.fromXContent(null) //QueryParseContext(parser)
        return searchSourceBuilder
    }

    private fun namedXContentRegistry(): NamedXContentRegistry {
        //return NamedXContentRegistry(NetworkModule.getNamedXContents())
        return NamedXContentRegistry(ClusterModule.getNamedXWriteables())
    }
}
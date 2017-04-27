package ee.es

import org.elasticsearch.client.Client
import org.elasticsearch.common.ParseFieldMatcher
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.NamedXContentRegistry
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryParseContext
import org.elasticsearch.search.aggregations.AggregatorParsers
import org.elasticsearch.search.builder.SearchSourceBuilder
import java.nio.file.Path

open class Exporter(val client: Client) {
    fun export(index: Array<String>, searchSource: String, targetPath: Path, fields: Array<String>, separator: String = " ") {

        val scroll = TimeValue(60000)
        var scrollResp = client.prepareSearch(*index)
                .setSource(searchSourceBuilder(searchSource))
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

    protected fun searchSourceBuilder(searchSource: String): SearchSourceBuilder? {

        /*
// from Map to XContent
        XContentBuilder builder = ... // see above
// from XContent to JSON
        String json = new String(builder.getBytes(), "UTF-8");
// use JSON to populate SearchSourceBuilder
        JsonXContent parser = createParser(JsonXContent.jsonXContent, json));
        sourceBuilder.parseXContent(new QueryParseContext(parser));
        */

        val parser = XContentFactory.xContent(XContentType.JSON).
                createParser(NamedXContentRegistry.EMPTY, searchSource)
        val searchSourceBuilder = SearchSourceBuilder.fromXContent(
                QueryParseContext(parser, ParseFieldMatcher.EMPTY), null, null, null)
        return searchSourceBuilder
        return null
    }
}
package szeweq.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import szeweq.craftery.scan.ScanInfo
import szeweq.craftery.util.entryPairStream
import java.util.stream.Stream

class ListAllTags: ModLookup<Pair<String, Set<String>>>("Tags") {
    override val explain = "Table displays all found tags"

    @Composable
    override fun ColumnScope.decorate(item: Pair<String, Set<String>>) {
        Text(item.first, fontWeight = FontWeight.Bold)
        Text("Values: " + item.second)
    }

    override fun gatherItems(si: ScanInfo): Stream<Pair<String, Set<String>>> {
        return si.tags.entryPairStream()
    }
}

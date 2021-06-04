package szewek.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import szewek.craftery.scan.ScanInfo

class ListAllTags: ModLookup<Pair<String, Set<String>>>("Tags") {
    override val explain = "Table displays all found tags"
    override val itemHeight = 48.dp

    @Composable
    override fun ColumnScope.decorate(item: Pair<String, Set<String>>) {
        Text(item.first, fontWeight = FontWeight.Bold)
        Text("Values: " + item.second)
    }

    override fun gatherItems(si: ScanInfo): List<Pair<String, Set<String>>> {
        return si.tags.entries.map { (k, v) -> k to v }
    }
}
package szewek.craftery.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import szewek.craftery.mcdata.Model
import szewek.craftery.mcdata.Models

@Composable
fun ItemSlot(name: String = "minecraft:item/golden_shovel", count: Int = 0, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val model = remember(name) {
        val x = mutableStateOf<Model>(Model.Empty)
        scope.launch { Models.getModelOf(name).let { x.value = it } }
        x
    }

    Box(modifier.size(48.dp)) {
        Canvas(Modifier.size(48.dp).padding(4.dp).border(2.dp, MaterialTheme.colors.primary)) {
            model.value.draw(this)
        }
        if (count > 1) {
            Text("$count", Modifier.padding(4.dp).align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun CraftingGrid() = Column {
    for (i in 0 until 3) Row {
        for (j in 0 until 3) ItemSlot("minecraft:item/chest", 3)
    }
}
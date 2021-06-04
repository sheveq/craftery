package szewek.craftery.mcdata

import szewek.craftery.net.Downloader
import szewek.craftery.util.LongBiConsumer
import szewek.craftery.util.downloadJson
import szewek.craftery.util.eachEntry
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.ZipInputStream

object MinecraftData {
    var manifest = Manifest(mapOf(), listOf())
    var updated = 0L
    val packages = mutableMapOf<String, Package>()
    val assets = mutableMapOf<String, AssetMap>()
    val filesFromJar = mutableMapOf<String, ByteArray>()

    fun updateManifest(progress: LongBiConsumer) {
        val d = System.currentTimeMillis()
        if (d - updated >= 1000 * 3600) {
            println("Updating Minecraft manifest...")
            val o = downloadJson<Manifest>("https://launchermeta.mojang.com/mc/game/version_manifest.json", progress)
            if (o != null) {
                manifest = o
                updated = d
            }
        }
    }

    fun getPackage(v: String, progress: LongBiConsumer): Package? {
        updateManifest(progress)
        println("PKG for $v")
        val p = packages[v]
        if (p == null) {
            val vu = manifest.versions.find { v == it.id }
            if (vu != null) {
                val o = downloadJson<Package>(vu.url, progress)
                if (o != null) {
                    packages[v] = o
                    return o
                }
            }
        } else {
            return p
        }
        return null
    }

    fun getAssetMap(v: String, progress: LongBiConsumer): AssetMap? {
        updateManifest(progress)
        println("AM for $v")
        val am = getPackage(v, progress)
        if (am != null) {
            println("DL ${am.downloads.map { (k, v) -> k to v.url }}")
            val vi = am.assetIndex.id
            val vu = am.assetIndex.url
            val ma = assets[vi]
            if (ma == null) {
                val dl = downloadJson<AssetMap>(vu, progress)
                if (dl != null) {
                    assets[vi] = dl
                    return dl
                }
            } else {
                return ma
            }
        }
        return null
    }

    fun getAsset(p: String, progress: LongBiConsumer) {
        updateManifest(progress)
        val v = manifest.latest["release"] ?: return
        val ma = getAssetMap(v, progress) ?: return
        val mf = ma.objects[p]
    }

    private fun getMinecraftClientJar(v: String?, progress: LongBiConsumer): ZipInputStream? {
        updateManifest(progress)
        progress.accept(0, 1)
        val z = v ?: manifest.latest["release"]
        if (z == null) {
            println("No Minecraft version selected!")
            return null
        }
        val p = getPackage(z, progress)
        progress.accept(0, 1)
        if (p == null) {
            println("No package found for $z!")
            return null
        }
        val u = p.downloads["client"]?.url
        if (u == null) {
            println("No URL specified for client version $z!")
            return null
        }
        println("Downloading Minecraft client $z jar...")
        val input = Downloader.downloadFile(u, progress)
        return ZipInputStream(input)
    }

    fun loadAllFilesFromJar(v: String?) {
        println("Getting Minecraft client...")
        val z = getMinecraftClientJar(v) { _, _ -> }
        val out = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
        println("Unpacking Minecraft client...")
        z?.eachEntry {
            if (!it.isDirectory && (it.name.startsWith("data/") || it.name.startsWith("assets/"))) {
                //println("Unzipping file ${it.name}...")
                out.reset()
                z.copyTo(out)
                filesFromJar[it.name] = out.toByteArray()
            }
        }
    }

    class Manifest(val latest: Map<String, String>, val versions: List<Version>)

    class Version(
            val id: String,
            val type: String,
            val url: String,
            val time: Date,
            val releaseTime: Date
    )

    class Package(
        val id: String,
        val assetIndex: AssetIndex,
        val downloads: Map<String, DownloadURL>
    )

    data class AssetIndex(val id: String, val url: String)

    class DownloadURL(val sha1: String, val size: Int, val url: String)

    class AssetMap(val objects: Map<String, FileCheck>) {
        val files = mutableMapOf<String, ByteArray>()
    }

    class FileCheck(val hash: String, val size: String)
}
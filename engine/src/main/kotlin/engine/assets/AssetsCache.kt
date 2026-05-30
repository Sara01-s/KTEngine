package engine.assets

class AssetCache<T : AutoCloseable> {
    private val assets = mutableMapOf<String, T>()

    fun get(path: String, loader: () -> T): T {
        return assets.getOrPut(path, loader)
    }

    fun dispose() {
        assets.values.forEach(AutoCloseable::close)
        assets.clear()
    }

    fun contains(path: String): Boolean {
        return assets.containsKey(path)
    }

    operator fun get(path: String): T? {
        return assets[path]
    }
}
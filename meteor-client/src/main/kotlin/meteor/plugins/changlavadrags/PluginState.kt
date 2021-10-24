package meteor.plugins.changlavadrags

interface PluginState {
    fun onGameTick()
    fun transition()
    fun sanityCheck()
}
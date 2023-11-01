package klibx.exception

class NotSupportedException(feature: String = ""):
    Exception("Feature $feature is not currently supported on this platform.")
package klibx.io.exception

class FileOpeningException:
    Exception("File could not be opened!")

class FileModeNotChangeableException:
    Exception("File mode is not changeable!")

class InvalidModeException:
    Exception("Invalid file mode!")

class FileClosedException:
    Exception("File is already closed!")
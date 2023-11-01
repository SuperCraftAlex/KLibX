package klibx.io.exception

import klibx.exception.ClosedException

class FileOpeningException:
    Exception("File could not be opened!")

class FileModeNotChangeableException:
    Exception("File mode is not changeable!")

class InvalidModeException:
    Exception("Invalid file mode!")

class FileClosedException:
    ClosedException("File is already closed!")

class EndOfFileException:
    Exception("End of file reached!")

class FileDeleteException:
    Exception("Could not delete file!")
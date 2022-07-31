package Cmd;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface Kernel32 extends Library {
    Kernel32 INSTANCE= Native.loadLibrary("Kernel32",Kernel32.class);
    int STD_INPUT_HANDLE=-10;
    int STD_OUTPUT_HANDLE = -11;
    int STD_ERROR_HANDLE=-12;
    boolean WriteConsoleW(
            Pointer hConsoleOutput,
            char[] lpBuffer,
            int nNumberOfCharsToWrite,
            IntByReference lpNumberOfCharsWritten,
            Pointer lpReserved
    );
    boolean ReadConsoleW(
            Pointer hConsoleInput,
            char[] lpBuffer,
            int nNumberOfCharsToRead,
            IntByReference lpNumberOfCharsRead,
            Pointer pInputControl
    );
    Pointer GetStdHandle(int nStdHandle);
    boolean GetConsoleMode(
            Pointer hConsoleInput,
            IntByReference lpMode
    );
}

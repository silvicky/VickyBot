package Cmd;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class Kernel32{
    static
    {
        Native.register("Kernel32");
    }
    public static final int STD_INPUT_HANDLE=-10;

    public static native boolean ReadConsoleW(
            Pointer hConsoleInput,
            char[] lpBuffer,
            int nNumberOfCharsToRead,
            IntByReference lpNumberOfCharsRead,
            Pointer pInputControl
    );
    public static native Pointer GetStdHandle(int nStdHandle);
    public static native boolean GetConsoleMode(
            Pointer hConsoleInput,
            IntByReference lpMode
    );
}

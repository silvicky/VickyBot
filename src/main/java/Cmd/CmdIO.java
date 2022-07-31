package Cmd;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import static com.sun.jna.Native.getLastError;

public class CmdIO {
    public static String readFromConsole() {
        Pointer hConsoleInput= Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        StringBuilder line = new StringBuilder();
        char[] lpBuffer = new char[128];
        IntByReference lpNumberOfCharsRead = new IntByReference();
        while (true) {
            if (!Kernel32.ReadConsoleW(hConsoleInput,
                    lpBuffer, lpBuffer.length, lpNumberOfCharsRead,
                    null)) {
                String errMsg = String.valueOf(getLastError());
                throw new IllegalStateException(errMsg);
            }
            int len = lpNumberOfCharsRead.getValue();
            line.append(lpBuffer, 0, len);
            if (lpBuffer[len - 1] == '\n') {
                break;
            }
        }
        return line.toString();
    }
    public static boolean isCMD()
    {
        Pointer hConsoleInput= Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        IntByReference lpMode = new IntByReference();
        return Kernel32.GetConsoleMode(hConsoleInput, lpMode);
    }
}

package pt.haslab.horus.syscall;

import com.sun.jna.Native;
import pt.haslab.horus.syscall.structures.TimeSpec;

public class Clock
{
    private static int CLOCK_REALTIME = 0;
    private static int CLOCK_MONOTONIC = 1;
    private static int CLOCK_MONOTONIC_RAW = 4;

    public static long kernelTime()
                    throws SyscallException
    {
        TimeSpec tp = new TimeSpec();

        int returnValue = LibC.INSTANCE.clock_gettime( Clock.CLOCK_MONOTONIC, tp );

        if ( returnValue < 0 )
        {
            throw new SyscallException( "Couldn't get Kernel Time [ERRNO:" + Native.getLastError() + "]" );
        }

        return tp.tv_sec * 1000000000L + tp.tv_nsec;
    }
}

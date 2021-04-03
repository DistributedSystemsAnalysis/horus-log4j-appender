package pt.haslab.horus.syscall;

import com.sun.jna.Library;
import com.sun.jna.Native;
import pt.haslab.horus.syscall.structures.TimeSpec;

interface LibC
                extends Library
{
    LibC INSTANCE = Native.loadLibrary( "c", LibC.class );

    int getpid();

    int clock_gettime( int clk_id, TimeSpec tp );

    int syscall( int number, Object... args );
}

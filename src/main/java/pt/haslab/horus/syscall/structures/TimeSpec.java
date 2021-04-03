package pt.haslab.horus.syscall.structures;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class TimeSpec
                extends Structure
{
    public int tv_sec;

    public long tv_nsec;

    protected List<String> getFieldOrder()
    {
        return Arrays.asList( "tv_sec", "tv_nsec" );
    }
}

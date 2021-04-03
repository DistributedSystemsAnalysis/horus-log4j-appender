package pt.haslab.horus.syscall;

public class Thread
{
    public static int tid()
    {
        return LibC.INSTANCE.syscall( SysCall64.gettid );
    }

    public static int pid()
    {
        return LibC.INSTANCE.getpid();
    }
}

package pt.haslab.horus.syscall;

public class SyscallException
                extends Exception
{
    public SyscallException()
    {
        super();
    }

    public SyscallException( String message )
    {
        super( message );
    }

    public SyscallException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public SyscallException( Throwable cause )
    {
        super( cause );
    }

    public SyscallException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}

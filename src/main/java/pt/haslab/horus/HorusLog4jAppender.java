package pt.haslab.horus;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.JsonObject;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import pt.haslab.horus.events.EventType;
import pt.haslab.horus.events.flatbuffers.generated.FalconEvent;
import pt.haslab.horus.syscall.Clock;
import pt.haslab.horus.syscall.SyscallException;
import pt.haslab.horus.syscall.Thread;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

public class HorusLog4jAppender
                extends AppenderSkeleton
{
    private String hostname;

    private HorusProducer producer;

    private String bootstrapServers;

    private String topic;

    private String group;

    private long timeout;

    public HorusLog4jAppender()
    {
        super();
        this.hostname = this.determineHostname();
    }

    public HorusLog4jAppender( List<String> bootstrapServers, String topic, String group )
    {
        super();
        this.bootstrapServers = String.join( ",", bootstrapServers );
        this.topic = topic;
        this.group = group;
        this.timeout = 0;

        this.connect();
    }

    private String determineHostname()
    {
        try
        {
            return java.net.InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch ( UnknownHostException e )
        {
            throw new RuntimeException( "Could not determine the hostname." );
        }
    }

    private void connect()
    {
        Properties props = new Properties();
        props.put( "bootstrap.servers", this.bootstrapServers );
        props.put( "group.id", this.group );

        this.producer = new HorusProducer( props, this.topic, this.timeout );
    }

    public String getBootstrapServers()
    {
        return this.bootstrapServers;
    }

    public void setBootstrapServers( String bootstrapServers )
    {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopic()
    {
        return this.topic;
    }

    public void setTopic( String topic )
    {
        this.topic = topic;
    }

    public String getGroup()
    {
        return this.group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public long getTimeout()
    {
        return this.timeout;
    }

    public void setTimeout( long timeout )
    {
        this.timeout = timeout;
    }

    protected void append( LoggingEvent event )
    {
        try
        {
            int pid = Thread.pid();
            int tid = Thread.tid();
            long userTime = event.getTimeStamp();
            long kernelTime = Clock.kernelTime();
            String comm = "java";
            JsonObject object = new JsonObject();
            object.addProperty( "message", event.getRenderedMessage() );

            // TODO: Replace the following with FlatBufferUtil.toLog() in the horus-events package to convert LogEvent to serializable FalconEvent.
            FlatBufferBuilder builder = new FlatBufferBuilder( 0 );
            int idFieldOffset = builder.createString(
                            String.join( "-", this.hostname, EventType.LOG.toString(), String.valueOf( kernelTime ) ) );
            int commFieldOffset = builder.createString( comm );
            int hostFieldOffset = builder.createString( this.hostname );
            int extraDataFieldOffset = builder.createString( object.toString() );

            FalconEvent.startFalconEvent( builder );
            FalconEvent.addId( builder, idFieldOffset );
            FalconEvent.addUserTime( builder, userTime );
            FalconEvent.addKernelTime( builder, kernelTime );
            FalconEvent.addType( builder, EventType.LOG.getCode() );
            FalconEvent.addPid( builder, pid );
            FalconEvent.addTid( builder, tid );
            FalconEvent.addComm( builder, commFieldOffset );
            FalconEvent.addHost( builder, hostFieldOffset );
            FalconEvent.addExtraData( builder, extraDataFieldOffset );
            builder.finish( FalconEvent.endFalconEvent( builder ) );

            this.producer.send( builder.sizedByteArray() );
        }
        catch ( SyscallException e )
        {
            LogLog.warn( "Couldn't get needed data. " + e.toString() );
            e.printStackTrace();
        }
    }

    @Override
    public void activateOptions()
    {
        this.connect();
    }

    public void close()
    {
        this.producer.close();
    }

    public boolean requiresLayout()
    {
        return false;
    }
}

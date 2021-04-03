package pt.haslab.horus;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.helpers.LogLog;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HorusProducer
                implements AutoCloseable
{
    private KafkaProducer<byte[], byte[]> producer;

    private String topic;

    private long timeout = 0;

    public HorusProducer( Properties props, String topic )
    {
        // Force serializers to deal with ByteArray.
        props.put( "key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer" );
        props.put( "value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer" );

        this.producer = new KafkaProducer<byte[], byte[]>( props );
        this.topic = topic;
    }

    public HorusProducer( Properties props, String topic, long timeout )
    {
        this( props, topic );
        this.timeout = timeout > 0L ? timeout : 0L;
    }

    public void send( byte[] message )
    {
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>( this.topic, message );
        Future<RecordMetadata> future = this.producer.send( record );
        try
        {
            if ( this.timeout > 0L )
                future.get( this.timeout, TimeUnit.MILLISECONDS );
            else
                future.get();
        }
        catch ( InterruptedException | ExecutionException | TimeoutException e )
        {
            e.printStackTrace();
            LogLog.warn( "Couldn't send message to Kafka servers: " + e.getMessage() );
        }
    }

    @Override
    public void close()
    {
        this.producer.close();
    }
}


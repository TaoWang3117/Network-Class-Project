import java.io.Serializable;
import java.net.InetAddress;

public class Header implements Serializable {
    protected int Destination_Port, Source_Port, Packet_Code;
    protected InetAddress Destination_IP, Source_IP;

    public Header(InetAddress Destination_IP, InetAddress Source_IP, int Destination_Port, int Source_Port, int Packet_Code) {
        this.Destination_IP = Destination_IP;
        this.Source_IP = Source_IP;
        this.Destination_Port = Destination_Port;
        this.Source_Port = Source_Port;
        this.Packet_Code = Packet_Code;
    }
}
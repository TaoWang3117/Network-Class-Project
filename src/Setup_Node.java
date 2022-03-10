import java.io.IOException;
import java.net.InetAddress;

public class Setup_Node<E> {
    private int Node_ID;
    private InetAddress IP_Address;
    private int Port_Number;
    private int Node_Connection_One;
    private int Node_Connection_Two;
    private int MTU;

    public Setup_Node(int ID, String IP, int Port, int Connection1, int Connection2, int mtu) throws IOException {
        this.Node_ID = ID;
        this.IP_Address = InetAddress.getByName(IP);
        this.Port_Number = Port;
        this.Node_Connection_One = Connection1;
        this.Node_Connection_Two = Connection2;
        MTU = mtu;
    }

    public int Get_Connection_One() {
        return Node_Connection_One;
    }

    public int Get_Connection_Two() {
        return Node_Connection_Two;
    }

    public int Get_MTU() {
        return this.MTU;
    }

    public int Get_ID() {
        return Node_ID;
    }

    public InetAddress Get_IP() {
        return IP_Address;
    }

    public int Get_Port() {
        return Port_Number;
    }

    public void to_String() {
        System.out.printf("Node ID: %d\r\nIP Address: %s\r\nPort Number: %d\r\nNode Connection 1: " +
                        "%d\r\nNode Connection 2: %d\r\nMaximum Transmission Unit Size: 2%d\n" + "--------------------------\n",
                Node_ID, IP_Address, Port_Number, Node_Connection_One, Node_Connection_Two, MTU);
    }
}
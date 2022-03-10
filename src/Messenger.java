import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;


public class Messenger{
    public static void main(String[] args) throws Exception {
        ITCScript_Reader ITC = new ITCScript_Reader();
        InetAddress User_IP = InetAddress.getLocalHost();
        Setup_Node User_Node = ITC.nodeCompare(User_IP);
        System.out.println(ITC.toString());
        if(User_Node == null) {
            System.out.println("Node is non existent...");
            System.exit(0);
        }
        DatagramSocket Receive = new DatagramSocket(User_Node.Get_Port(), User_Node.Get_IP());
        DatagramSocket Send = new DatagramSocket();


        Thread Receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] MTU_Limit = new byte[User_Node.Get_MTU()];
                    DatagramPacket Data_Packet = new DatagramPacket(MTU_Limit, MTU_Limit.length);
                    //Received Message Code
                    try {
                        Receive.receive(Data_Packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] Header2_Byte = new byte[User_Node.Get_MTU()];
                    byte[] Message_Byte = new byte[User_Node.Get_MTU()];
                    int Index = 0;
                    int i = 0;
                    while(Index <= 247) {
                        Header2_Byte[Index] = MTU_Limit[Index++];
                    }
                    Index++;
                    while(MTU_Limit[Index] != 0) {
                        Message_Byte[i++] = MTU_Limit[Index++];
                    }
                    ByteArrayInputStream Byte_Input_Stream = new ByteArrayInputStream(Header2_Byte);
                    ObjectInputStream Object_Input_Stream = null;
                    try {
                        Object_Input_Stream = new ObjectInputStream(Byte_Input_Stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Header packetHeader = null;
                    try {
                        packetHeader = (Header)Object_Input_Stream.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (User_Node.Get_IP().equals(packetHeader.Destination_IP) && User_Node.Get_Port() == packetHeader.Destination_Port) {
                        StringBuilder output = new StringBuilder();
                        int o = 0;
                        while (Message_Byte[o] != 0) {
                            output.append((char) Message_Byte[o]);
                            o++;
                        }
                        System.out.println(output);
                    } else{
                        System.out.println("Future Forwarding Method Goes Here...");
                    }
                }
            }
        });


        Thread Sender = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Enter a node ID...");
                    BufferedReader Input = new BufferedReader(new InputStreamReader(System.in));
                    String User_ID_Node;
                    String Out_Message;
                    User_ID_Node = null;
                    Out_Message = null;
                    try {
                        User_ID_Node = Input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Setup_Node send_MSG_Node = null;
                    try {
                        Integer.parseInt(User_ID_Node);
                    } catch(NumberFormatException Error) {
                        if(User_ID_Node.equals("exit")){
                            System.exit(0);
                        } else {
                            System.out.println("That is Not a proper node name...");
                            continue;
                        }
                    }
                    if (ITC.nodeExists(Integer.parseInt(User_ID_Node))) {

                        send_MSG_Node = ITC.grabNode(Integer.parseInt(User_ID_Node));
                    } else {
                        System.out.println("There is Not a Node with that ID in the ITC Script...");
                        continue;
                    }
                    System.out.println("Enter a Message...");
                    try {
                        Out_Message = Input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!Out_Message.equals("exit")){
                        Header header = new Header(send_MSG_Node.Get_IP(), User_Node.Get_IP(),send_MSG_Node.Get_Port(), User_Node.Get_Port(),0);
                        ByteArrayOutputStream Byte_Out_Stream = new ByteArrayOutputStream();
                        ObjectOutputStream Object_Out_Stream;
                        try {
                            Object_Out_Stream = new ObjectOutputStream(Byte_Out_Stream);
                            Object_Out_Stream.writeObject(header);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] Header_Byte = Byte_Out_Stream.toByteArray();
                        byte[] Message_Byte = null;
                        byte[] Packet_Byte = new byte[User_Node.Get_MTU()];
                        Message_Byte = ("Message Received from Node "+ User_ID_Node + ": " + Out_Message).getBytes();
                        int Index = 0;
                        for(byte i:Header_Byte) {
                            Packet_Byte[Index++] = i;
                        }
                        Index++;
                        for(byte i:Message_Byte) {
                            Packet_Byte[Index++] = i;
                        }
                        DatagramPacket Output_Message;
                        if ((int) (Math.random() * 100) < 15) {
                            byte[] corrupted = garble(Packet_Byte);
                            Output_Message = new DatagramPacket(corrupted, corrupted.length, send_MSG_Node.Get_IP(),send_MSG_Node.Get_Port());
                        } else {
                            Output_Message = new DatagramPacket(Packet_Byte, Packet_Byte.length, send_MSG_Node.Get_IP(),send_MSG_Node.Get_Port());
                        }
                        try {
                            Send.send(Output_Message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.exit(0);
                    }
                }
            }
        });
        Receiver.start();
        Sender.start();
        Receiver.join();
        Sender.join();
    }
    public static byte[] garble(byte[] data) {
// Garble/drop the packet
        int C = 0; // packet corruption fraction (between 0 and 100)

        Random rand = new Random(); // random generator
        if (!(rand.nextInt(101) < C)) { // corrupt the packet
            int randomLocationToGarble = 0; // holds the byte location of the packet to be garbled

// Corrupt the packet
            if (data.length > 0) { // Determine the size of the packet
                randomLocationToGarble = rand.nextInt(data.length + 1);
                data[randomLocationToGarble] = (byte)(~((int)data[randomLocationToGarble]));
            }
        }
        return data;
    }
}
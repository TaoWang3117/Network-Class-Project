import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class ITCScript_Reader<E> {
    private static ArrayList<Setup_Node> Node_Address = new ArrayList<>();
    private Map<Integer, List<Integer>> Node_Map = new HashMap<>();

    ITCScript_Reader() throws IOException {
        try {
            InputStream Script = ITCScript_Reader.class.getResourceAsStream("ITCScript.txt");
            Scanner Input = new Scanner(Script);
            while (Input.hasNext()) {
                int Node_ID = Integer.parseInt(Input.next());
                String IP_Address = Input.next();
                int Port_Number = Input.nextInt();
                int Node_Connection_One = Input.nextInt();
                int Node_Connection_Two = Input.nextInt();
                int Maximum_Transmission_Unit = Input.nextInt();

                Setup_Node New_Node = new Setup_Node(Node_ID, IP_Address, Port_Number, Node_Connection_One, Node_Connection_Two, Maximum_Transmission_Unit);
                Node_Address.add(New_Node);
                New_Node.to_String();
            }
        } catch (FileNotFoundException Script_Error) {
            System.out.println("Error Occurred When Connecting to the Server...");
        }

        for (Setup_Node n : Node_Address) {
            addVertex(n.Get_ID());
            addEdge(n.Get_ID(), n.Get_Connection_One(), false);
            addEdge(n.Get_ID(), n.Get_Connection_Two(), false);
        }
    }

    // This function adds a new vertex to the graph
    public void addVertex(Integer s) {
        Node_Map.put(s, new LinkedList<Integer>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(Integer sourceNode,
                        Integer destinationNode,
                        boolean bidirectional) {

        if (!Node_Map.containsKey(sourceNode))
            addVertex(sourceNode);

        if (!Node_Map.containsKey(destinationNode))
            addVertex(destinationNode);

        Node_Map.get(sourceNode).add(destinationNode);
        if (bidirectional == true) {
            Node_Map.get(destinationNode).add(sourceNode);
        }
    }

    public static boolean compareIPs(InetAddress ip1, InetAddress ip2) {
        String[] temp;
        temp = ip2.toString().split("/");
        String[] temp2;
        temp2 = ip1.toString().split("/");
        if(temp2[1].equals(temp[1])) {
            return true;
        }
        return false;
    }

    public Setup_Node nodeCompare(InetAddress User_IP) {
        for(Setup_Node n:Node_Address) {
            if (compareIPs(n.Get_IP(),User_IP)) {
                try (DatagramSocket ignored = new DatagramSocket(n.Get_Port(), User_IP)) {
                    ignored.close();
                    return n;
                } catch (IOException ignored) {
                    continue;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public boolean nodeExists(int S) {
        for (Setup_Node N:Node_Address ) {
            if (S == N.Get_ID()) {
                return true;
            }
        }
        return false;
    }

    public Setup_Node grabNode(int R){
        for(Setup_Node N:Node_Address) {
            if (R == N.Get_ID()) {
                return N;
            }
        }
        return null;
    }

    public String toString() {
        return Node_Map.toString();
    }
}
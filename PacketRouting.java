import java.awt.*;
import java.Applet.*;

public class PacketRouting extends Applet
{
    Graph g = new Graph(this);
    Options options = new Options(this);

    public void init()
    {
        
    }

    public static void main(String [] args)
    {
        PacketRouting p = new PacketRouting();
        p.init();

    }
}

class Options extends Panel
{
    Button b1 = new Button("Reset");
    Button b2 = new Button("Clear");
    Button b3 = new Button("Run");
    Button b4 = new Button("Step");
    Button b5 = new Button("Exit");

    PacketRouting p1;
    boolean run = false; 
    Options(PacketRouting   root)
    {
        p1 = root;
        setLayout(new GridLayout(6,1,0,10));
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
    }

    public boolean action(Event evt, Object arg)
    {
        if(evt.target instanceof Button){

        }
    }
}
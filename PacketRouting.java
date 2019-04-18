import java.awt.*;
import java.Applet.*;
/* <applet code="PacketRouting.class" height=600 width=600></applet>*/
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

   public void lock()
   {
       run =true;
   }
   public void unlock()
   {
       run = false;
   }
}

class Graph extends Canvas implements Runnable
{
    final int MAXNODES = 20;
    final int MAX = MAXNODES+1;
    final int NODESIZE = 20;
    final int NODERADIX = 13;
    final int DIJKSTRA = 1;

    Point node [] = new Point[MAX];
    int weight[][] = new int[MAX][MAX];
    Point arrow[][] = new Point[MAX][MAX];
    Point startp[][] = new Point[MAX][MAX];
    Point endp[][] = new Point[MAX][MAX];
    float dir_x[][] = new float[MAX][MAX];
    float dir_y[][] = new flaot[MAX][MAX];

    boolean aledge[][] = new boolean[MAX][MAX];
    int dist[] = new int [MAX];
    int finaldist[] = new int[MAX];
    Color colornode = new Color[MAX];
    boolean changed[] = new boolean[MAX];

    int numchanged =0;
    int neighbours = 0;
    int step = 0;
    int mindist, minnode, minstart, minend;

    int numnodes = 0;
    int emptyspots = 0;
    int startgraph = 0;

    int hitnode;
    int node1,node2;
    Point thispoint = new Point(0,0);
    Point oldpoint = new Point(0,0);

    boolean newarrow = false;
    boolean movearrow = false;
    boolean deletenode = false;
    boolean movenode = false;
    boolean performalg = false;
    boolean clicked = false;

    Font f1 = new Font("Arial",Font.BOLD,14);
    Font f2 = new Font("Helvetica",Font.BOLD,16);
    FontMetrics fm = getFontMetrics(f1);
    int h = (int)fm.getHeight()/3;
    
    private Image img;
    private Graphics gph;
    private Dimension dim;

    Thread algrthm;

    int algorithm;

    String showstring =  new String("");

    boolean stepthrough = false;
 
    
} 
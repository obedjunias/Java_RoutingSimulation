import java.awt.*;
import java.applet.*;
/* <applet code="PacketRouting.class" height=600 width=600></applet>*/
public class PacketRouting extends Applet
{
    GraphC g = new GraphC(this);
    Options options = new Options(this);
    Txtarea ta = new Txtarea();

    public void init()
    {
      Font myFont = new Font("TimesRoman", Font.BOLD, 18);
      showStatus("Started");
      ta.setFont(myFont);
      options.setBackground(Color.black);
      setLayout(new BorderLayout(2,2));
      add("Center", g);
      add("North", ta);
      add("East", options);
    }
    public void lock() {
        g.lock();
        options.lock();
    }

    public void unlock() {
        g.unlock();
        options.unlock();
    }
    public static void main(String [] args)
    {
        PacketRouting p = new PacketRouting();
        p.init();
        Frame frame = new Frame("Dijkstra");
        frame.add("Center", p);
        frame.resize(800,600);
        frame.show();
        frame.resize(800,600);
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
    boolean locked = false; 
    Options(PacketRouting   root)
    {
        p1 = root;
        setLayout(new GridLayout(5,1,0,0));
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
    }
    
    public boolean action(Event evt, Object arg) {
        if (evt.target instanceof Button) {
            if (((String)arg).equals("Step")) {
                if (!locked) {
                    b3.setLabel("Next step");
                    p1.g.stepalg();                }
                else p1.ta.doctext.showline("Locked");
            }
            if (((String)arg).equals("Next step"))
                p1.g.nextstep();
            if (((String)arg).equals("Reset")) {
                p1.g.reset();
                b3.setLabel("Run");
            }
            if (((String)arg).equals("Clear")) {
                p1.g.clear();
                b3.setLabel("Run");
            }
            if (((String)arg).equals("Run")) {
                if (!locked)
                    p1.g.runalg();
                else p1.ta.doctext.showline("Locked");
            }
            if (((String)arg).equals("Exit")) {
                System.exit(0);
            }
        }
        return true;
    }
   public void lock()
   {
       locked =true;
   }
   public void unlock()
   {
       locked = false;
   }
}
class Txtarea extends Panel { 
    TxtOptions topt = new TxtOptions(this);
    DocText doctext = new DocText();
        Txtarea() {
        setLayout(new BorderLayout(2, 2));
        add("West", topt);
        add("Center", doctext);
    }
}
class TxtOptions extends Panel{
    Choice c = new Choice();
    Txtarea t;

    TxtOptions(Txtarea t1)
    {
        
        t = t1;
        t.setBackground(Color.gray);
        setLayout(new GridLayout(2, 1, 0, 0));
        c.addItem("Draw nodes");
        c.addItem("Remove nodes");
        c.addItem("Move nodes");
        c.addItem("Draw arrows");
        c.addItem("Change weights");
        c.addItem("Remove arrows");
        c.addItem("Clear/Reset");
        c.addItem("Run Algorithm");
        c.addItem("Step through");
        c.addItem("Exit");
        add(c);
    } 
    public boolean action(Event evt, Object arg) {
        if (evt.target instanceof Choice) {
            String str=new String(c.getSelectedItem());
            t.doctext.showline(str);
        }
        return true;
    }
    }
class DocText extends TextArea{
    final String txt = new String("Welcome To Packet Routing Applet");
    DocText() {
        super(5, 2);
        setText(txt);
    }
    public void showline(String str)
    {
        setText(str);
    }

}
class GraphC extends Canvas implements Runnable
{
    final int MAXNODES = 20;
    final int MAX = MAXNODES+1;
    final int NODESIZE = 26;
    final int NODERADIX = 13;
    final int DIJKSTRA = 1;

    Point node [] = new Point[MAX];
    int weight[][] = new int[MAX][MAX];
    Point arrow[][] = new Point[MAX][MAX];
    Point startp[][] = new Point[MAX][MAX];
    Point endp[][] = new Point[MAX][MAX];
    float dir_x[][] = new float[MAX][MAX];
    float dir_y[][] = new float[MAX][MAX];

    boolean algedge[][] = new boolean[MAX][MAX];
    int dist[] = new int[MAX];
    int finaldist[] = new int[MAX];
    Color colornode[] = new Color[MAX];
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
    boolean movestart = false;
    boolean performalg = false;
    boolean clicked = false;

    Font f1 = new Font("Arial",Font.BOLD,18);
    Font f2 = new Font("Helvetica",Font.BOLD,18);
    FontMetrics fm = getFontMetrics(f1);
    int h = (int)fm.getHeight()/3;
    
    private Image img;
    private Graphics gph;
    private Dimension dim;

    Thread algrthm;

    int algorithm;

    String showstring =  new String("");

    boolean stepthrough = false;
    boolean locked = false;

    PacketRouting root;

    GraphC(PacketRouting tempRoot){
        root = tempRoot;
        init();
        algorithm = DIJKSTRA;
        setBackground(Color.black);
    }
    public void lock(){
        locked = true;
    }
    public void unlock(){
        locked= false;
    }
    public void start(){
        if(algrthm!=null)
            algrthm.resume();
    }
    public void init(){
        for (int i=0;i<MAXNODES;i++){
            colornode[i]=Color.orange.brighter();
        for (int j=0;j<MAXNODES;j++)
            algedge[i][j] = false;    
    }
    colornode[startgraph] = Color.red;
    performalg = false;
}
public void clear(){
    startgraph = 0;
    numnodes = 0;
    emptyspots = 0;
    init();
    for (int i=0;i<MAXNODES;i++){
        node[i] = new Point(0,0);
        for (int j=0;j<MAXNODES;j++)
        weight[i][j] = 0;    
    }
    if (algrthm!=null)
        algrthm.stop();
        root.unlock();
        repaint();

}
public void reset(){
    init();
    if (algrthm!=null)
    algrthm.stop();
    root.unlock();
    repaint();
}
public void runalg(){
    root.lock();
    initalg();
    performalg = true;
    algrthm = new Thread(this);
    algrthm.start();
}
public void stepalg(){
    root.lock();
    initalg();
    performalg = true;
    nextstep();
}
public void nextstep(){
    finaldist[minend] = mindist;
    algedge[minstart][minend] = true;
    colornode[minend] = Color.blue;
    step++;
    repaint();

}
public void initalg(){
    init();
    for(int i=0;i<MAXNODES;i++){
        dist[i]=-1;
        finaldist[i] = -1;
        for (int j=0;j<MAXNODES;j++)
            algedge[i][j]=false;
    }
    dist[startgraph] = 0;
    finaldist[startgraph] = 0;
    step =0;
}
public void stop(){
    if(algrthm!=null)
        algrthm.suspend();
}
public void run(){
    for(int i=0;i<(numnodes-emptyspots);i++){
        nextstep();
        try {algrthm.sleep(500);}
        catch (InterruptedException e){}
    }
    algrthm = null;
}
public boolean mouseDown(Event evt, int x, int y) {

    if (locked)
       root.ta.doctext.showline("Locked");
    else {
        clicked = true;
        if (evt.shiftDown()) {
            if (nodehit(x, y, NODESIZE)) {
                oldpoint = node[hitnode];
                node1 = hitnode;
                movenode=true;
            }
        }
        else if (evt.controlDown()) {
            if (nodehit(x, y, NODESIZE)) {
                node1 = hitnode;
                if (startgraph==node1) {
                    movestart=true;
                    thispoint = new Point(x,y);
                    colornode[startgraph]=Color.gray;
                }
                else
                    deletenode= true;
            }
        }
        else if (arrowhit(x, y, 5)) {
            movearrow = true;
            repaint();
        }
        else if (nodehit(x, y, NODESIZE)) {
            if (!newarrow) {
                newarrow = true;
                thispoint = new Point(x, y);
                node1 = hitnode;
            }
        }
        else if ( !nodehit(x, y, 50) && !arrowhit(x, y, 50) )  {
            if (emptyspots==0) {
                if (numnodes < MAXNODES)
                    node[numnodes++]=new Point(x, y);
                else root.ta.doctext.showline("Max Nodes");
            }
            else {
                int i;
                for (i=0;i<numnodes;i++)
                    if (node[i].x==-100)
                    break;
                node[i]=new Point(x, y);
                emptyspots--;
            }
        }
        else
           root.ta.doctext.showline("Mouseclick too close to a point or arrowhead");
        repaint();
    } 
    return true;
}
public boolean mouseDrag(Event evt, int x, int y) {
    if ( (!locked) && clicked ) {
        if (movenode) {
            node[node1]=new Point(x, y);
            for (int i=0;i<numnodes;i++) {
                if (weight[i][node1]>0) {
                    arrowupdate(i, node1, weight[i][node1]);
                }
                if (weight[node1][i]>0) {
                    arrowupdate(node1, i, weight[node1][i]);
                }
            }
            repaint();
        }
        else if (movestart || newarrow) {
            thispoint = new Point(x, y);
            repaint();
        }
        else if (movearrow) {
            changeweight(x, y);
            repaint();
        }
    }
    return true;
}
public boolean mouseUp(Event evt, int x, int y) {
    if ( (!locked) && clicked ) {
        if (movenode) {
            node[node1]=new Point(0, 0);
            if ( nodehit(x, y, 50) || (x<0) || (x>this.size().width) ||
                    (y<0) || (y>this.size().height) ) {
                node[node1]=oldpoint;
               root.ta.doctext.showline("Mouseclick too close to a point or arrowhead");
            }
            else node[node1]=new Point(x, y);
            for (int i=0;i<numnodes;i++) {
                if (weight[i][node1]>0)
                    arrowupdate(i, node1, weight[i][node1]);
                if (weight[node1][i]>0)
                    arrowupdate(node1, i, weight[node1][i]);
            }
            movenode=false;
        }
        else if (deletenode) {
            nodedelete();
            deletenode=false;
        }
        else if (newarrow) {
            newarrow = false;
            if (nodehit(x, y, NODESIZE)) {
                node2=hitnode;
                if (node1!=node2) {
                    arrowupdate(node1, node2, 50);
                    if (weight[node2][node1]>0) {
                        arrowupdate(node2, node1, weight[node2][node1]);
                    }
                   root.ta.doctext.showline("Change distances");
                }
                else System.out.println("4B SelfStudy Project");
            }
        }
        else if (movearrow) {
            movearrow = false;
            if (weight[node1][node2]>0)
                changeweight(x, y);
        }
        else if (movestart) {
            if (nodehit(x, y, NODESIZE))
                startgraph=hitnode;
            colornode[startgraph]=Color.blue;
            movestart=false;
        }
        repaint();
    }
    return true;
}
public boolean nodehit(int x, int y, int dist) {
    for (int i=0; i<numnodes; i++)
        if ( (x-node[i].x)*(x-node[i].x) +
                (y-node[i].y)*(y-node[i].y) < dist*dist ) {
            hitnode = i;
            return true;
        }
    return false;
}

public boolean arrowhit(int x, int y, int dist) {
    for (int i=0; i<numnodes; i++)
        for (int j=0; j<numnodes; j++) {
            if ( ( weight[i][j]>0 ) &&
                    (Math.pow(x-arrow[i][j].x, 2) +
                            Math.pow(y-arrow[i][j].y, 2) < Math.pow(dist, 2) ) ) {
                node1 = i;
                node2 = j;
                return true;
            }
        }
    return false;
}
public void nodedelete() {
    node[node1]=new Point(-100, -100);
    for (int j=0;j<numnodes;j++) {
        weight[node1][j]=0;
        weight[j][node1]=0;
    }
    emptyspots++;
}

public void changeweight(int x, int y) {
    int diff_x = (int)(20*dir_x[node1][node2]);
    int diff_y = (int)(20*dir_y[node1][node2]);

    boolean follow_x=false;
    if (Math.abs(endp[node1][node2].x-startp[node1][node2].x) > Math.abs(endp[node1][node2].y-startp[node1][node2].y))
    {    follow_x = true;   }
    if (follow_x) {
        int hbound = Math.max(startp[node1][node2].x,endp[node1][node2].x)-Math.abs(diff_x);
        int lbound = Math.min(startp[node1][node2].x,endp[node1][node2].x)+Math.abs(diff_x);
        if (x<lbound) { arrow[node1][node2].x=lbound; }
        else if (x>hbound) { arrow[node1][node2].x=hbound; }
        else arrow[node1][node2].x=x;

        arrow[node1][node2].y= (arrow[node1][node2].x-startp[node1][node2].x) *
    (endp[node1][node2].y-startp[node1][node2].y)/(endp[node1][node2].x-startp[node1][node2].x) + startp[node1][node2].y;
        weight[node1][node2] = Math.abs(arrow[node1][node2].x-startp[node1][node2].x-diff_x)*100/(hbound-lbound);
    }
    else {
        int hbound = Math.max(startp[node1][node2].y,
                endp[node1][node2].y)-Math.abs(diff_y);
        int lbound = Math.min(startp[node1][node2].y,endp[node1][node2].y)+Math.abs(diff_y);
        if (y<lbound) { arrow[node1][node2].y=lbound; }
        else if (y>hbound) { arrow[node1][node2].y=hbound; }
        else arrow[node1][node2].y=y;
        arrow[node1][node2].x=
                (arrow[node1][node2].y-startp[node1][node2].y) *(endp[node1][node2].x-startp[node1][node2].x)/
                (endp[node1][node2].y-startp[node1][node2].y) + startp[node1][node2].x;

        weight[node1][node2]=
                Math.abs(arrow[node1][node2].y-startp[node1][node2].y-diff_y)*100/(hbound-lbound);
    }
}

public void arrowupdate(int p1, int p2, int w) {
    int dx, dy;
    float l;
    weight[p1][p2]=w;

    dx = node[p2].x-node[p1].x;
    dy = node[p2].y-node[p1].y;

    l = (float)( Math.sqrt((float)(dx*dx + dy*dy)));
    dir_x[p1][p2]=dx/l;
    dir_y[p1][p2]=dy/l;

    if (weight[p2][p1]>0) {
        startp[p1][p2] = new Point((int)(node[p1].x-5*dir_y[p1][p2]),
                (int)(node[p1].y+5*dir_x[p1][p2]));
        endp[p1][p2] = new Point((int)(node[p2].x-5*dir_y[p1][p2]),
                (int)(node[p2].y+5*dir_x[p1][p2]));
    }
    else {
        startp[p1][p2] = new Point(node[p1].x, node[p1].y);
        endp[p1][p2] = new Point(node[p2].x, node[p2].y);
    }

    int diff_x = (int)(Math.abs(20*dir_x[p1][p2]));
    int diff_y = (int)(Math.abs(20*dir_y[p1][p2]));

    if (startp[p1][p2].x>endp[p1][p2].x) {
        arrow[p1][p2] = new Point(endp[p1][p2].x + diff_x +
                (Math.abs(endp[p1][p2].x-startp[p1][p2].x) - 2*diff_x )*
                        (100-w)/100 , 0);
    }
    else {
        arrow[p1][p2] = new Point(startp[p1][p2].x + diff_x +
                (Math.abs(endp[p1][p2].x-startp[p1][p2].x) - 2*diff_x )*
                        w/100, 0);
    }

    if (startp[p1][p2].y>endp[p1][p2].y) {
        arrow[p1][p2].y=endp[p1][p2].y + diff_y +
                (Math.abs(endp[p1][p2].y-startp[p1][p2].y) - 2*diff_y )*
                        (100-w)/100;
    }
    else {
        arrow[p1][p2].y=startp[p1][p2].y + diff_y +
                (Math.abs(endp[p1][p2].y-startp[p1][p2].y) - 2*diff_y )*
                        w/100;
    }
}
public String intToString(int i) {
    char c=(char)((int)'a'+i);
    return ""+c;
}

public final synchronized void update(Graphics g) {
    Dimension d=size();
    if ((img == null) || (d.width != dim.width) ||
            (d.height != dim.height)) {
        img = createImage(d.width, d.height);
        dim = d;
        gph = img.getGraphics();
    }
    gph.setColor(Color.black);
    gph.fillRect(0, 0, d.width, d.height);
    paint(gph);
    g.drawImage(img, 0, 0, null);
}

public void drawarrow(Graphics g, int i, int j) {
    int x1, x2, x3, y1, y2, y3;

    x1= (int)(arrow[i][j].x - 3*dir_x[i][j] + 6*dir_y[i][j]);
    x2= (int)(arrow[i][j].x - 3*dir_x[i][j] - 6*dir_y[i][j]);
    x3= (int)(arrow[i][j].x + 6*dir_x[i][j]);

    y1= (int)(arrow[i][j].y - 3*dir_y[i][j] - 6*dir_x[i][j]);
    y2= (int)(arrow[i][j].y - 3*dir_y[i][j] + 6*dir_x[i][j]);
    y3= (int)(arrow[i][j].y + 6*dir_y[i][j]);

    int arrowhead_x[] = { x1, x2, x3, x1 };
    int arrowhead_y[] = { y1, y2, y3, y1 };

    if (algedge[i][j]) g.setColor(Color.orange);

    g.drawLine(startp[i][j].x, startp[i][j].y, endp[i][j].x, endp[i][j].y);
    g.fillPolygon(arrowhead_x, arrowhead_y, 4);

    int dx = (int)(Math.abs(7*dir_y[i][j]));
    int dy = (int)(Math.abs(7*dir_x[i][j]));
    String str = new String("" + weight[i][j]);
    g.setColor(Color.green);
    if ((startp[i][j].x>endp[i][j].x) && (startp[i][j].y>=endp[i][j].y))
        g.drawString( str, arrow[i][j].x + dx, arrow[i][j].y - dy);
    if ((startp[i][j].x>=endp[i][j].x) && (startp[i][j].y<endp[i][j].y))
        g.drawString( str, arrow[i][j].x - fm.stringWidth(str) - dx ,
                arrow[i][j].y - dy);
    if ((startp[i][j].x<endp[i][j].x) && (startp[i][j].y<=endp[i][j].y))
        g.drawString( str, arrow[i][j].x - fm.stringWidth(str) ,
                arrow[i][j].y + fm.getHeight());
    if ((startp[i][j].x<=endp[i][j].x) && (startp[i][j].y>endp[i][j].y))
        g.drawString( str, arrow[i][j].x + dx,
                arrow[i][j].y + fm.getHeight() );
}
public void detailsDijkstra(Graphics g, int i, int j) {

    if ( (finaldist[i]!=-1) && (finaldist[j]==-1) ) {
        g.setColor(Color.red);
        if ( (dist[j]==-1) || (dist[j]>=(dist[i]+weight[i][j])) ) {
            if ( (dist[i]+weight[i][j])<dist[j] ) {
                changed[j]=true;
                numchanged++;
            }
            dist[j] = dist[i]+weight[i][j];
            colornode[j]=Color.red;
            if ( (mindist==0) || (dist[j]<mindist) ) {
                mindist=dist[j];
                minstart=i;
                minend=j;
            }
        }
    }
    else g.setColor(Color.gray);
}
public void endstepDijkstra(Graphics g) {

    for (int i=0; i<numnodes; i++)
        if ( (node[i].x>0) && (dist[i]!=-1) ) {
            String str = new String(""+dist[i]);
            g.drawString(str, node[i].x - (int)fm.stringWidth(str)/2 -1,
                    node[i].y + h);

            if (finaldist[i]==-1) {
                neighbours++;
                if (neighbours!=1)
                    showstring = showstring + ", ";
                showstring = showstring + intToString(i) +"=" + dist[i];
            }
        }
    showstring = showstring + ". ";

    if ( (step>1) && (numchanged>0) ) {
        if (numchanged>1)
            showstring = showstring + "Notice that the distances to ";
        else showstring = showstring + "Notice that the distance to ";
        for (int i=0; i<numnodes; i++)
            if ( changed[i] )
                showstring = showstring + intToString(i) +", ";
        if (numchanged>1)
            showstring = showstring + "have changed!\n";
        else showstring = showstring + "has changed!\n";
    }
    else showstring = showstring + " ";
    if (neighbours>1) {

        showstring = showstring + "Node " + intToString(minend) +
                " has the minimum distance.\n";
        int newpaths=0;
        for (int i=0; i<numnodes; i++)
            if ( (node[i].x>0) && (weight[i][minend]>0) && ( finaldist[i] == -1 ) )
                newpaths++;
        if (newpaths>0)
            showstring = showstring + "Any other path to " + intToString(minend) +
                    " visits another red node, and will be longer than " +  mindist + ".\n";
        else showstring = showstring +
                "There are no other arrows coming in to "+
                intToString(minend) + ".\n";
    }
    else {
        boolean morenodes=false;
        for (int i=0; i<numnodes; i++)
            if ( ( node[i].x>0 ) && ( finaldist[i] == -1 ) && ( weight[i][minend]>0 ) )
                morenodes=true;
        boolean bridge=false;
        for (int i=0; i<numnodes; i++)
            if ( ( node[i].x>0 ) && ( finaldist[i] == -1 ) && ( weight[minend][i]>0 ) )
                bridge=true;
        if ( morenodes && bridge )
            showstring = showstring + "Since this node forms a 'bridge' to "+
                    "the remaining nodes,\nall other paths to this node will be longer.\n";
        else if ( morenodes && (!bridge) )
            showstring = showstring + "Remaining gray nodes are not reachable.\n";
        else showstring = showstring + "There are no other arrows coming in to "+
                    intToString(minend) + ".\n";
    }
    showstring = showstring + "Node " + intToString(minend) +
            " will be colored blue to indicate " + mindist +
            " is the length of the shortest path to " + intToString(minend) +".";
   root.ta.doctext.showline(showstring);
}
public void detailsalg(Graphics g, int i, int j) {
    if (algorithm==DIJKSTRA)
        detailsDijkstra(g, i, j);
}
public void endstepalg(Graphics g) {

    if (algorithm==DIJKSTRA)
        endstepDijkstra(g);
    if ( ( performalg ) && (mindist==0) ) {
        if (algrthm != null) algrthm.stop();
        int nreachable = 0;
        for (int i=0; i<numnodes; i++)
            if (finaldist[i] > 0)
                nreachable++;
        if (nreachable == 0)
         root.ta.doctext.showline("none");
        else if (nreachable< (numnodes-emptyspots-1))
         root.ta.doctext.showline("some");
        else
       root.ta.doctext.showline("done");
    }
}
public void paint(Graphics g) {
    mindist=0;
    minnode=MAXNODES;
    minstart=MAXNODES;
    minend=MAXNODES;
    for(int i=0; i<MAXNODES; i++)
        changed[i]=false;
    numchanged=0;
    neighbours=0;
    g.setFont(f1);
    g.setColor(Color.green.darker());
    if (step==1)
        showstring="Algorithm running: red arrows point to nodes reachable from " +
                " the startnode.\nThe distance to: ";
    else
        showstring="Step " + step + ": Red arrows point to nodes reachable from " +
                "nodes that already have a final distance." +
                "\nThe distance to: ";
    if (newarrow)
        g.drawLine(node[node1].x, node[node1].y, thispoint.x, thispoint.y);
    for (int i=0; i<numnodes; i++)
        for (int j=0; j<numnodes; j++)
            if (weight [i][j]>0) {
                if (performalg)
                    detailsalg(g, i, j);
                drawarrow(g, i, j);
            }
    if (movearrow && weight[node1][node2]==0) {
        drawarrow(g, node1, node2);
        g.drawLine(startp[node1][node2].x, startp[node1][node2].y,
                endp[node1][node2].x, endp[node1][node2].y);
    }
    for (int i=0; i<numnodes; i++)
        if (node[i].x>0) {
            g.setColor(colornode[i]);
            g.fillOval(node[i].x-NODERADIX, node[i].y-NODERADIX,
                    NODESIZE, NODESIZE);
        }
g.setColor(Color.blue);
    if (movestart)
        g.fillOval(thispoint.x-NODERADIX, thispoint.y-NODERADIX,NODESIZE, NODESIZE);
    g.setColor(Color.orange);
    if (performalg) endstepalg(g);
    g.setFont(f2);
    for (int i=0; i<numnodes; i++)
        if (node[i].x>0) {
            g.setColor(Color.orange);
            g.drawOval(node[i].x-NODERADIX, node[i].y-NODERADIX,
                    NODESIZE, NODESIZE);
            g.setColor(Color.magenta);
            g.drawString(intToString(i), node[i].x-14, node[i].y-14);
        }
}
}  
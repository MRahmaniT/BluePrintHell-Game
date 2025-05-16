package GameLogic;

import GameShapes.GameShape;
import GameShapes.LineShape;

public class Connection {
    public GameShape blockA;
    public int portA;
    public GameShape blockB;
    public int portB;
    public LineShape line;
    public boolean packetOnLine;

    public Connection(GameShape a, int pa, GameShape b, int pb, LineShape l) {
        this.blockA = a;
        this.portA = pa;
        this.blockB = b;
        this.portB = pb;
        this.line = l;
        this.packetOnLine = false;
    }

    public boolean contains(GameShape block, int port) {
        return (blockA == block && portA == port) || (blockB == block && portB == port);
    }
}

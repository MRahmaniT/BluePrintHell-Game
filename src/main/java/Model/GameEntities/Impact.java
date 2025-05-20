package Model.GameEntities;

import java.awt.*;

public class Impact {
    public Packet packet1;
    public Packet packet2;
    public Point point;
    private boolean disabled;

    public Impact(Packet packet1, Packet packet2, Point point) {
        this.packet1 = packet1;
        this.packet2 = packet2;
        this.point = point;
        this.disabled = false;
    }

    public boolean contains(Packet packet11, Packet packet22) {
        return ((packet1 == packet11) && (packet2 == packet22)) || ((packet2 == packet11) && (packet1 == packet22));
    }
}

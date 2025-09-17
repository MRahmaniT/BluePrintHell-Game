package Client.Model.GameEntities;

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
        return ((packet1.getId() == packet11.getId()) && (packet2.getId() == packet22.getId())) ||
                ((packet2.getId() == packet11.getId()) && (packet1.getId() == packet22.getId()));
    }
}

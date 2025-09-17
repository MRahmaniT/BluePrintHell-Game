package Storage.Facade;

import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Model.GameEntities.Wire.Wire;
import Storage.RealTime.GameEnvironment.BlockSystemStorage;
import Storage.RealTime.GameEnvironment.ConnectionStorage;
import Storage.RealTime.GameEnvironment.PacketStorage;
import Storage.RealTime.GameEnvironment.WireStorage;

import java.util.List;

public final class StorageFacade {
    public enum Mode { LOCAL, REMOTE }
    private static Mode mode = Mode.LOCAL;
    private static RemoteGateway remote = new RemoteGateway.Noop(); // فعلاً Noop

    private StorageFacade() {}

    public static void useLocal() { mode = Mode.LOCAL; }
    public static void useRemote(RemoteGateway gw) { remote = gw; mode = Mode.REMOTE; }
    public static Mode mode() { return mode; }

    // ---- Loads ----
    public static List<BlockSystem> loadBlockSystems() {
        return (mode==Mode.LOCAL) ? BlockSystemStorage.LoadBlockSystems()
                : remote.loadBlockSystems();
    }
    public static List<Connection> loadConnections() {
        return (mode==Mode.LOCAL) ? ConnectionStorage.LoadConnections()
                : remote.loadConnections();
    }
    public static List<Wire> loadWires() {
        return (mode==Mode.LOCAL) ? WireStorage.LoadWires()
                : remote.loadWires();
    }
    public static List<Packet> loadPackets() {
        return (mode==Mode.LOCAL) ? PacketStorage.LoadPackets()
                : remote.loadPackets();
    }

    // ---- Saves ----
    public static void saveBlockSystems(List<BlockSystem> v) {
        if (mode==Mode.LOCAL) BlockSystemStorage.SaveBlockSystems(v);
        else remote.saveBlockSystems(v);
    }
    public static void saveConnections(List<Connection> v) {
        if (mode==Mode.LOCAL) ConnectionStorage.SaveConnections(v);
        else remote.saveConnections(v);
    }
    public static void saveWires(List<Wire> v) {
        if (mode==Mode.LOCAL) WireStorage.SaveWires(v);
        else remote.saveWires(v);
    }
    public static void savePackets(List<Packet> v) {
        if (mode==Mode.LOCAL) PacketStorage.SavePackets(v);
        else remote.savePackets(v);
    }
}

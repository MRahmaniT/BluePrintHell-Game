package Storage;

import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.Connection;
import MVC.Model.GameEntities.Packet;
import MVC.Model.GameEntities.Wire.Wire;
import Storage.RealTime.GameEnvironment.BlockSystemStorage;
import Storage.RealTime.GameEnvironment.ConnectionStorage;
import Storage.RealTime.GameEnvironment.PacketStorage;
import Storage.RealTime.GameEnvironment.WireStorage;

import java.util.List;

public final class LocalSnapshotReader {
    private LocalSnapshotReader(){}

    public static List<BlockSystem> readBlockSystems() { return BlockSystemStorage.LoadBlockSystems(); }
    public static List<Connection>  readConnections()  { return ConnectionStorage.LoadConnections(); }
    public static List<Wire>        readWires()        { return WireStorage.LoadWires(); }
    public static List<Packet>      readPackets()      { return PacketStorage.LoadPackets(); }
}

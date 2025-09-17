package Client.Storage;

import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Model.GameEntities.Wire.Wire;
import Client.Storage.RealTime.GameEnvironment.BlockSystemStorage;
import Client.Storage.RealTime.GameEnvironment.ConnectionStorage;
import Client.Storage.RealTime.GameEnvironment.PacketStorage;
import Client.Storage.RealTime.GameEnvironment.WireStorage;

import java.util.List;

public final class LocalSnapshotReader {
    private LocalSnapshotReader(){}

    public static List<BlockSystem> readBlockSystems() { return BlockSystemStorage.LoadBlockSystems(); }
    public static List<Connection>  readConnections()  { return ConnectionStorage.LoadConnections(); }
    public static List<Wire>        readWires()        { return WireStorage.LoadWires(); }
    public static List<Packet>      readPackets()      { return PacketStorage.LoadPackets(); }
}

package Client.Storage.Facade;

import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Model.GameEntities.Wire.Wire;

import java.util.List;

public interface RemoteGateway {

    List<BlockSystem> loadBlockSystems();
    List<Connection>  loadConnections();
    List<Wire>        loadWires();
    List<Packet>      loadPackets();

    void saveBlockSystems(List<BlockSystem> v);
    void saveConnections(List<Connection> v);
    void saveWires(List<Wire> v);
    void savePackets(List<Packet> v);

    class Noop implements RemoteGateway {
        public List<BlockSystem> loadBlockSystems() { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Connection>  loadConnections()  { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Wire>        loadWires()        { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Packet>      loadPackets()      { throw new UnsupportedOperationException("Remote not ready"); }
        public void saveBlockSystems(List<BlockSystem> v) { throw new UnsupportedOperationException("Remote not ready"); }
        public void saveConnections(List<Connection> v)  { throw new UnsupportedOperationException("Remote not ready"); }
        public void saveWires(List<Wire> v)              { throw new UnsupportedOperationException("Remote not ready"); }
        public void savePackets(List<Packet> v)          { throw new UnsupportedOperationException("Remote not ready"); }
    }
}

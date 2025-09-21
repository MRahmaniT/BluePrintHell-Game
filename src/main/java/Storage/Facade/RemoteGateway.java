package Storage.Facade;

import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.Connection;
import MVC.Model.GameEntities.GameData;
import MVC.Model.GameEntities.Packet;
import MVC.Model.GameEntities.Wire.Wire;
import Storage.RealTime.GameEnvironment.GameDataStorage;

import java.util.List;

public interface RemoteGateway {

    List<BlockSystem> loadBlockSystems();
    List<Connection>  loadConnections();
    List<Wire>        loadWires();
    List<Packet>      loadPackets();
    GameData    loadGameData();

    void saveBlockSystems(List<BlockSystem> v);
    void saveConnections(List<Connection> v);
    void saveWires(List<Wire> v);
    void savePackets(List<Packet> v);
    void saveGameData(GameData v);

    class Noop implements RemoteGateway {
        public List<BlockSystem> loadBlockSystems() { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Connection>  loadConnections()  { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Wire>        loadWires()        { throw new UnsupportedOperationException("Remote not ready"); }
        public List<Packet>      loadPackets()      { throw new UnsupportedOperationException("Remote not ready"); }

        @Override
        public GameData loadGameData() {
            return null;
        }

        public void saveBlockSystems(List<BlockSystem> v) { throw new UnsupportedOperationException("Remote not ready"); }
        public void saveConnections(List<Connection> v)  { throw new UnsupportedOperationException("Remote not ready"); }
        public void saveWires(List<Wire> v)              { throw new UnsupportedOperationException("Remote not ready"); }
        public void savePackets(List<Packet> v)          { throw new UnsupportedOperationException("Remote not ready"); }

        @Override
        public void saveGameData(GameData v) {

        }
    }
}

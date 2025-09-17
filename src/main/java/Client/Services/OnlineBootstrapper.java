package Client.Services;

import Client.Storage.LocalSnapshotReader;
import Client.Storage.Facade.RemoteGateway;

import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Model.GameEntities.Wire.Wire;

import java.util.List;

public final class OnlineBootstrapper {
    private OnlineBootstrapper(){}

    public static void bootstrap(RemoteGateway remote) {
        // Pull remote
        List<BlockSystem> rBs = remote.loadBlockSystems();
        List<Connection>  rCs = remote.loadConnections();
        List<Wire>        rWs = remote.loadWires();
        List<Packet>      rPs = remote.loadPackets();

        boolean remoteEmpty = isEmpty(rBs) && isEmpty(rCs) && isEmpty(rWs) && isEmpty(rPs);

        if (remoteEmpty) {
            // Push local snapshot if any
            List<BlockSystem> lBs = LocalSnapshotReader.readBlockSystems();
            List<Connection>  lCs = LocalSnapshotReader.readConnections();
            List<Wire>        lWs = LocalSnapshotReader.readWires();
            List<Packet>      lPs = LocalSnapshotReader.readPackets();

            boolean localHasData = !(isEmpty(lBs) && isEmpty(lCs) && isEmpty(lWs) && isEmpty(lPs));
            if (localHasData) {
                remote.saveBlockSystems(lBs);
                remote.saveConnections(lCs);
                remote.saveWires(lWs);
                remote.savePackets(lPs);
            }
        } else {
            // Remote has data -> do nothing; gameplay will read from remote via facade.
            // (Optionally: archive local files or flag as "synced")
        }
    }

    private static boolean isEmpty(List<?> l) { return l == null || l.isEmpty(); }
}

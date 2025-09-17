package Client.Controller.Packets.Spawning;

import Client.Model.Enums.PortRole;
import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FindWire {

    private final List<BlockSystem> blockSystems;
    private final List<Connection> connections;
    private final BlockSystem blockSystem;
    private final Packet packet;

    public FindWire(List<BlockSystem> blockSystems, List<Connection> connections, BlockSystem blockSystem, Packet packet) {
        this.blockSystems = blockSystems;
        this.connections = connections;
        this.blockSystem = blockSystem;
        this.packet = packet;
    }

    public Connection pickFreeOutgoingConnection() {

        List<Connection> options = new ArrayList<>();

        for (Connection connection : connections) {

            // wire is busy
            if (connection.isPacketOnLine()) continue;

            // destination system is not active
            if (!blockSystems.get(connection.getToSystemId()).isActive()) continue;

            // destination system is full
            if (blockSystems.get(connection.getToSystemId()).queueCount() == blockSystems.get(connection.getToSystemId()).getCapacity()) continue;

            if (connection.getFromSystemId() == blockSystem.getId() &&
                    isOutput(blockSystem, connection.getFromPortId()) &&
                    !packet.isDoNotFindCompatible()) {
                if (isCompatible(blockSystem, connection.getFromPortId(), packet)) {
                    return connection;
                } else {
                    options.add(connection);
                }
            } else if (connection.getFromSystemId() == blockSystem.getId() &&
                    isOutput(blockSystem, connection.getFromPortId()) &&
                    packet.isDoNotFindCompatible()) {
                if (isCompatible(blockSystem, connection.getFromPortId(), packet)) {
                    options.add(connection);
                } else {
                    return connection;
                }
                packet.setDoNotFindCompatible(false);
            }
        }

        if (options.isEmpty()) return null;

        // choose random wire
        Random random = new Random();
        int randomOption = random.nextInt(options.size());
        return options.get(randomOption);
    }

    // Helpers
    private static boolean isOutput(BlockSystem blockSystem, int port) {
        return blockSystem.getPort(port).getRole() == PortRole.OUT;
    }

    private static boolean isCompatible(BlockSystem blockSystem, int port, Packet packet) {
        return Objects.equals(blockSystem.getPort(port).getType().toString(), packet.getPacketType().toString());
    }
}

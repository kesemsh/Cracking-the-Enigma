package users;

import machine.Machine;
import machine.engine.MachineEngine;
import machine.engine.MachineEngineImpl;
import object.user.type.UserType;

public class UBoat extends User {
    private final MachineEngine machineEngine;

    public UBoat(String name) {
        super(name, UserType.UBOAT);
        machineEngine = new MachineEngineImpl();
    }

    public MachineEngine getMachineEngine() {
        return machineEngine;
    }
}

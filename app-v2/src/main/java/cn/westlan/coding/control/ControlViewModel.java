package cn.westlan.coding.control;

import androidx.lifecycle.ViewModel;
import cn.westlan.coding.core.bean.Firmware;
import cn.westlan.coding.core.bean.Identifier;
import cn.westlan.coding.core.connect.PrintContext;

public class ControlViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private PrintContext printContext;
    private Firmware firmware;
    private Identifier identifier;
    private boolean locked;

    public PrintContext getPrintContext() {
        return printContext;
    }

    public void setPrintContext(PrintContext printContext) {
        this.printContext = printContext;
    }

    public Firmware getFirmware() {
        return firmware;
    }

    public void setFirmware(Firmware firmware) {
        this.firmware = firmware;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
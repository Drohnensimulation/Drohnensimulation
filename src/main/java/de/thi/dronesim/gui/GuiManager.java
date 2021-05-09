package de.thi.dronesim.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.gui.jframe.IInstrumentView;
import de.thi.dronesim.gui.mview.MView;

/**
 * Class for managing gui types (simple and dview)
 *
 * @author Michael Weichenrieder
 */
public class GuiManager implements ISimulationChild {

    private Simulation simulation;
    private IInstrumentView instrumentView;
    private boolean isDView;

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    public GuiManager() {
        // Dark mode
        FlatDarkLaf.install();
    }

    /**
     * Opens a {@link de.thi.dronesim.gui.mview.MView} gui
     */
    public void openMViewGui() {
        if(!existsGui()) {
            instrumentView = new MView();
            isDView = false;
        }
    }

    /**
     * Opens a {@link de.thi.dronesim.gui.dview.DView} gui
     */
    public void openDViewGui() {
        if(!existsGui()) {
            // TODO: Open DView and save to attribute
            isDView = true;
        }
    }

    /**
     * @return True if any gui is opened
     */
    public boolean existsGui() {
        return instrumentView != null;
    }

    /**
     * @return True if the opened gui is a {@link de.thi.dronesim.gui.dview.DView}
     */
    public boolean isDView() {
        return isDView;
    }
}

package de.thi.dronesim.obstacle;

import com.google.gson.Gson;
import com.jme3.math.Vector3f;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.persistence.entity.ObstacleConfig;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

/**
 * @author Christian Schmied
 */
public class UfoObjsSpeedTest {
    private static final Logger logger = LogManager.getLogger(UfoObjsSpeedTest.class);
    private static int NUM_RUNS = 50_000;
    private static UfoObjs instance;
    private static Simulation simulation;

    /**
     * Helper method to load the configuration
     * @author Michael Küchenmeister
     */
    private static void loadTestConfig() {
        String jsonObj =
                "{" +
                        "  \"obstacles\":[" +
                        "    {" +
                        "      \"modelName\":\"testObj1\"," +
                        "      \"modelPath\":\"/test1\"," +
                        "      \"id\":1," +
                        "      \"hitboxes\":[{\"position\":[2.5,1.0,3.5], \"rotation\":[0.0,0.0,0.0], \"dimension\":[0.5,0.5,0.5]}]," +
                        "      \"position\":[2.5,1.0,3.5]," +
                        "      \"rotation\":[0.0,0.0,0.0]," +
                        "      \"scale\":[0.25,0.25,0.25]" +
                        "    }," +
                        "    {" +
                        "      \"modelName\":\"testObj2\"," +
                        "      \"modelPath\":\"/test2\"," +
                        "      \"id\":2," +
                        "      \"hitboxes\":[{\"position\":[-0.5,1.0,3.5], \"rotation\":[0.0,0.0,0.0], \"dimension\":[0.5,0.5,0.5]}]," +
                        "      \"position\":[-0.5,1.0,3.5]," +
                        "      \"rotation\":[0.0,0.0,0.0]," +
                        "      \"scale\":[0.25,0.25,0.25]" +
                        "    }," +
                        "    {" +
                        "      \"modelName\":\"testObj3\"," +
                        "      \"modelPath\":\"/test3\"," +
                        "      \"id\":3," +
                        "      \"hitboxes\":[{\"position\":[1.0,1.0,5.0], \"rotation\":[0.0,0.0,0.0], \"dimension\":[0.5,0.5,0.5]}]," +
                        "      \"position\":[1.0,1.0,5.0]," +
                        "      \"rotation\":[0.0,0.0,0.0]," +
                        "      \"scale\":[0.25,0.25,0.25]" +
                        "    }," +
                        "    {" +
                        "      \"modelName\":\"testObj4\"," +
                        "      \"modelPath\":\"/test4\"," +
                        "      \"id\":4," +
                        "      \"hitboxes\":[{\"position\":[4.0,10.0,1.0], \"rotation\":[0.0,0.0,0.0], \"dimension\":[0.5,0.5,0.5]}]," +
                        "      \"position\":[4.0,10.0,1.0]," +
                        "      \"rotation\":[0.0,0.0,0.0]," +
                        "      \"scale\":[0.25,0.25,0.25]" +
                        "    }" +
                        "  ]," +
                        "  \"config\":{\"rayDensity\":10}" +
                        "}";

        Gson gson = new Gson();
        ObstacleConfig obstacleConfig = gson.fromJson(jsonObj, ObstacleConfig.class);

        LinkedList<ObstacleConfig> configList = new LinkedList<>();
        configList.add(obstacleConfig);

        SimulationConfig config = simulation.getConfig();
        config.setObstacleConfigList(configList);

        instance.initialize(simulation);
    }

    @BeforeEach
    public void setup() {
        simulation = new Simulation();
        simulation.prepare();
        instance = simulation.getChild(UfoObjs.class);
        loadTestConfig(); // Load test obstacles, see method below
    }

    @Test
    public void checkBodySpeedFasterThanRays() {
        long start = System.currentTimeMillis();
        Vector3f origin = new Vector3f(0, 0, 0);
        for (int i = 0; i < NUM_RUNS; i++) {
            origin.addLocal(1, 0, 0);
            instance.checkSphereRayCollisionImp(origin, 2);
        }
        long rayDuration = System.currentTimeMillis() - start;
        logger.info("TimeRay: {}", rayDuration);

        long startBody = System.currentTimeMillis();
        origin = new Vector3f(0, 0, 0);
        for (int i = 0; i < NUM_RUNS; i++) {
            origin.addLocal(1, 0, 0);
            instance.checkSphereCollision(origin, 2);
        }
        long bodyDuration = System.currentTimeMillis() - startBody;
        System.out.println("body: " + bodyDuration + ",   ray: " + rayDuration);
        logger.info("TimeBody: {}", bodyDuration);

        Assertions.assertTrue(bodyDuration <= rayDuration);
    }
}

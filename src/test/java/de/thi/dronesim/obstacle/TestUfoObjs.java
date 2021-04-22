package de.thi.dronesim.obstacle;

import com.google.gson.Gson;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;
import de.thi.dronesim.obstacle.entity.Obstacle;
import org.junit.jupiter.api.*;
import com.jme3.math.Vector3f;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UfoObjs}
 */
public class TestUfoObjs {
    private static UfoObjs instance;
    private ObstacleDTO obsDTO1 = null;
    private ObstacleDTO obsDTO2 = null;
    private Obstacle o1 = null;
    private Obstacle o2 = null;

    @BeforeEach
    public void setup() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        instance = simulation.getChild(UfoObjs.class);
        //TODO Go through the Config Provider (Simulation.config)
        Gson gson = new Gson();

        if(obsDTO1 == null && obsDTO2 == null) {
            String obs1JSON = "{\"modelName\":\"test1\"," +
                    " \"modelPath\":\"./test1\", " +
                    " \"id\":1, " +
                    " \"hitboxes\":[{\"position\":[4.5,4.5,4.5], \"rotation\":[0.0,1.0,0.0], \"dimension\":[3.0,3.0,3.0]}], " +
                    " \"position\":[4.5,4.5,4.5], " +
                    " \"rotation\":[0.0,1.0,0.0], " +
                    " \"scale\":[2.0,2.0,2.0]}";
            obsDTO1 = gson.fromJson(obs1JSON, ObstacleDTO.class);

            String obs2JSON = "{\"modelName\":\"test2\"," +
                    " \"modelPath\":\"./test2\", " +
                    " \"id\":2, " +
                    " \"hitboxes\":[{\"position\":[8.5,8.5,8.5], \"rotation\":[0.0,1.0,0.0], \"dimension\":[5.0,5.0,5.0]}], " +
                    " \"position\":[8.5,8.5,8.5], " +
                    " \"rotation\":[0.0,1.0,0.0], " +
                    " \"scale\":[1.0,1.0,1.0]}";
            obsDTO2 = gson.fromJson(obs2JSON, ObstacleDTO.class);
        }

        if(o1 == null && o2 == null) {
            o1 = instance.addObstacle(obsDTO1);
            o2 = instance.addObstacle(obsDTO2);
        }
    }

    /**
     * Test method for {@link UfoObjs#addObstacle(ObstacleDTO)}
     */
    @Disabled // TODO: Enable, if class Obstacle have a constructor and the method getObstacles() is implemented
    @Test
    public void addObstacle() {
        // Create test obstacles to compare it with the return value from addObstacle()
        Obstacle testObs1 = new Obstacle(); // TODO: Add parameters
        Obstacle testObs2 = new Obstacle(); // TODO: Add parameters

        // Test, if the returned object from addObstacle() is correct
        // o1 and o2 have already been specified in setup() method
        assertEquals(testObs1,o1,"The returned Obstacle (o1) is false or null!");
        assertEquals(testObs2,o2,"The returned Obstacle (o2) is false or null!");

        // Test, if obstacles are added
        Set<Obstacle> addedObstacles = instance.getObstacles();
        if(addedObstacles != null) {
            assertTrue(addedObstacles.contains(o1) && addedObstacles.contains(o2),"Obstacles were not added properly");
        } else {
            fail("The return value of getObstacles() is null");
        }
    }

    /**
     * Test method for {@link UfoObjs#removeObstacle(ObstacleDTO)}
     */
    @Disabled // TODO: Enable, if the methods getObstacles() and addObstacles() are implemented
    @Test
    public void removeObstacleDTO() {
        // Remove obstacle and check the return value
        assertTrue(instance.removeObstacle(obsDTO1),"ObstacleDTO (obsDTO1) cannot removed!");

        // Test, if obstacle is removed
        Set<Obstacle> obstacles = instance.getObstacles();
        if(obstacles != null) {
            assertFalse(obstacles.contains(o1),"ObstacleDTO (obsDTO1) was not removed properly");
        } else {
            fail("The return value of getObstacles() is null");
        }

        // Check, if the return value of an unavailable object is "false"
        assertFalse(instance.removeObstacle(obsDTO1),"The return value of an unavailable Obstacle (obsDTO1) must be false!");
    }

    /**
     * Test method for {@link UfoObjs#removeObstacle(Obstacle)}
     */
    @Disabled // TODO: Enable, if the method getObstacles() and addObstacles() are implemented
    @Test
    public void removeObstacleObj() {
        // Remove obstacle and check the return value
        assertTrue(instance.removeObstacle(o2),"Obstacle (o2) cannot removed!");

        // Test, if obstacle is removed
        Set<Obstacle> obstacles = instance.getObstacles();
        if(obstacles != null) {
            assertFalse(obstacles.contains(o2),"Obstacle (o2) was not removed properly");
        } else {
            fail("The return value of getObstacles() is null");
        }

        // Check, if the return value of an unavailable object is "false"
        assertFalse(instance.removeObstacle(o2),"The return value of an unavailable Obstacle (o2) must be false!");
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCone(Vector3f, Vector3f, float, Vector3f)}
     */
    @Test
    public void pruefeSensorCone() {
        // TODO: Write test method for ray test in a cone
    }

    /**
     * Test method for {@link UfoObjs#checkSensorPyramid(Vector3f, Vector3f, float, Vector3f)}
     */
    @Test
    public void checkSensorPyramid() {
        // TODO: Write test method for ray test in a pyramid
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCuboid(Vector3f, Vector3f, Vector3f)}
     */
    @Test
    public void checkSensorCuboid() {
        // TODO: Write test method for ray test in a cuboid
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCylinder(Vector3f, Vector3f, Vector3f)}
     */
    @Test
    public void checkSensorCylinder() {
        // TODO: Write test method for ray test in a cylinder
    }

    /**
     * Test method for {@link UfoObjs#load(Object)}
     */
    // TODO: Actually use the SimulationContext to load the Configuration...
    // Slight Improvement for code readability load the JSON from the Resources (this can also be done for test Resources)
    @Disabled // TODO: Enable, if class Obstacle have a constructor and the methods getObstacles() and addObstacles() are implemented
    @Test
    public void load() {
        String jsonObj =
                "{" +
                "  \"hindernisse\":[" +
                "    {" +
                "      \"modelName\":\"testLoad\"," +
                "      \"modelPath\":\"/test\"," +
                "      \"id\":3," +
                "      \"hitboxes\":[{\"position\":[6.5,6.5,6.5], \"rotation\":[0.0,1.0,0.0], \"dimension\":[3.0,3.0,3.0]}]," +
                "      \"position\":[6.5,6.5,6.5]," +
                "      \"rotation\":[0.0,1.0,0.0]," +
                "      \"scale\":[1.0,1.0,1.0]" +
                "    }" +
                "  ]," +
                "  \"config\":{\"numberOfRays\":5}" +
                "}";

        Gson gson = new Gson();
        ObstacleJsonDTO jsonDTO = gson.fromJson(jsonObj, ObstacleJsonDTO.class);
        //instance.load(jsonDTO);

        // Create a test obstacle to compare it with the return value from addObstacle()
        Obstacle o3 = new Obstacle(); // TODO: Add parameters

        // Test, if obstacle is added
        Set<Obstacle> obstacles = instance.getObstacles();
        if(obstacles != null) {
            assertFalse(obstacles.contains(o3),"Obstacle (o3) was not added properly");
        } else {
            fail("The return value of getObstacles() is null");
        }
    }

    /**
     * Test method for {@link UfoObjs#save()}
     */
    @Test
    public void save() {
        // TODO: Write test method for save the actual environmental context into file
    }
}

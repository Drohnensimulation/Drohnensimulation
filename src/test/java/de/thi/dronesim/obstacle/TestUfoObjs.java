package de.thi.dronesim.obstacle;

import com.google.gson.Gson;
import com.jme3.math.Vector3f;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.helpers.Jme3MathHelper;
import de.thi.dronesim.obstacle.dto.HitBoxDTO;
import de.thi.dronesim.obstacle.dto.ObstacleConfigurationDTO;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.obstacle.util.HitBoxRigidBody;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.persistence.entity.ObstacleConfig;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UfoObjs}
 *
 * @author Michael Küchenmeister
 */

public class TestUfoObjs {
    private static UfoObjs instance;
    private static Simulation simulation;

    @BeforeEach
    public void setup() {
        simulation = new Simulation();
        simulation.prepare();
        instance = simulation.getChild(UfoObjs.class);
        loadTestConfig(); // Load test obstacles, see method below
    }

    /**
     * Test method for {@link UfoObjs#addObstacle(ObstacleDTO)}
     */
    @Test
    public void addObstacle() {
        Gson gson = new Gson();
        // Create a new ObstacleDTO Object
        String obsJSON = "{\"modelName\":\"testAddDTO\"," +
                " \"modelPath\":\"./test5\", " +
                " \"id\":5, " +
                " \"hitboxes\":[{\"position\":[2.0,2.0,2.0], \"rotation\":[0.0,0.0,0.0], \"dimension\":[0.5,0.5,0.5]}], " +
                " \"position\":[2.0,2.0,2.0], " +
                " \"rotation\":[0.0,0.0,0.0], " +
                " \"scale\":[0.5,0.5,0.5]}";
        ObstacleDTO obsDTO1 = gson.fromJson(obsJSON, ObstacleDTO.class);

        Obstacle o1 = instance.addObstacle(obsDTO1);

        // Check the values of the added obstacle
        assertEquals("testAddDTO", o1.getModelName());
        assertEquals("./test5", o1.getModelPath());
        assertEquals(5L, o1.getID());
        HitBoxRigidBody hitbox = o1.getHitboxes().stream().findFirst().get();
        assertEquals(o1,hitbox.getObstacle());
        assertArrayEquals(new Float[]{2.0f,2.0f,2.0f}, o1.getPosition());
        assertArrayEquals(new Float[]{0.0f,0.0f,0.0f}, o1.getRotation());
        assertArrayEquals(new Float[]{0.5f,0.5f,0.5f}, o1.getScale());

        // Check, if obstacle is added to the context
        Set<Obstacle> addedObstacles = instance.getObstacles();
        if(addedObstacles != null) {
            assertTrue(addedObstacles.contains(o1),"Obstacle is not added properly");
        } else {
            fail("The return value of getObstacles() is null");
        }
    }

    /**
     * Test method for {@link UfoObjs#removeObstacle(ObstacleDTO)}
     */
    @Test
    public void removeObstacleDTO() {
        SimulationConfig config = simulation.getConfig();
        List<ObstacleConfig> configList = config.getObstacleConfigList();
        ObstacleConfig obstacleConfig = configList.get(0);
        ObstacleDTO obstacleToRemove = obstacleConfig.obstacles.stream().findFirst().get(); // Get the first element from obstacleDTO set, to remove it
        Long removedID = obstacleToRemove.id;

        // Remove obstacle and check the return value
        assertTrue(instance.removeObstacle(obstacleToRemove),"ObstacleDTO cannot removed!");

        // Check by ID, if obstacle is removed
        Set<Obstacle> obstacles = instance.getObstacles();
        for(Obstacle o : obstacles) {
            assertNotEquals(removedID,o.getID());
        }

        // Check, if the return value of an unavailable object is "false"
        assertFalse(instance.removeObstacle(obstacleToRemove),"The return value of an unavailable Obstacle (obsDTO1) must be false!");
    }

    /**
     * Test method for {@link UfoObjs#removeObstacle(Obstacle)}
     */
    @Test
    public void removeObstacleObj() {
       Set<Obstacle> obstacles = instance.getObstacles();
        Obstacle obstacleToRemove = obstacles.stream().findFirst().get(); // Get the first element from obstacle set, to remove it
        Long removedID = obstacleToRemove.getID();

        // Remove obstacle and check the return value
        assertTrue(instance.removeObstacle(obstacleToRemove),"Obstacle cannot removed!");

        // Test, if obstacle is removed
        obstacles = instance.getObstacles();
        for(Obstacle o : obstacles) {
            assertNotEquals(removedID,o.getID());
        }

        // Check, if the return value of an unavailable object is "false"
        assertFalse(instance.removeObstacle(obstacleToRemove),"The return value of an unavailable Obstacle (o2) must be false!");
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCone(com.jme3.math.Vector3f, com.jme3.math.Vector3f, float, com.jme3.math.Vector3f)}
     */
    @Test
    public void checkSensorCone() {
        Vector3f sensorPos = Jme3MathHelper.of(1,1,1);
        Vector3f direction = Jme3MathHelper.of(0,0,1);
        Vector3f open45Deg = Jme3MathHelper.of(1,0,1);

        Set<HitMark> hits = instance.checkSensorCone(sensorPos,direction,10,open45Deg);
        checkRayTestValues(hits); // See this method below
    }

    /**
     * Test method for {@link UfoObjs#checkSensorPyramid(com.jme3.math.Vector3f, com.jme3.math.Vector3f, float, com.jme3.math.Vector3f)}
     */
    @Test
    public void checkSensorPyramid() {
        Vector3f sensorPos = Jme3MathHelper.of(1,1,1);
        Vector3f direction = Jme3MathHelper.of(0,0,1);
        Vector3f open45Deg = Jme3MathHelper.of(1,0,1);

        Set<HitMark> hits = instance.checkSensorPyramid(sensorPos,direction,10,open45Deg);
        checkRayTestValues(hits); // See this method below
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCuboid(com.jme3.math.Vector3f, com.jme3.math.Vector3f, com.jme3.math.Vector3f)}
     */
    @Test
    public void checkSensorCuboid() {
        Vector3f sensorPos = Jme3MathHelper.of(1,1,1);
        Vector3f direction = Jme3MathHelper.of(0,0,1);
        Vector3f dimension = Jme3MathHelper.of(6,2,6);

        Set<HitMark> hits = instance.checkSensorCuboid(sensorPos,direction,dimension);
        checkRayTestValues(hits); // See this method below
    }

    /**
     * Test method for {@link UfoObjs#checkSensorCylinder(Vector3f, Vector3f, Vector3f)}
     */
    @Test
    public void checkSensorCylinder() {
        Vector3f sensorPos = Jme3MathHelper.of(1,1,1);
        Vector3f direction = Jme3MathHelper.of(0,0,1);
        Vector3f dimension = Jme3MathHelper.of(5,5,10);

        Set<HitMark> hits = instance.checkSensorCylinder(sensorPos,direction,dimension);
        checkRayTestValues(hits); // See this method below
    }

    /**
     * Test method for {@link UfoObjs#save()}
     */
    @Test
    public void save() {
        ObstacleJsonDTO testDTO = instance.save();

        if(testDTO == null) {
            fail("The returned ObstacleJsonDTO is null!");
        }

        // Check the value of rayDensity
        ObstacleConfigurationDTO testConfDTO = testDTO.config;
        assertEquals(10,testConfDTO.rayDensity);

        Set<ObstacleDTO> obstacles = testDTO.obstacles;
        int numberObstacles = 0;

        // Check the values of the different obstacles
        for(ObstacleDTO o : obstacles) {
            if(o.id == 1L) {
                assertEquals("testObj1", o.modelName);
                assertEquals("/test1", o.modelPath);
                assertArrayEquals(new Float[]{2.5f,1.0f,3.5f},o.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},o.rotation);
                assertArrayEquals(new Float[]{0.25f,0.25f,0.25f},o.scale);
                Set<HitBoxDTO> hitbox = o.hitboxes;
                HitBoxDTO h = hitbox.stream().findFirst().get(); // Get the first element from hitbox set
                assertArrayEquals(new Float[]{2.5f,1.0f,3.5f},h.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},h.rotation);
                assertArrayEquals(new Float[]{0.5f,0.5f,0.5f},h.dimension);
                numberObstacles++;
            } else if(o.id == 2L) {
                assertEquals("testObj2", o.modelName);
                assertEquals("/test2", o.modelPath);
                assertArrayEquals(new Float[]{-0.5f,1.0f,3.5f},o.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},o.rotation);
                assertArrayEquals(new Float[]{0.25f,0.25f,0.25f},o.scale);
                Set<HitBoxDTO> hitbox = o.hitboxes;
                HitBoxDTO h = hitbox.stream().findFirst().get(); // Get the first element from hitbox set
                assertArrayEquals(new Float[]{-0.5f,1.0f,3.5f},h.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},h.rotation);
                assertArrayEquals(new Float[]{0.5f,0.5f,0.5f},h.dimension);
                numberObstacles++;
            } else if(o.id == 3L) {
                assertEquals("testObj3", o.modelName);
                assertEquals("/test3", o.modelPath);
                assertArrayEquals(new Float[]{1.0f,1.0f,5.0f},o.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},o.rotation);
                assertArrayEquals(new Float[]{0.25f,0.25f,0.25f},o.scale);
                Set<HitBoxDTO> hitbox = o.hitboxes;
                HitBoxDTO h = hitbox.stream().findFirst().get(); // Get the first element from hitbox set
                assertArrayEquals(new Float[]{1.0f,1.0f,5.0f},h.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},h.rotation);
                assertArrayEquals(new Float[]{0.5f,0.5f,0.5f},h.dimension);
                numberObstacles++;
            } else if(o.id == 4L) {
                assertEquals("testObj4", o.modelName);
                assertEquals("/test4", o.modelPath);
                assertArrayEquals(new Float[]{4.0f,10.0f,1.0f},o.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},o.rotation);
                assertArrayEquals(new Float[]{0.25f,0.25f,0.25f},o.scale);
                Set<HitBoxDTO> hitbox = o.hitboxes;
                HitBoxDTO h = hitbox.stream().findFirst().get(); // Get the first element from hitbox set
                assertArrayEquals(new Float[]{4.0f,10.0f,1.0f},h.position);
                assertArrayEquals(new Float[]{0.0f,0.0f,0.0f},h.rotation);
                assertArrayEquals(new Float[]{0.5f,0.5f,0.5f},h.dimension);
                numberObstacles++;
            }
        }
        assertEquals(4,numberObstacles, "The returned ObstacleJsonDTO contains not all obstacles!");
    }

    /**
     * Helper method to load the configuration
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
        ObstacleConfig obstacleConfig = gson.fromJson(jsonObj,ObstacleConfig.class);

        LinkedList<ObstacleConfig> configList = new LinkedList<>();
        configList.add(obstacleConfig);

        SimulationConfig config = simulation.getConfig();
        config.setObstacleConfigList(configList);

        instance.setSimulation(simulation);
    }

    /**
     * Helper Method for checkSensor... Unit tests
     * @param hits
     *
     *                   (1.0,1.0,1.0)
     *                      Drone         ------
     *                        *           | o4 |
     *                      | | |         ------
     *                     |     |     (4.0,10.0,1.0)
     *                   |    |    |
     *            ------      |     ------
     *            | o2 |      |     | o1 |
     *            ------      |     ------
     *       (-0.5,1.0,3.5)   |   (2.5,1.0,3.5)
     *                    ------
     *                    | o3 |
     *                    ------
     *                (1.0,1.0,5.0)
     */
    private static void checkRayTestValues(Set<HitMark> hits) {
        Set<Obstacle> obstacles = instance.getObstacles();
        Obstacle hitObs1 = null, hitObs2 = null, hitObs3 = null, hitObs4 = null;
        for(Obstacle o : obstacles) {
            if(o.getID().equals(1L)) {
                hitObs1 = o;
            } else if(o.getID().equals(2L)) {
                hitObs2 = o;
            } else if(o.getID().equals(3L)) {
                hitObs3 = o;
            } else if(o.getID().equals(4L)) {
                hitObs4 = o;
            }
        }

        int numberHitObj1 = 0; // Number of hits on obstacle 1
        int numberHitObj2 = 0; // Number of hits on obstacle 2
        int numberHitObj3 = 0; // Number of hits on obstacle 3
        int numberHitObj4 = 0; // Number of hits on obstacle 4

        if(hits != null) {
            for (HitMark h : hits) {
                if (h.getObstacle().equals(hitObs1)) {
                    numberHitObj1++;
                } else if (h.getObstacle().equals(hitObs2)) {
                    numberHitObj2++;
                } else if (h.getObstacle().equals(hitObs3)) {
                    numberHitObj3++;
                } else if (h.getObstacle().equals(hitObs4)) {
                    numberHitObj4++; // Check a obstacle which is out of range. It´s on position (4.0, 10.0, 1.0)
                }
            }
            if(numberHitObj1+numberHitObj2+numberHitObj3+numberHitObj4 == 0) {
                fail("No obstacle was hitted!");
            } else {
                assertTrue(numberHitObj1 > 0, "Obstacle 1 on position (2.5, 1.0, 3.5) was not hitted!");
                assertTrue(numberHitObj2 > 0, "Obstacle 2 on position (-0.5, 1.0, 3.5) was not hitted!");
                assertTrue(numberHitObj3 > 0, "Obstacle 3 on position (1.0, 1.0, 5.0 was not hitted!");
                assertEquals(0, numberHitObj4, "Obstacle 4 on position (4.0, 10.0, 1.0) which should be out of range was hitted!");
            }
        } else {
            fail("The returned HitMark set is null");
        }
    }
}

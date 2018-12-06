package com.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.reporting.providers.ConsoleReportGenerator;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.*;

import static org.junit.Assert.assertEquals;

@Ignore
public class MyResourceTest {
    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule(new ConsoleReportGenerator());

    private static HttpServer server;
    private static WebTarget target;

    @BeforeClass
    public static void setUp() {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
    }

    /**
     * Sequential
     */

    @Test
    public void sequential_With1NumObjectsAnd10Delay() {
        String responseMsg = target
            .path("/seq/num-objects/1/delay/10")
            .request()
            .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void sequential_With10NumObjectsAnd10Delay() {
        String responseMsg = target
            .path("/seq/num-objects/10/delay/10")
            .request()
            .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void sequential_With100NumObjectsAnd100Delay() {
        String responseMsg = target
            .path("/seq/num-objects/100/delay/100")
            .request()
            .get(String.class);

        assertEquals("OK", responseMsg);
    }

    /**
     * Fork-Join
     */

    @Test
    public void forkJoin_With10000NumObjectsAnd100Delay() {
        String responseMsg = target
                .path("/fork-join/num-objects/1000/delay/100")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }


    @Test
    public void forkJoin_With100NumObjectsAnd100Delay() {
        String responseMsg = target
            .path("/fork-join/num-objects/100/delay/100")
            .request()
            .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 120_000)
    public void forkJoin_With10NumObjectsAnd10Delay() {
        String responseMsg = target
                .path("/fork-join/num-objects/10/delay/10")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void forkJoin_With1NumObjectsAnd10Delay() {
        String responseMsg = target
            .path("/fork-join/num-objects/1/delay/100")
            .request()
            .get(String.class);

        assertEquals("OK", responseMsg);
    }
}

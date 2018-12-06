package com.example;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.ConsoleReportGenerator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

@Ignore
public class WriteTests {
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
     * Write Tests
     *
     * Blocking IO calls with heavy string allocation
     */

    @JUnitPerfTest(threads = 1, durationMs = 15_000, warmUpMs = 5_000, maxExecutionsPerSecond = 11_000)
    @JUnitPerfTestRequirement(percentiles = "99:1200")
    @Test
    public void writeForkJoin_With1NumObjectsAnd10Lines() {
        String responseMsg = target
                .path("/write/fork-join/num-objects/1/lines/100")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void writeForkJoin_With10NumObjectsAnd100Lines() {
        String responseMsg = target
                .path("/write/fork-join/num-objects/10/lines/100")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void writeForkJoin_With100NumObjectsAnd100Lines() {
        String responseMsg = target
                .path("/write/fork-join/num-objects/100/lines/100")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }

    // @JUnitPerfTest(durationMs = 12_500, warmUpMs = 10_000, maxExecutionsPerSecond = 11_000)
    @Test
    public void writeForkJoin_With10NumObjectsAnd20000Lines() {
        String responseMsg = target
                .path("/write/fork-join/num-objects/10/lines/20000")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }

    @Test
    public void writeSequentially_With10NumObjectsAnd2000Lines() {
        String responseMsg = target
                .path("/write/seq/num-objects/100/lines/200")
                .request()
                .get(String.class);

        assertEquals("OK", responseMsg);
    }
}

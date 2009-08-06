/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summit.jbeacon;

import com.summit.jbeacon.beacons.MultiCastResourceBeacon;
import com.summit.jbeacon.beacons.MultiCastResourceBeaconException;
import com.summit.jbeacon.buoys.MultiCastResourceBuoy;
import com.summit.jbeacon.buoys.MultiCastResourceBuoyException;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author justin
 */
public class MultiCastResourceTest {

    private static Log log;
    private Map<MultiCastResourceBuoy, String> multiCastBuoys;
    private Map<MultiCastResourceBeacon, String> multiCastBeacons;
    private Map<MultiCastResourceBuoy, MultiCastResourceBeacon> multiCastPairs;

    @BeforeClass
    public static void setUpClass() throws Exception {
        log = LogFactory.getLog(MultiCastResourceTest.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    private ClassPathXmlApplicationContext applicationContext;

    @Before
    public final void setUp() {
        applicationContext =
                new ClassPathXmlApplicationContext(
                "testApplicationContext.xml");
        applicationContext.registerShutdownHook();
        multiCastBuoys = (Map<MultiCastResourceBuoy, String>) applicationContext.getBean("multiCastBuoys");
        multiCastBeacons = (Map<MultiCastResourceBeacon, String>) applicationContext.getBean("multiCastBeacons");
        multiCastPairs = (Map<MultiCastResourceBuoy, MultiCastResourceBeacon>) applicationContext.getBean("multiCastPairs");
    }

    @After
    public final void tearDown() {
        applicationContext.close();
        applicationContext = null;
        System.gc();
    }

    @Test
    public final void testMultiCastBouys() throws Exception {
        log.info("Running MultiCastResourceBuoy tests.");
        Set<Map.Entry<MultiCastResourceBuoy, String>> mapSet =
                multiCastBuoys.entrySet();
        for (Map.Entry<MultiCastResourceBuoy, String> e : mapSet) {
            log.info("Testing : " + e.getValue());
            try {
                e.getKey().startReceiver();
            } catch (MultiCastResourceBuoyException ex) {
                fail("\t" + ex.getMessage());
            }
            //Tests?
            //We sleep, let the thread run...
            Thread.sleep(1000);
            try {
                e.getKey().stopReceiver();
            } catch (MultiCastResourceBuoyException ex) {
                fail("\t" + ex.getMessage());
            }
        }
    }

    @Test
    public final void testMultiCastBeacons() throws Exception {
        log.info("Running MultiCastResourceBeacon tests.");
        Set<Map.Entry<MultiCastResourceBeacon, String>> mapSet =
                multiCastBeacons.entrySet();
        for (Map.Entry<MultiCastResourceBeacon, String> e : mapSet) {
            log.info("Testing : " + e.getValue());
            try {
                e.getKey().startListening();
            } catch (MultiCastResourceBeaconException ex) {
                fail("\t" + ex.getMessage());
            }
            //Tests?
            //We sleep, let the thread run...
            Thread.sleep(1000);
            try {
                e.getKey().stopListening();
            } catch (MultiCastResourceBeaconException ex) {
                fail("\t" + ex.getMessage());
            }
        }

    }

    @Test
    public final void testMultiCastPairs() throws Exception {
        log.info("Running MultiCastResourcePair tests.");
        Set<Map.Entry<MultiCastResourceBuoy, MultiCastResourceBeacon>> mapSet =
                multiCastPairs.entrySet();
        for (Map.Entry<MultiCastResourceBuoy, MultiCastResourceBeacon> e :
                mapSet) {
            log.info("Testing : " + multiCastBuoys.get(e.getKey()) 
                    + " < - > "  + multiCastBeacons.get(e.getValue()));
            try {
                e.getKey().startReceiver();
                e.getValue().startListening();
            } catch (MultiCastResourceBeaconException ex) {
                fail("\t" + ex.getMessage());
            } catch (MultiCastResourceBuoyException ex) {
                fail("\t" + ex.getMessage());
            }
            //Tests?
            //We sleep, let the thread run...
            //
            e.getValue().refreshData();
            //Thread.sleep(60000);
            try {
                e.getKey().stopReceiver();
                e.getValue().stopListening();
            } catch (MultiCastResourceBeaconException ex) {
                fail("\t" + ex.getMessage());
            } catch (MultiCastResourceBuoyException ex) {
                fail("\t" + ex.getMessage());
            }
        }
    }
}

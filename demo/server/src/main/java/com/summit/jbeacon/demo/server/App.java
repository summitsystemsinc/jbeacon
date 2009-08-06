package com.summit.jbeacon.demo.server;

import com.summit.jbeacon.beacons.MultiCastResourceBeacon;
import com.summit.jbeacon.beacons.MultiCastResourceBeaconException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, MultiCastResourceBeaconException {
        //application context loads the beacon, starts and stops it...
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("demoServerContext.xml");
        applicationContext.registerShutdownHook();


        MultiCastResourceBeacon beacon = (MultiCastResourceBeacon) applicationContext.getBean("beacon");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        while(true){
            System.out.println(MENU);
            input = in.readLine();
            LogFactory.getLog(App.class).info("Received: " + input);
            if(input.equalsIgnoreCase("q")||input.equalsIgnoreCase("quit")){
                LogFactory.getLog(App.class).info("Trying to quit");
                break;
            }else if(input.equalsIgnoreCase("r")){
                beacon.refreshData();
            }
        }
        System.out.println("Bye.");
        applicationContext.close();
        System.exit(0);
    }

    private static final String MENU = "\r\n" +
            "(R) to Refresh nodes\r\n" +
            "(Q or QUIT) to quit\r\n";
}

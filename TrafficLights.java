///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS com.pi4j:pi4j-core:2.3.0
//DEPS com.pi4j:pi4j-plugin-pigpio:2.3.0
//DEPS com.pi4j:pi4j-plugin-raspberrypi:2.3.0

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;

public class TrafficLights {

    private static TrafficLight redTrafficLight;
    private static TrafficLight yellowTrafficLight;
    private static TrafficLight greenTrafficLight;
    private static final int redLightPinBoardNumber = 26;
    private static final int yellowLightPinBoardNumber = 25;
    private static final int greenLightPinBoardNumber = 17;
    private static Context pi4j;

    public static void main(String[] args) {
        pi4j = Pi4J.newAutoContext();

        redTrafficLight = new TrafficLight("light_1", "redLight", redLightPinBoardNumber);
        yellowTrafficLight = new TrafficLight("light_2", "yellowLight", yellowLightPinBoardNumber);
        greenTrafficLight = new TrafficLight("light_3", "greenLight", greenLightPinBoardNumber);
        blinkAllLightForTest();

        for (int i = 0; i < 5; i ++) {
            simulateTrafficLights();
        }

        pi4j.shutdown();
    }

    private static void blinkAllLightForTest() {
        System.out.println("-- BLINK TEST START --");
        try {
            Thread.sleep(1000);
            turnOffTrafficLights();
            Thread.sleep(1000);
            turnOnTrafficLights();
            Thread.sleep(1000);
            turnOffTrafficLights();
            Thread.sleep(1000);
            turnOnTrafficLights();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-- BLINK TEST END --");
    }

       private static void simulateTrafficLights() {
        System.out.println(" ");
        System.out.println("START OF CYCLE");
        // Make sure that YELLOW and GREEN lights are OFF
        yellowTrafficLight.turnOff();
        greenTrafficLight.turnOff();

        // Turn ON RED light
        redTrafficLight.turnOn();

        // Wait 4s to TURN ON YELLOW light
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Turn ON YELLOW LIGHT
        yellowTrafficLight.turnOn();

        // Wait 1s to TURN OFF RED and YELLOW light
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        redTrafficLight.turnOff();
        yellowTrafficLight.turnOff();

        // Turn ON green light for 5s
        greenTrafficLight.turnOn();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Turn GREEN light OFF and turn YELLOW light ON
        greenTrafficLight.turnOff();
        yellowTrafficLight.turnOn();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("END OF CYCLE");
        System.out.println(" ");
    }

    private static void turnOffTrafficLights() {
        redTrafficLight.turnOff();
        yellowTrafficLight.turnOff();
        greenTrafficLight.turnOff();
    }

    private static void turnOnTrafficLights() {
        redTrafficLight.turnOn();
        yellowTrafficLight.turnOn();
        greenTrafficLight.turnOn();
    }

    private static class TrafficLight {
        private final String id;
        private final String name;
        private final int pinBoardNumber;
        private DigitalOutput light;

        public TrafficLight(String id, String name, int pinBoardNumber) {
            this.id = id;
            this.name = name;
            this.pinBoardNumber = pinBoardNumber;
            this.createDigitalOutput();
        }

        private void createDigitalOutput() {
            DigitalOutputConfigBuilder config = DigitalOutput.newConfigBuilder(pi4j)
                    .id(this.id)
                    .name(this.name)
                    .address(this.pinBoardNumber)
                    .shutdown(DigitalState.LOW)
                    .initial(DigitalState.HIGH)
                    .provider("pigpio-digital-output");
            this.light = pi4j.create(config);
        }

        private void turnOn() {
            this.light.on();
        }

        private void turnOff() {
            this.light.off();
        }

        private void toggle() {
            this.light.toggle();
        }
    }

}
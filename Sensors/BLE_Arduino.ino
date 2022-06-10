/*
  * Adapted from Arduino Nano 33 BLE Getting Started
  * which was adapted from Arduino BatteryMonitor example 
  * scl in 5a
  * This transmits the temperature as a string
 */
 
#include <Wire.h>
#include <Adafruit_TMP117.h>
#include <Adafruit_Sensor.h>

#include <ArduinoBLE.h>

Adafruit_TMP117  tmp117;

BLEService tempService("181A");  // User defined service

BLEStringCharacteristic tempCharacteristic("2A6E",  // standard 16-bit characteristic UUID
    BLERead, 5); // remote clients will only be able to read this

void setup() {
  //Serial.begin(9600);    // initialize serial communication
  //while (!Serial);

  pinMode(LED_BUILTIN, OUTPUT); // initialize the built-in LED pin

  //Temp sensor
  Serial.begin(115200);
  //while (!Serial) delay(10);     // will pause Zero, Leonardo, etc until serial console opens
  Serial.println("Adafruit TMP117 test!");

  // Try to initialize!
  while (!tmp117.begin()) {
    Serial.println("Failed to find TMP117 chip");
    //while (1) { delay(10); }
    delay(500);
  }
  Serial.println("TMP117 Found!");
  

  if (!BLE.begin()) {   // initialize BLE
    Serial.println("starting BLE failed!");
    while (1);
  }

  //ble setup
  BLE.setLocalName("DFU Nano");  // Set name for connection
  BLE.setAdvertisedService(tempService); // Advertise service
  tempService.addCharacteristic(tempCharacteristic); // Add characteristic to service
  BLE.addService(tempService); // Add service
  // greetingCharacteristic.setValue(greeting); // Set greeting string

  BLE.advertise();  // Start advertising
  Serial.print("Peripheral device MAC: ");
  Serial.println(BLE.address());
  Serial.println("Waiting for connections...");
}

void loop() {
  BLEDevice central = BLE.central();  // Wait for a BLE central to connect

  // if a central is connected to the peripheral:
  if (central) {
    Serial.print("Connected to central MAC: ");
    // print the central's BT address:
    Serial.println(central.address());
    // turn on the LED to indicate the connection:
    digitalWrite(LED_BUILTIN, HIGH);

    while (central.connected()){
      
      long interval = 10;
      unsigned long currentCount = 0;
      //unsigned long previousMillis = 0;
      //if(currentCount > interval)
      //{
        currentCount = 0;

        
        sensors_event_t temp; // create an empty event to be filled
        tmp117.getEvent(&temp); //fill the empty event object with the current measurements
        float tempReadingF = temp.temperature * 1.8 + 32;
        Serial.print("Temperature  "); Serial.print(tempReadingF);Serial.println(" degrees F");
        //Serial.print("Temperature  "); Serial.print(temp.temperature);Serial.println(" degrees C");
        Serial.println("");

        
        //tempCharacteristic.writeValue((int)temp.temperature);
        //tempCharacteristic.writeValue(temp.temperature);
//        unsigned char tempArray[5] = {0, byte(temp.temperature), 0, 0, 0};
        byte tempArray[2] = {tempReadingF/100, 0};
        //tempCharacteristic.setValue((temp.temperature * 1.8 +32));
        String tempString = String(tempReadingF);
        tempCharacteristic.setValue(tempString);
        //tempCharacteristic.setValue(temp.temperature * 5);
        //tempCharacteristic.setValue(tempArray);
        bool success = tempCharacteristic.valueUpdated();

        
        //Serial.print("Value updated?"); Serial.println(success);
        //BLE.advertise();  
         
        delay(2000);
      //} else {
        //Serial.print("Current val "); Serial.println(currentCount);
        //currentCount = currentCount + 1;
      //}
      
    } // keep looping while connected
    
    // when the central disconnects, turn off the LED:
    digitalWrite(LED_BUILTIN, LOW);
    Serial.print("Disconnected from central MAC: ");
    Serial.println(central.address());
  }
}

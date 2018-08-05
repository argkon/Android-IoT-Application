# Android-IoT-Application

This project was made having 2 cornerstones:
  - Java Application
  - Android Application

Specifically the Android Application receives sound (musictopic) and visual (flashtopic) command-messages which are sent by the Java 
Application, using the MQTT, in order to forward the responses to the user. At the same time, it has the ability to send the desirable 
download rate of the notifications to the Java app. This is implemented with the existence of constant and 2-way(ambidromy) communication 
between the 2 entitites. Furthermore, the Android App might be needed to have an internet connection, without this being the reason of 
its existence though. On the contrary, the connection with the MQTT server which is the only communication channel between the Android 
and the Java Apps, is more important. In this base, the Android App is able to provide the choice of automatic or manual connection with 
the MQTT Broker to the user, with the help of Automatic/Manual Mode.

### Java Application:

  The following commands-messages are being sent to the Android App:
- Flash On: The Flash is turned on for a specific time limit of a few seconds (non-periodic message) while, also the number as well as 
the rate of this command's execution can be determined (periodic message).
- Flash Off: Likewise, the same principals are used, with the only difference being that the flash is turned off, if its already on.
- Music On: Same as the previous two, however the action taken is that a predetermined sound clip is being played.
- Music off: Following the 3 previous commands, same principles are applied here with the sole difference that the music is paused.
- Random: A "random" function is implemented which ,when activated, a random command selected from the pool which is consisted of the 
previous 4 commands is being picked and activated for a random time window.
- Readfiles: The ability of reading and then categorizing all or a specific set (optionally) of CSV files which were given to us by the 
Scan lab of the Department of Informatics and Telecommunications of the University Of Athens, during the winter period of 2017-2018, for 
the purposes of the "K23β Software Development for Telecommunication Network Systems". The CSV files were produced using the EMOTIV 
machine and the help of students who attended the class this semester, without referencing any personal information. Specifically, 
starting off, the specified CSV files are being read and the Entropy vector is being calculated, which is composed of the values of 14 
different channels/sensors that the EMOTIV machine consists of. Continuing, regarding the available Train Set and by using the Weight knn 
classification algorithm, a category for each CSV file is being produced (either Eyes Open or Eyes Closed). Depending on the category 
produced, a new (filestopic) command "ON"/"OFF" is being made. Then, the proper Runnable is dropped into a buffer (FIFO queue of infinite 
length) and shortly after, the consumer thread gains periodically data from the queue. Each copied Runnable is run with the help of the 
threads while the MQTT broker guides it towards its final destination, the mobile terminal where it is being received.
- StopAll: any consumer thread action is being stopped and the buffer is cleaned using a "signal" 2(stopping all)
- Exit: The Java app shuts down using a "signal" 1 (exit)

### Android Application 

As mentioned before, the Android App is responsible for receiving sound and visual notifications. On Android Settings, the user has the 
ability to modify his IP address and the port that are used to connect to the MQTT broker. Also, the user is able to determine the rate 
that the command notifications are received. The chosen frequency is then shared with the Java App, through a special topic 
(frequencytopic). As soon as the Java App receives a new frequency, the consumer thread inactivity thread is reset, in order to match the 
rate of the "data pumping" from the buffer with the new desired frequency of the user. While this is happening, an Internet Checker which
is already implemented and is basically a background thread, is responsible for checking the internet connection stability and existence. 
In addition, two modes are available (manual and automatic) that determine the app's behavior. If Manual mode (default) is selected, the 
user is solely responsible for subscribng/unsubscribing from the MQTT server, using the corresponding buttons. On the contrary, if 
Automatic mode is selected, the MQTT connection is done automatically.

### Authors

  - Panayiotis Raptis,
  - Konstantinos-Marios Argyriades,
  - Dimitrios Anastasopoulos

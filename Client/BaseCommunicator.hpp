#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string>
#include <iostream>
using namespace std;

class BaseCommunicator
{
    private:
        int sockfd, portNo, n;
        struct sockaddr_in serv_addr;
        struct hostent *server;
        char buffer[256];

    public:
        BaseCommunicator();
        ~BaseCommunicator();
        int sendAcceleration(float accel);
        int sendVelocity(float speed);
        int sendPosition(float position);
        int sendPodTemperature(float temp);
        int sendStripeCount(int stripes);
        int sendDistance(float distance);
        int sendGroundProximity(float prox);
        int sendRailProximity(float prox);
        int batteryTemperature(float temp);
        int sendBatteryCurrent(float current);
        int sendBatteryVoltage(float volt);
        int sendAccum1(int status);
        int sendAccum2(int status);
        int sendPump1(int status);
        int sendPump2(int status);
        int sendData(string message);
        // void receiverThread();
};

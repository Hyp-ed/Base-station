#include "BaseCommunicator.cpp"

int main()
{
    // std :: thread (receiverThread);

    BaseCommunicator baseStation;

    baseStation.sendVelocity(300000000);
    baseStation.sendAccum1(0);

    return 0;
}

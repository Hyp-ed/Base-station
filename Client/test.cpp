#include "BaseCommunicator.cpp"

int main()
{
    // std :: thread (receiverThread);

    BaseCommunicator baseCommunicator;

    baseCommunicator.sendVelocity(300000000);
    baseCommunicator.sendAccum1(0);

    return 0;
}

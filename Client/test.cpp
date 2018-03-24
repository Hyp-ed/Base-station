#include "BaseCommunicator.cpp"

int main()
{
    // std :: thread (receiverThread);

    BaseCommunicator baseCommunicator;

    baseCommunicator.sendVelocity(6546);
    baseCommunicator.sendDistance(564);
    baseCommunicator.sendAcceleration(584);
    baseCommunicator.sendStripeCount(964);

    return 0;
}

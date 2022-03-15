package ru.otus.protobuf;

import io.grpc.ManagedChannelBuilder;
import ru.otus.protobuf.client.GetValuesObserver;
import ru.otus.protobuf.generated.ClientRequest;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;

import java.util.concurrent.CountDownLatch;

public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    private static final int FIRST_VALUE = 0;
    private static final int LAST_REQUEST_VALUE = 30;
    private static final int LAST_LOOP_VALUE = 50;
    private static final int SLEEP_MILLIS = 1000;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var latch = new CountDownLatch(1);
        var stub = RemoteDBServiceGrpc.newStub(channel);

        var request = ClientRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_REQUEST_VALUE)
                .build();

        int currentValue = FIRST_VALUE;

        var observer = new GetValuesObserver(currentValue, latch);
        stub.getValues(request, observer);

        int oldServerValue = FIRST_VALUE;

        for (int i = FIRST_VALUE; i < LAST_LOOP_VALUE; i++) {
            try {
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (observer) {
                int currentServerValue = observer.getCurrentValue();

                if (currentServerValue != oldServerValue) {
                    currentValue += currentServerValue;
                    oldServerValue = currentServerValue;
                }
                currentValue++;

                System.out.println("(iteration=" + (i + 1) + ") current value: " + currentValue);
            }
        }

        latch.await();
        channel.shutdown();
    }
}

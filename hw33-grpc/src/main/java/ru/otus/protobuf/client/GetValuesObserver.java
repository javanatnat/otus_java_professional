package ru.otus.protobuf.client;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.generated.ServerResponse;

import java.util.concurrent.CountDownLatch;

public class GetValuesObserver implements StreamObserver<ServerResponse> {
    private volatile int currentValue;
    private final CountDownLatch latch;

    public GetValuesObserver(
            int currentValue,
            CountDownLatch latch
    ) {
        this.currentValue = currentValue;
        this.latch = latch;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    @Override
    public void onNext(ServerResponse value) {
        currentValue = value.getValue();
        System.out.println("\tvalue from server: " + currentValue);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("error getValues: " + t);
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("end getValues");
        latch.countDown();
    }
}

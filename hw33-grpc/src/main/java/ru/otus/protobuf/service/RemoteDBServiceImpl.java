package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.generated.ClientRequest;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;
import ru.otus.protobuf.generated.ServerResponse;

public class RemoteDBServiceImpl extends RemoteDBServiceGrpc.RemoteDBServiceImplBase {

    private static final int SLEEP_MILLIS = 2000;

    @Override
    public void getValues(ClientRequest request, StreamObserver<ServerResponse> responseObserver) {
        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();

        int currentValue = firstValue;
        while (currentValue < lastValue) {
            try {
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentValue++;
            responseObserver.onNext(
                    ServerResponse
                            .newBuilder()
                            .setValue(currentValue)
                            .build()
            );
        }
        responseObserver.onCompleted();
    }
}

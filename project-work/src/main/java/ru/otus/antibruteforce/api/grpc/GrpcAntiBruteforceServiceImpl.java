package ru.otus.antibruteforce.api.grpc;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.antibruteforce.service.AntiBruteforceService;
import ru.otus.protobuf.generated.*;


@GRpcService
public class GrpcAntiBruteforceServiceImpl extends AntiBruteforceServiceGrpc.AntiBruteforceServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(AntiBruteforceServiceGrpc.class);

    private final AntiBruteforceService service;

    public GrpcAntiBruteforceServiceImpl(AntiBruteforceService service) {
        this.service = service;
    }

    @Override
    public void isBruteforce(BruteforceRequest request, StreamObserver<BruteforceResponse> responseObserver) {
        responseObserver.onNext(isBruteforce(request));
        responseObserver.onCompleted();
    }

    @Override
    public void dropBucket(DropBucketRequest request, StreamObserver<SimpleResponse> responseObserver) {
        LOG.debug("dropBucket: login={}, ip={}", request.getLogin(), request.getIp());
        responseObserver.onNext(doAction(() -> service.deleteByLoginIp(request.getLogin(), request.getIp())));
        responseObserver.onCompleted();
    }

    @Override
    public void addIpToBlacklist(IpListRequest request, StreamObserver<SimpleResponse> responseObserver) {
        LOG.debug("addIpToBlacklist: ip={}", request.getIp());
        responseObserver.onNext(doAction(() -> service.addIpBlacklist(request.getIp())));
        responseObserver.onCompleted();
    }

    @Override
    public void dropIpFromBlacklist(IpListRequest request, StreamObserver<SimpleResponse> responseObserver) {
        LOG.debug("dropIpFromBlacklist: ip={}", request.getIp());
        responseObserver.onNext(doAction(() -> service.deleteIpBlacklist(request.getIp())));
        responseObserver.onCompleted();
    }

    @Override
    public void addIpToWhitelist(IpListRequest request, StreamObserver<SimpleResponse> responseObserver) {
        LOG.debug("addIpToWhitelist: ip={}", request.getIp());
        responseObserver.onNext(doAction(() -> service.addIpWhitelist(request.getIp())));
        responseObserver.onCompleted();
    }

    @Override
    public void dropIpFromWhitelist(IpListRequest request, StreamObserver<SimpleResponse> responseObserver) {
        LOG.debug("dropIpFromWhitelist: ip={}", request.getIp());
        responseObserver.onNext(doAction(() -> service.deleteIpWhitelist(request.getIp())));
        responseObserver.onCompleted();
    }

    private BruteforceResponse isBruteforce(BruteforceRequest request) {
        LOG.debug("isBruteforce: login={}, password={}, ip={}",
                request.getLogin(),
                request.getPassword(),
                request.getIp()
        );
        if (service.isBruteforce(request.getLogin(), request.getPassword(), request.getIp())) {
            return BruteforceResponse.newBuilder().setValue(BruteforceResponse.AnswerType.YES).build();
        }
        return BruteforceResponse.newBuilder().setValue(BruteforceResponse.AnswerType.NO).build();
    }

    private SimpleResponse getOk() {
        return SimpleResponse.newBuilder().setOk(true).build();
    }

    private SimpleResponse getOkFalse() {
        return SimpleResponse.newBuilder().setOk(false).build();
    }

    private SimpleResponse doAction(Action action) {
        try {
            action.execute();
            return getOk();
        } catch (Exception ex) {
            LOG.error("Exception in doAction: {}", ex.getMessage());
            return getOkFalse();
        }
    }
}

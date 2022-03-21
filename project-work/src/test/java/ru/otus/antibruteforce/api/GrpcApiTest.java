package ru.otus.antibruteforce.api;

import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.otus.antibruteforce.api.grpc.GrpcAntiBruteforceServiceImpl;
import ru.otus.antibruteforce.service.*;
import ru.otus.protobuf.generated.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@SpringJUnitConfig(classes = { GrpcApiTest.Config.class })
public class GrpcApiTest {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "012345678";
    private static final String IP = "128.11.0.1";
    private static final String IP_WHITELIST = "128.11.0.2";
    private static final String IP_BLACKLIST = "128.11.0.3";
    private static final String IP_ERROR = "12811.0.1";

    private static final String LOGIN_BRUTEFORCE = "login_BRUT";
    private static final String ERROR_MSG = "error";

    static class Config {

        @Bean
        RedisService redisService() {
            RedisService redisService = mock(RedisService.class);

            given(redisService.isBruteforceByLogin(LOGIN)).willReturn(false);
            given(redisService.isBruteforceByLogin(LOGIN_BRUTEFORCE)).willReturn(true);

            return redisService;
        }

        @Bean
        BlacklistService blacklistService() {
            BlacklistService blacklistService = mock(BlacklistService.class);

            willThrow(new IllegalIpv4Exception(ERROR_MSG)).given(blacklistService).addIpBlacklist(IP_ERROR);
            willDoNothing().given(blacklistService).addIpBlacklist(IP_BLACKLIST);
            given(blacklistService.isInList(IP_BLACKLIST)).willReturn(true);

            return blacklistService;
        }

        @Bean
        WhitelistService whitelistService() {
            WhitelistService whitelistService = mock(WhitelistService.class);

            willThrow(new IllegalIpv4Exception(ERROR_MSG)).given(whitelistService).addIpWhitelist(IP_ERROR);
            willDoNothing().given(whitelistService).addIpWhitelist(IP_WHITELIST);
            given(whitelistService.isInList(IP_WHITELIST)).willReturn(true);

            return whitelistService;
        }

        @Bean
        AntiBruteforceService antiBruteforceService() {
            return new AntiBruteforceServiceImpl(
                    redisService(),
                    blacklistService(),
                    whitelistService());
        }

        @Bean
        GrpcAntiBruteforceServiceImpl grpcService() {
            return new GrpcAntiBruteforceServiceImpl(antiBruteforceService());
        }
    }

    @Autowired
    GrpcAntiBruteforceServiceImpl grpcService;

    @Test
    void isBruteforceFalseTest() {
        BruteforceRequest request = BruteforceRequest.newBuilder()
                .setLogin(LOGIN)
                .setPassword(PASSWORD)
                .setIp(IP)
                .build();

        StreamRecorder<BruteforceResponse> responseObserver = StreamRecorder.create();
        grpcService.isBruteforce(request, responseObserver);
        assertNull(responseObserver.getError());

        List<BruteforceResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        BruteforceResponse response = results.get(0);
        BruteforceResponse expected = BruteforceResponse.newBuilder()
                .setValue(BruteforceResponse.AnswerType.NO)
                .build();
        assertEquals(expected, response);
    }

    @Test
    void isBruteforceFalseWhitelistTest() {
        BruteforceRequest request = BruteforceRequest.newBuilder()
                .setLogin(LOGIN_BRUTEFORCE)
                .setPassword(PASSWORD)
                .setIp(IP_WHITELIST)
                .build();

        StreamRecorder<BruteforceResponse> responseObserver = StreamRecorder.create();
        grpcService.isBruteforce(request, responseObserver);
        assertNull(responseObserver.getError());

        List<BruteforceResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        BruteforceResponse response = results.get(0);
        BruteforceResponse expected = BruteforceResponse.newBuilder()
                .setValue(BruteforceResponse.AnswerType.NO)
                .build();
        assertEquals(expected, response);
    }

    @Test
    void isBruteforceTrueTest() {
        BruteforceRequest request = BruteforceRequest.newBuilder()
                .setLogin(LOGIN_BRUTEFORCE)
                .setPassword(PASSWORD)
                .setIp(IP)
                .build();

        StreamRecorder<BruteforceResponse> responseObserver = StreamRecorder.create();
        grpcService.isBruteforce(request, responseObserver);
        assertNull(responseObserver.getError());

        List<BruteforceResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        BruteforceResponse response = results.get(0);
        BruteforceResponse expected = BruteforceResponse.newBuilder().setValue(BruteforceResponse.AnswerType.YES).build();
        assertEquals(expected, response);
    }

    @Test
    void isBruteforceTrueBlacklistTest() {
        BruteforceRequest request = BruteforceRequest.newBuilder()
                .setLogin(LOGIN)
                .setPassword(PASSWORD)
                .setIp(IP_BLACKLIST)
                .build();

        StreamRecorder<BruteforceResponse> responseObserver = StreamRecorder.create();
        grpcService.isBruteforce(request, responseObserver);
        assertNull(responseObserver.getError());

        List<BruteforceResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        BruteforceResponse response = results.get(0);
        BruteforceResponse expected = BruteforceResponse.newBuilder().setValue(BruteforceResponse.AnswerType.YES).build();
        assertEquals(expected, response);
    }

    @Test
    void dropBucketTest() {
        DropBucketRequest request = DropBucketRequest.newBuilder()
                .setLogin(LOGIN)
                .setIp(IP)
                .build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.dropBucket(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(true).build();
        assertEquals(expected, response);
    }

    @Test
    void addIpToBlacklistTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_BLACKLIST).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.addIpToBlacklist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(true).build();
        assertEquals(expected, response);
    }

    @Test
    void addIpToBlacklistErrorTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_ERROR).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.addIpToBlacklist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(false).build();
        assertEquals(expected, response);
    }

    @Test
    void dropIpFromBlacklistTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_BLACKLIST).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.dropIpFromBlacklist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(true).build();
        assertEquals(expected, response);
    }

    @Test
    void addIpToWhitelistTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_WHITELIST).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.addIpToWhitelist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(true).build();
        assertEquals(expected, response);
    }

    @Test
    void addIpToWhitelistErrorTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_ERROR).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.addIpToWhitelist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(false).build();
        assertEquals(expected, response);
    }

    @Test
    void dropIpFromWhitelistTest() {
        IpListRequest request = IpListRequest.newBuilder().setIp(IP_WHITELIST).build();

        StreamRecorder<SimpleResponse> responseObserver = StreamRecorder.create();
        grpcService.dropIpFromWhitelist(request, responseObserver);
        assertNull(responseObserver.getError());

        List<SimpleResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        SimpleResponse response = results.get(0);
        SimpleResponse expected = SimpleResponse.newBuilder().setOk(true).build();
        assertEquals(expected, response);
    }

}

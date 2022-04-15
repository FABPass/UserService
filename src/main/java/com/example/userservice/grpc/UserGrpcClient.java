package com.example.userservice.grpc;

import com.nwt.fabpass.systemevents.ActionRequest;
import com.nwt.fabpass.systemevents.ActionResponse;
import com.nwt.fabpass.systemevents.ActionServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcClient {
    @GrpcClient("grpc-channel")
    ActionServiceGrpc.ActionServiceBlockingStub syncClient;

    public String log(String serviceName, String user, String method, String responseStatus) {
        try {
            ActionRequest req = ActionRequest.newBuilder().setServiceName(serviceName).setUser(user).setMethod(method).setResponseStatus(responseStatus).build();
            ActionResponse resp = syncClient.log(req);
            return resp.getResponseMesage();
        } catch (Exception e) {
            return "Exception during gRPC request";
        }
    }
}

# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import environment.grpc.jobshop_pb2 as jobshop__pb2


class EnvironmentStub(object):
    """Missing associated documentation comment in .proto file"""

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.ApplyAction = channel.unary_unary(
                '/env.Environment/ApplyAction',
                request_serializer=jobshop__pb2.MasAction.SerializeToString,
                response_deserializer=jobshop__pb2.MasActionResponse.FromString,
                )
        self.Reset = channel.unary_unary(
                '/env.Environment/Reset',
                request_serializer=jobshop__pb2.Empty.SerializeToString,
                response_deserializer=jobshop__pb2.MasState.FromString,
                )
        self.Render = channel.unary_unary(
                '/env.Environment/Render',
                request_serializer=jobshop__pb2.Empty.SerializeToString,
                response_deserializer=jobshop__pb2.Empty.FromString,
                )
        self.SetSeed = channel.unary_unary(
                '/env.Environment/SetSeed',
                request_serializer=jobshop__pb2.Seed.SerializeToString,
                response_deserializer=jobshop__pb2.Empty.FromString,
                )
        self.Setup = channel.unary_unary(
                '/env.Environment/Setup',
                request_serializer=jobshop__pb2.SettingsMsg.SerializeToString,
                response_deserializer=jobshop__pb2.SetupMsg.FromString,
                )


class EnvironmentServicer(object):
    """Missing associated documentation comment in .proto file"""

    def ApplyAction(self, request, context):
        """Apply an action to all agents
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def Reset(self, request, context):
        """Reset the environment
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def Render(self, request, context):
        """Show GUI
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def SetSeed(self, request, context):
        """Set the env seed
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def Setup(self, request, context):
        """Set and get Settings
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_EnvironmentServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'ApplyAction': grpc.unary_unary_rpc_method_handler(
                    servicer.ApplyAction,
                    request_deserializer=jobshop__pb2.MasAction.FromString,
                    response_serializer=jobshop__pb2.MasActionResponse.SerializeToString,
            ),
            'Reset': grpc.unary_unary_rpc_method_handler(
                    servicer.Reset,
                    request_deserializer=jobshop__pb2.Empty.FromString,
                    response_serializer=jobshop__pb2.MasState.SerializeToString,
            ),
            'Render': grpc.unary_unary_rpc_method_handler(
                    servicer.Render,
                    request_deserializer=jobshop__pb2.Empty.FromString,
                    response_serializer=jobshop__pb2.Empty.SerializeToString,
            ),
            'SetSeed': grpc.unary_unary_rpc_method_handler(
                    servicer.SetSeed,
                    request_deserializer=jobshop__pb2.Seed.FromString,
                    response_serializer=jobshop__pb2.Empty.SerializeToString,
            ),
            'Setup': grpc.unary_unary_rpc_method_handler(
                    servicer.Setup,
                    request_deserializer=jobshop__pb2.SettingsMsg.FromString,
                    response_serializer=jobshop__pb2.SetupMsg.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'env.Environment', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class Environment(object):
    """Missing associated documentation comment in .proto file"""

    @staticmethod
    def ApplyAction(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/env.Environment/ApplyAction',
            jobshop__pb2.MasAction.SerializeToString,
            jobshop__pb2.MasActionResponse.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def Reset(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/env.Environment/Reset',
            jobshop__pb2.Empty.SerializeToString,
            jobshop__pb2.MasState.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def Render(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/env.Environment/Render',
            jobshop__pb2.Empty.SerializeToString,
            jobshop__pb2.Empty.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def SetSeed(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/env.Environment/SetSeed',
            jobshop__pb2.Seed.SerializeToString,
            jobshop__pb2.Empty.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def Setup(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/env.Environment/Setup',
            jobshop__pb2.SettingsMsg.SerializeToString,
            jobshop__pb2.SetupMsg.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)
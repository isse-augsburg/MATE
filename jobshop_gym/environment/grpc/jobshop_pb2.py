# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: jobshop.proto

from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='jobshop.proto',
  package='env',
  syntax='proto3',
  serialized_options=b'\n\013jobshop.envB\010EnvProtoP\001\242\002\003JSE',
  serialized_pb=b'\n\rjobshop.proto\x12\x03\x65nv\"x\n\x11MasActionResponse\x12\x1c\n\x05state\x18\x01 \x01(\x0b\x32\r.env.MasState\x12\x1e\n\x06reward\x18\x02 \x03(\x0b\x32\x0e.env.RewardMsg\x12\x0c\n\x04\x64one\x18\x03 \x01(\x08\x12\x17\n\x04info\x18\x04 \x01(\x0b\x32\t.env.Info\",\n\x04Info\x12\x13\n\x0b\x63urrentStep\x18\x01 \x01(\x05\x12\x0f\n\x07timeout\x18\x02 \x01(\x05\"1\n\x0bSettingsMsg\x12\x0e\n\x06\x63onfig\x18\x01 \x01(\t\x12\x12\n\nclockSpeed\x18\x02 \x01(\x01\"U\n\x08SetupMsg\x12!\n\x08machines\x18\x01 \x03(\x0b\x32\x0f.env.MachineMsg\x12&\n\x08products\x18\x02 \x01(\x0b\x32\x14.env.ProductSetupMsg\"P\n\x08MasState\x12!\n\x08machines\x18\x01 \x03(\x0b\x32\x0f.env.MachineMsg\x12!\n\x08products\x18\x02 \x03(\x0b\x32\x0f.env.ProductMsg\"\xb9\x01\n\nMachineMsg\x12\"\n\x08position\x18\x01 \x01(\x0b\x32\x10.env.PositionMsg\x12#\n\x0binputBuffer\x18\x02 \x01(\x0b\x32\x0e.env.BufferMsg\x12$\n\x0coutputBuffer\x18\x03 \x01(\x0b\x32\x0e.env.BufferMsg\x12\x12\n\ncapability\x18\x04 \x01(\t\x12(\n\x0c\x63\x61pabilities\x18\x05 \x03(\x0b\x32\x12.env.CapabilityMsg\"\x98\x01\n\rCapabilityMsg\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0e\n\x06status\x18\x02 \x01(\x08\x12\x11\n\tsetupTime\x18\x03 \x01(\x05\x12\x16\n\x0eprocessingTime\x18\x04 \x01(\x05\x12\x13\n\x0b\x66\x61ilureRate\x18\x05 \x01(\x01\x12\x17\n\x0fmaintenanceTime\x18\x06 \x01(\x05\x12\x12\n\nrepairTime\x18\x07 \x01(\x05\"i\n\nProductMsg\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0c\n\x04size\x18\x02 \x01(\x01\x12\x0c\n\x04step\x18\x03 \x01(\x05\x12\x0f\n\x07\x61rrival\x18\x04 \x01(\x05\x12\x10\n\x08\x64uration\x18\x05 \x01(\x05\x12\x10\n\x08workflow\x18\x06 \x03(\t\"[\n\x0fProductSetupMsg\x12\x11\n\tbatchSize\x18\x01 \x01(\x05\x12\x12\n\nbatchCount\x18\x02 \x01(\x05\x12!\n\x08products\x18\x03 \x03(\x0b\x32\x0f.env.ProductMsg\"\x9a\x01\n\tRewardMsg\x12\x17\n\x0fglobalProcessed\x18\x01 \x01(\x05\x12\x16\n\x0elocalProcessed\x18\x02 \x01(\x05\x12\x10\n\x08makespan\x18\x03 \x01(\x05\x12\x10\n\x08\x66lowTime\x18\x04 \x01(\x01\x12\x11\n\ttardiness\x18\x05 \x01(\x01\x12\x10\n\x08lateness\x18\x06 \x01(\x01\x12\x13\n\x0butilization\x18\x07 \x01(\x04\")\n\x0bPositionMsg\x12\x0c\n\x04posX\x18\x01 \x01(\x01\x12\x0c\n\x04posY\x18\x02 \x01(\x01\".\n\tBufferMsg\x12\x0c\n\x04size\x18\x01 \x01(\x01\x12\x13\n\x0butilization\x18\x02 \x01(\x01\"(\n\tMasAction\x12\x1b\n\x06\x61\x63tion\x18\x01 \x03(\x0b\x32\x0b.env.Action\"/\n\x06\x41\x63tion\x12\x12\n\ncapability\x18\x01 \x01(\t\x12\x11\n\toperation\x18\x02 \x01(\t\"\x14\n\x04Seed\x12\x0c\n\x04seed\x18\x01 \x01(\x05\"\x07\n\x05\x45mpty2\xe0\x01\n\x0b\x45nvironment\x12\x37\n\x0b\x41pplyAction\x12\x0e.env.MasAction\x1a\x16.env.MasActionResponse\"\x00\x12$\n\x05Reset\x12\n.env.Empty\x1a\r.env.MasState\"\x00\x12\"\n\x06Render\x12\n.env.Empty\x1a\n.env.Empty\"\x00\x12\"\n\x07SetSeed\x12\t.env.Seed\x1a\n.env.Empty\"\x00\x12*\n\x05Setup\x12\x10.env.SettingsMsg\x1a\r.env.SetupMsg\"\x00\x42\x1f\n\x0bjobshop.envB\x08\x45nvProtoP\x01\xa2\x02\x03JSEb\x06proto3'
)




_MASACTIONRESPONSE = _descriptor.Descriptor(
  name='MasActionResponse',
  full_name='env.MasActionResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='state', full_name='env.MasActionResponse.state', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='reward', full_name='env.MasActionResponse.reward', index=1,
      number=2, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='done', full_name='env.MasActionResponse.done', index=2,
      number=3, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='info', full_name='env.MasActionResponse.info', index=3,
      number=4, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=22,
  serialized_end=142,
)


_INFO = _descriptor.Descriptor(
  name='Info',
  full_name='env.Info',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='currentStep', full_name='env.Info.currentStep', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='timeout', full_name='env.Info.timeout', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=144,
  serialized_end=188,
)


_SETTINGSMSG = _descriptor.Descriptor(
  name='SettingsMsg',
  full_name='env.SettingsMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='config', full_name='env.SettingsMsg.config', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='clockSpeed', full_name='env.SettingsMsg.clockSpeed', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=190,
  serialized_end=239,
)


_SETUPMSG = _descriptor.Descriptor(
  name='SetupMsg',
  full_name='env.SetupMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='machines', full_name='env.SetupMsg.machines', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='products', full_name='env.SetupMsg.products', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=241,
  serialized_end=326,
)


_MASSTATE = _descriptor.Descriptor(
  name='MasState',
  full_name='env.MasState',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='machines', full_name='env.MasState.machines', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='products', full_name='env.MasState.products', index=1,
      number=2, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=328,
  serialized_end=408,
)


_MACHINEMSG = _descriptor.Descriptor(
  name='MachineMsg',
  full_name='env.MachineMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='position', full_name='env.MachineMsg.position', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='inputBuffer', full_name='env.MachineMsg.inputBuffer', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='outputBuffer', full_name='env.MachineMsg.outputBuffer', index=2,
      number=3, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='capability', full_name='env.MachineMsg.capability', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='capabilities', full_name='env.MachineMsg.capabilities', index=4,
      number=5, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=411,
  serialized_end=596,
)


_CAPABILITYMSG = _descriptor.Descriptor(
  name='CapabilityMsg',
  full_name='env.CapabilityMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='env.CapabilityMsg.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='status', full_name='env.CapabilityMsg.status', index=1,
      number=2, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='setupTime', full_name='env.CapabilityMsg.setupTime', index=2,
      number=3, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='processingTime', full_name='env.CapabilityMsg.processingTime', index=3,
      number=4, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='failureRate', full_name='env.CapabilityMsg.failureRate', index=4,
      number=5, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='maintenanceTime', full_name='env.CapabilityMsg.maintenanceTime', index=5,
      number=6, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='repairTime', full_name='env.CapabilityMsg.repairTime', index=6,
      number=7, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=599,
  serialized_end=751,
)


_PRODUCTMSG = _descriptor.Descriptor(
  name='ProductMsg',
  full_name='env.ProductMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='env.ProductMsg.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='size', full_name='env.ProductMsg.size', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='step', full_name='env.ProductMsg.step', index=2,
      number=3, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='arrival', full_name='env.ProductMsg.arrival', index=3,
      number=4, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='duration', full_name='env.ProductMsg.duration', index=4,
      number=5, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='workflow', full_name='env.ProductMsg.workflow', index=5,
      number=6, type=9, cpp_type=9, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=753,
  serialized_end=858,
)


_PRODUCTSETUPMSG = _descriptor.Descriptor(
  name='ProductSetupMsg',
  full_name='env.ProductSetupMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='batchSize', full_name='env.ProductSetupMsg.batchSize', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='batchCount', full_name='env.ProductSetupMsg.batchCount', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='products', full_name='env.ProductSetupMsg.products', index=2,
      number=3, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=860,
  serialized_end=951,
)


_REWARDMSG = _descriptor.Descriptor(
  name='RewardMsg',
  full_name='env.RewardMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='globalProcessed', full_name='env.RewardMsg.globalProcessed', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='localProcessed', full_name='env.RewardMsg.localProcessed', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='makespan', full_name='env.RewardMsg.makespan', index=2,
      number=3, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='flowTime', full_name='env.RewardMsg.flowTime', index=3,
      number=4, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='tardiness', full_name='env.RewardMsg.tardiness', index=4,
      number=5, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='lateness', full_name='env.RewardMsg.lateness', index=5,
      number=6, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='utilization', full_name='env.RewardMsg.utilization', index=6,
      number=7, type=4, cpp_type=4, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=954,
  serialized_end=1108,
)


_POSITIONMSG = _descriptor.Descriptor(
  name='PositionMsg',
  full_name='env.PositionMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='posX', full_name='env.PositionMsg.posX', index=0,
      number=1, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='posY', full_name='env.PositionMsg.posY', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1110,
  serialized_end=1151,
)


_BUFFERMSG = _descriptor.Descriptor(
  name='BufferMsg',
  full_name='env.BufferMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='size', full_name='env.BufferMsg.size', index=0,
      number=1, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='utilization', full_name='env.BufferMsg.utilization', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1153,
  serialized_end=1199,
)


_MASACTION = _descriptor.Descriptor(
  name='MasAction',
  full_name='env.MasAction',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='action', full_name='env.MasAction.action', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1201,
  serialized_end=1241,
)


_ACTION = _descriptor.Descriptor(
  name='Action',
  full_name='env.Action',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='capability', full_name='env.Action.capability', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='operation', full_name='env.Action.operation', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1243,
  serialized_end=1290,
)


_SEED = _descriptor.Descriptor(
  name='Seed',
  full_name='env.Seed',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='seed', full_name='env.Seed.seed', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1292,
  serialized_end=1312,
)


_EMPTY = _descriptor.Descriptor(
  name='Empty',
  full_name='env.Empty',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=1314,
  serialized_end=1321,
)

_MASACTIONRESPONSE.fields_by_name['state'].message_type = _MASSTATE
_MASACTIONRESPONSE.fields_by_name['reward'].message_type = _REWARDMSG
_MASACTIONRESPONSE.fields_by_name['info'].message_type = _INFO
_SETUPMSG.fields_by_name['machines'].message_type = _MACHINEMSG
_SETUPMSG.fields_by_name['products'].message_type = _PRODUCTSETUPMSG
_MASSTATE.fields_by_name['machines'].message_type = _MACHINEMSG
_MASSTATE.fields_by_name['products'].message_type = _PRODUCTMSG
_MACHINEMSG.fields_by_name['position'].message_type = _POSITIONMSG
_MACHINEMSG.fields_by_name['inputBuffer'].message_type = _BUFFERMSG
_MACHINEMSG.fields_by_name['outputBuffer'].message_type = _BUFFERMSG
_MACHINEMSG.fields_by_name['capabilities'].message_type = _CAPABILITYMSG
_PRODUCTSETUPMSG.fields_by_name['products'].message_type = _PRODUCTMSG
_MASACTION.fields_by_name['action'].message_type = _ACTION
DESCRIPTOR.message_types_by_name['MasActionResponse'] = _MASACTIONRESPONSE
DESCRIPTOR.message_types_by_name['Info'] = _INFO
DESCRIPTOR.message_types_by_name['SettingsMsg'] = _SETTINGSMSG
DESCRIPTOR.message_types_by_name['SetupMsg'] = _SETUPMSG
DESCRIPTOR.message_types_by_name['MasState'] = _MASSTATE
DESCRIPTOR.message_types_by_name['MachineMsg'] = _MACHINEMSG
DESCRIPTOR.message_types_by_name['CapabilityMsg'] = _CAPABILITYMSG
DESCRIPTOR.message_types_by_name['ProductMsg'] = _PRODUCTMSG
DESCRIPTOR.message_types_by_name['ProductSetupMsg'] = _PRODUCTSETUPMSG
DESCRIPTOR.message_types_by_name['RewardMsg'] = _REWARDMSG
DESCRIPTOR.message_types_by_name['PositionMsg'] = _POSITIONMSG
DESCRIPTOR.message_types_by_name['BufferMsg'] = _BUFFERMSG
DESCRIPTOR.message_types_by_name['MasAction'] = _MASACTION
DESCRIPTOR.message_types_by_name['Action'] = _ACTION
DESCRIPTOR.message_types_by_name['Seed'] = _SEED
DESCRIPTOR.message_types_by_name['Empty'] = _EMPTY
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

MasActionResponse = _reflection.GeneratedProtocolMessageType('MasActionResponse', (_message.Message,), {
  'DESCRIPTOR' : _MASACTIONRESPONSE,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.MasActionResponse)
  })
_sym_db.RegisterMessage(MasActionResponse)

Info = _reflection.GeneratedProtocolMessageType('Info', (_message.Message,), {
  'DESCRIPTOR' : _INFO,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.Info)
  })
_sym_db.RegisterMessage(Info)

SettingsMsg = _reflection.GeneratedProtocolMessageType('SettingsMsg', (_message.Message,), {
  'DESCRIPTOR' : _SETTINGSMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.SettingsMsg)
  })
_sym_db.RegisterMessage(SettingsMsg)

SetupMsg = _reflection.GeneratedProtocolMessageType('SetupMsg', (_message.Message,), {
  'DESCRIPTOR' : _SETUPMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.SetupMsg)
  })
_sym_db.RegisterMessage(SetupMsg)

MasState = _reflection.GeneratedProtocolMessageType('MasState', (_message.Message,), {
  'DESCRIPTOR' : _MASSTATE,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.MasState)
  })
_sym_db.RegisterMessage(MasState)

MachineMsg = _reflection.GeneratedProtocolMessageType('MachineMsg', (_message.Message,), {
  'DESCRIPTOR' : _MACHINEMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.MachineMsg)
  })
_sym_db.RegisterMessage(MachineMsg)

CapabilityMsg = _reflection.GeneratedProtocolMessageType('CapabilityMsg', (_message.Message,), {
  'DESCRIPTOR' : _CAPABILITYMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.CapabilityMsg)
  })
_sym_db.RegisterMessage(CapabilityMsg)

ProductMsg = _reflection.GeneratedProtocolMessageType('ProductMsg', (_message.Message,), {
  'DESCRIPTOR' : _PRODUCTMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.ProductMsg)
  })
_sym_db.RegisterMessage(ProductMsg)

ProductSetupMsg = _reflection.GeneratedProtocolMessageType('ProductSetupMsg', (_message.Message,), {
  'DESCRIPTOR' : _PRODUCTSETUPMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.ProductSetupMsg)
  })
_sym_db.RegisterMessage(ProductSetupMsg)

RewardMsg = _reflection.GeneratedProtocolMessageType('RewardMsg', (_message.Message,), {
  'DESCRIPTOR' : _REWARDMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.RewardMsg)
  })
_sym_db.RegisterMessage(RewardMsg)

PositionMsg = _reflection.GeneratedProtocolMessageType('PositionMsg', (_message.Message,), {
  'DESCRIPTOR' : _POSITIONMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.PositionMsg)
  })
_sym_db.RegisterMessage(PositionMsg)

BufferMsg = _reflection.GeneratedProtocolMessageType('BufferMsg', (_message.Message,), {
  'DESCRIPTOR' : _BUFFERMSG,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.BufferMsg)
  })
_sym_db.RegisterMessage(BufferMsg)

MasAction = _reflection.GeneratedProtocolMessageType('MasAction', (_message.Message,), {
  'DESCRIPTOR' : _MASACTION,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.MasAction)
  })
_sym_db.RegisterMessage(MasAction)

Action = _reflection.GeneratedProtocolMessageType('Action', (_message.Message,), {
  'DESCRIPTOR' : _ACTION,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.Action)
  })
_sym_db.RegisterMessage(Action)

Seed = _reflection.GeneratedProtocolMessageType('Seed', (_message.Message,), {
  'DESCRIPTOR' : _SEED,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.Seed)
  })
_sym_db.RegisterMessage(Seed)

Empty = _reflection.GeneratedProtocolMessageType('Empty', (_message.Message,), {
  'DESCRIPTOR' : _EMPTY,
  '__module__' : 'jobshop_pb2'
  # @@protoc_insertion_point(class_scope:env.Empty)
  })
_sym_db.RegisterMessage(Empty)


DESCRIPTOR._options = None

_ENVIRONMENT = _descriptor.ServiceDescriptor(
  name='Environment',
  full_name='env.Environment',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=1324,
  serialized_end=1548,
  methods=[
  _descriptor.MethodDescriptor(
    name='ApplyAction',
    full_name='env.Environment.ApplyAction',
    index=0,
    containing_service=None,
    input_type=_MASACTION,
    output_type=_MASACTIONRESPONSE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='Reset',
    full_name='env.Environment.Reset',
    index=1,
    containing_service=None,
    input_type=_EMPTY,
    output_type=_MASSTATE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='Render',
    full_name='env.Environment.Render',
    index=2,
    containing_service=None,
    input_type=_EMPTY,
    output_type=_EMPTY,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='SetSeed',
    full_name='env.Environment.SetSeed',
    index=3,
    containing_service=None,
    input_type=_SEED,
    output_type=_EMPTY,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='Setup',
    full_name='env.Environment.Setup',
    index=4,
    containing_service=None,
    input_type=_SETTINGSMSG,
    output_type=_SETUPMSG,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_ENVIRONMENT)

DESCRIPTOR.services_by_name['Environment'] = _ENVIRONMENT

# @@protoc_insertion_point(module_scope)

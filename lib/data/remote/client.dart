import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/auth/geth_auth.dart';
import 'package:gethsemane/data/remote/converter.dart';
import 'package:gethsemane/data/remote/model/event.dart';
import 'package:gethsemane/data/remote/service/events.dart';

final gethClient = ChopperClient(
  baseUrl: Uri.parse('http://api.geth.by'),
  services: [EventsService.create()],
  converter: const JsonSerializableConverter({
    EventDTO: EventDTO.fromJsonFactory,
  }),
  interceptors: [HttpLoggingInterceptor(), GethAuthInterceptor()],
);

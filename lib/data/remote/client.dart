import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/auth/geth_auth.dart';
import 'package:gethsemane/data/remote/converter.dart';
import 'package:gethsemane/data/remote/model/event_dto.dart';
import 'package:gethsemane/data/remote/model/worship_dto.dart';
import 'package:gethsemane/data/remote/service/api_geth_mobile_service.dart';
import 'package:gethsemane/data/remote/service/api_geth_service.dart';

class HttpClients {
  final ChopperClient geth;

  HttpClients()
      : geth = ChopperClient(
          baseUrl: Uri.parse('http://api.geth.by'),
          services: [
            ApiGethService.create(),
            ApiGethMobileService.create(),
          ],
          converter: const JsonSerializableConverter({
            EventDTO: EventDTO.fromJsonFactory,
            WorshipDTO: WorshipDTO.fromJsonFactory,
          }),
          interceptors: [HttpLoggingInterceptor(), GethAuthInterceptor()],
        );
}

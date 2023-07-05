import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/auth/geth_auth.dart';
import 'package:gethsemane/data/remote/auth/gethsemane_auth.dart';
import 'package:gethsemane/data/remote/converter.dart';
import 'package:gethsemane/data/remote/model/author_dto.dart';
import 'package:gethsemane/data/remote/model/event_dto.dart';
import 'package:gethsemane/data/remote/model/music_group_dto.dart';
import 'package:gethsemane/data/remote/model/worship_dto.dart';
import 'package:gethsemane/data/remote/service/api_geth_mobile_service.dart';
import 'package:gethsemane/data/remote/service/api_geth_service.dart';
import 'package:gethsemane/data/remote/service/api_gethsemane_service.dart';

class HttpClients {
  final ChopperClient geth;
  final ChopperClient gethsemane;

  HttpClients()
      : geth = ChopperClient(
          baseUrl: Uri.parse('http://api.geth.by'),
          services: [
            ApiGethService.create(),
            ApiGethMobileService.create(),
          ],
          converter: const JsonSerializableConverter({
            AuthorDTO: AuthorDTO.fromJsonFactory,
            EventDTO: EventDTO.fromJsonFactory,
            WorshipDTO: WorshipDTO.fromJsonFactory,
          }),
          interceptors: [GethAuthInterceptor(), HttpLoggingInterceptor()],
        ),
        gethsemane = ChopperClient(
          baseUrl: Uri.parse('http://api.gethsemane.by'),
          services: [
            ApiGethsemaneService.create(),
          ],
          converter: const JsonSerializableConverter({
            MusicGroupDTO: MusicGroupDTO.fromJsonFactory,
          }),
          interceptors: [GethsemaneAuthInterceptor(), HttpLoggingInterceptor()],
        );
}

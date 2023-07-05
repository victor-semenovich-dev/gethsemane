import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/model/music_group_dto.dart';

part '../../../generated/data/remote/service/api_gethsemane_service.chopper.dart';

@ChopperApi()
abstract class ApiGethsemaneService extends ChopperService {
  static ApiGethsemaneService create([ChopperClient? client]) =>
      _$ApiGethsemaneService(client);

  @Get(path: '/MusicGroups')
  Future<Response<List<MusicGroupDTO>>> getMusicGroups();

  @Get(path: '/MusicGroups/{id}')
  Future<Response<List<MusicGroupDTO>>> getMusicGroup(@Path() int id);
}

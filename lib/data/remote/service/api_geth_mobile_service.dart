import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/model/worship_dto.dart';

part '../../../generated/data/remote/service/api_geth_mobile_service.chopper.dart';

@ChopperApi(baseUrl: '/mobile')
abstract class ApiGethMobileService extends ChopperService {
  static ApiGethMobileService create([ChopperClient? client]) =>
      _$ApiGethMobileService(client);

  @Get(path: '/worship/{id}')
  Future<Response<WorshipDTO>> getWorship(@Path() int id);
}

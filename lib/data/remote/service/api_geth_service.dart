import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/model/event_dto.dart';

part '../../../generated/data/remote/service/api_geth_service.chopper.dart';

@ChopperApi()
abstract class ApiGethService extends ChopperService {
  static ApiGethService create([ChopperClient? client]) =>
      _$ApiGethService(client);

  /// Fetch all events from the specified [date].
  /// If the [date] is not specified, fetch events from the current date.
  ///
  /// [date] - a date in the YYYY-MM-DD format.
  @Get(path: '/events')
  Future<Response<List<EventDTO>>> getEvents({@Query() String? date});
}

import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/model/event.dart';

part '../../../generated/data/remote/service/events.chopper.dart';

@ChopperApi(baseUrl: '/events')
abstract class EventsService extends ChopperService {
  static EventsService create([ChopperClient? client]) =>
      _$EventsService(client);

  /// Fetch all events from the specified [date].
  /// If the [date] is not specified, fetch events from the current date.
  ///
  /// [date] - a date in the YYYY-MM-DD format.
  @Get()
  Future<Response<List<EventDTO>>> getEvents({@Query() String? date});
}

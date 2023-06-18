import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/service/events.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:logging/logging.dart' as log;

class EventsRepositoryImpl extends EventsRepository {
  final ChopperClient _gethClient;

  EventsRepositoryImpl(this._gethClient);

  @override
  Future<void> loadEvents() async {
    final response = await _gethClient.getService<EventsService>().getEvents();
    if (response.isSuccessful) {
      log.Logger.root.log(log.Level.INFO, 'loaded events: ${response.body}');
    }
  }
}

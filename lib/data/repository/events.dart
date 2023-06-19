import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/events.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/repository/events.dart';

class EventsRepositoryImpl extends EventsRepository {
  final AppDatabase database;
  final ChopperClient gethClient;

  EventsRepositoryImpl({
    required this.database,
    required this.gethClient,
  });

  @override
  Future<void> loadEvents() async {
    final response = await gethClient.getService<EventsService>().getEvents();
    if (response.isSuccessful) {
      final eventDtoList = response.body;
      if (eventDtoList != null) {
        database.batch((batch) {
          batch.insertAllOnConflictUpdate(
            database.event,
            eventDtoList.map((dto) => eventDtoToDbEntity(dto)),
          );
        });
      }
    }
  }
}

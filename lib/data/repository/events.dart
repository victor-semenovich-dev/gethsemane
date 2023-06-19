import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/events.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/repository/events.dart';

class EventsRepositoryImpl extends EventsRepository {
  final AppDatabase database;
  final EventsService eventsService;

  EventsRepositoryImpl({
    required this.database,
    required this.eventsService,
  });

  @override
  Future<void> loadEvents() async {
    final response = await eventsService.getEvents(date: '2023-06-01');
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

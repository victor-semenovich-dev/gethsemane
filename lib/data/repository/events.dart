import 'package:drift/drift.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/events.dart';
import 'package:gethsemane/data/util/dateformat.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/repository/events.dart';

class EventsRepositoryImpl extends EventsRepository {
  final worshipEventId = 10;

  final AppDatabase database;
  final EventsService eventsService;

  EventsRepositoryImpl({
    required this.database,
    required this.eventsService,
  });

  @override
  Future<void> loadEvents({DateTime? dateFrom}) async {
    final response = await eventsService.getEvents(
        date: dateFrom == null ? null : yyyyMMdd.format(dateFrom));
    if (response.isSuccessful) {
      final eventDtoList = response.body;
      if (eventDtoList != null) {
        database.batch((batch) {
          batch.deleteAll(database.event);
          batch.insertAllOnConflictUpdate(
            database.event,
            eventDtoList.map((dto) => eventDtoToDbEntity(dto)),
          );
        });
      }
    }
  }

  @override
  Stream<List<EventData>> getActualWorshipEvents() {
    return (database.event.select()
          ..where((tbl) =>
              tbl.categoryId.equals(worshipEventId) & tbl.isDraft.equals(false))
          ..orderBy([
            (t) => OrderingTerm(expression: t.date, mode: OrderingMode.desc),
          ]))
        .watch();
  }
}

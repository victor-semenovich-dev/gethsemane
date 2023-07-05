import 'package:gethsemane/data/local/database.dart';

abstract class EventsRepository {
  /// Sync all events with the server from the specified [dateFrom].
  /// If the [dateFrom] is not specified, the default date will be chosen
  /// (30 days before now).
  ///
  /// All events before [dateFrom] will be removed.
  Future<void> loadEvents({DateTime? dateFrom});

  /// Get a list of worship events that are not marked as draft
  Stream<List<EventData>> getActualWorshipEvents();
}

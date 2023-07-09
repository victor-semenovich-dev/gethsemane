import 'package:drift/drift.dart';

class Song extends Table {
  @override
  String? get tableName => 'FSong';

  IntColumn get id => integer()();
  IntColumn get musicGroupId => integer()();
  IntColumn get eventId => integer().nullable()();
  TextColumn get title => text()();
  DateTimeColumn get date => dateTime()();
  TextColumn get remoteAudio => text()();
  TextColumn get localAudio => text().nullable()();
  BoolColumn get showInCatalog =>
      boolean().withDefault(const Constant(false))();

  @override
  Set<Column> get primaryKey => {id};
}

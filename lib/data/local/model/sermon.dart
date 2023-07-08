import 'package:drift/drift.dart';

class Sermon extends Table {
  @override
  String? get tableName => 'FSermon';

  IntColumn get id => integer()();
  TextColumn get title => text()();
  TextColumn get type => textEnum<SermonType>()();
  IntColumn get authorId => integer().nullable()();
  TextColumn get author => text().nullable()();
  DateTimeColumn get date => dateTime()();
  TextColumn get remoteAudio => text()();
  TextColumn get localAudio => text().nullable()();
  IntColumn get eventId => integer().nullable()();
  BoolColumn get showInCatalog =>
      boolean().withDefault(const Constant(false))();

  @override
  Set<Column> get primaryKey => {id};
}

enum SermonType { sermon, witness }

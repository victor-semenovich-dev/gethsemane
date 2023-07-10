import 'package:drift/drift.dart';

class Photo extends Table {
  @override
  String? get tableName => 'FPhoto';

  IntColumn get id => integer()();
  IntColumn get albumId => integer().nullable()();
  IntColumn get eventId => integer().nullable()();
  TextColumn get title => text().nullable()();
  DateTimeColumn get date => dateTime()();
  TextColumn get previewUrl => text()();
  TextColumn get remoteUrl => text()();
  TextColumn get localPath => text().nullable()();
  BoolColumn get isRecent => boolean().withDefault(const Constant(false))();

  @override
  Set<Column> get primaryKey => {id};
}

import 'package:drift/drift.dart';

class MusicGroup extends Table {
  @override
  String? get tableName => 'FMusicGroup';

  IntColumn get id => integer()();
  TextColumn get title => text().nullable()();
  TextColumn get history => text().nullable()();
  TextColumn get leader => text().nullable()();
  TextColumn get image => text().nullable()();
  BoolColumn get isActive => boolean()();

  @override
  Set<Column> get primaryKey => {id};
}

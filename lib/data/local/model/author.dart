import 'package:drift/drift.dart';

class Author extends Table {
  @override
  String? get tableName => 'FAuthor';

  IntColumn get id => integer()();
  TextColumn get name => text()();
  TextColumn get biography => text().nullable()();
  IntColumn get sermonsCount => integer().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

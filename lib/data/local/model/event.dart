import 'package:drift/drift.dart';

class Event extends Table {
  @override
  String? get tableName => 'FEvent';

  IntColumn get id => integer()();
  IntColumn get categoryId => integer()();
  TextColumn get title => text()();
  DateTimeColumn get date => dateTime()();
  TextColumn get note => text().nullable()();
  TextColumn get audio => text().nullable()();
  TextColumn get shortDesc => text().nullable()();
  BoolColumn get isDraft => boolean()();
  BoolColumn get isArchive => boolean()();
  IntColumn get musicGroupId => integer().nullable()();
  TextColumn get video => text().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

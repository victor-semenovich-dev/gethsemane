import 'package:drift/drift.dart';

class Worship extends Table {
  @override
  String? get tableName => 'FWorship';

  IntColumn get id => integer()();
  DateTimeColumn get date => dateTime()();
  TextColumn get title => text().nullable()();
  TextColumn get shortDesc => text().nullable()();
  TextColumn get audio => text().nullable()();
  TextColumn get video => text().nullable()();
  TextColumn get poster => text().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

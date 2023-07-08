import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:gethsemane/data/local/model/author.dart';
import 'package:gethsemane/data/local/model/event.dart';
import 'package:gethsemane/data/local/model/music_group.dart';
import 'package:gethsemane/data/local/model/sermon.dart';
import 'package:logging/logging.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

part '../../generated/data/local/database.g.dart';

@DriftDatabase(tables: [Event, Author, MusicGroup, Sermon])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;
}

LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    final dbFolder = await getApplicationDocumentsDirectory();
    Logger.root.log(Level.INFO, 'DB file storage path - $dbFolder');
    final file = File(join(dbFolder.path, 'db.sqlite'));
    return NativeDatabase.createInBackground(file);
  });
}

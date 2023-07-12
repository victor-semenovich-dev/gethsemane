import 'package:drift/drift.dart';
import 'package:flutter/material.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/local/model/sermon.dart';
import 'package:gethsemane/data/remote/service/api_geth_mobile_service.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/model/worship_extended.dart';
import 'package:gethsemane/domain/repository/worships_repository.dart';
import 'package:stream_transform/stream_transform.dart';

class WorshipsRepositoryImpl extends WorshipsRepository {
  final AppDatabase database;
  final ApiGethMobileService apiGethMobileService;

  WorshipsRepositoryImpl({
    required this.database,
    required this.apiGethMobileService,
  });

  @override
  Future<void> loadWorship(int id) async {
    final response = await apiGethMobileService.getWorship(id);
    if (response.isSuccessful) {
      final worshipDto = response.body;
      if (worshipDto != null) {
        await database.batch((batch) async {
          batch.insert(database.worship, worshipDtoToDbEntity(worshipDto),
              mode: InsertMode.insertOrReplace);

          // sermons and witnesses
          batch.update(
            database.sermon,
            const SermonCompanion(eventId: Value(null)),
            where: ((sermon) => sermon.eventId.equals(id)),
          );
          batch.insertAllOnConflictUpdate(
              database.sermon,
              worshipDto.sermons.map((dto) => worshipSermonDtoToDbEntity(dto,
                  id, SermonType.sermon, DateUtils.dateOnly(worshipDto.date))));
          batch.insertAllOnConflictUpdate(
              database.sermon,
              worshipDto.witnesses.map((dto) => worshipSermonDtoToDbEntity(
                  dto,
                  id,
                  SermonType.witness,
                  DateUtils.dateOnly(worshipDto.date))));

          // songs
          batch.update(
            database.song,
            const SongCompanion(eventId: Value(null)),
            where: ((song) => song.eventId.equals(id)),
          );
          batch.insertAllOnConflictUpdate(
              database.song,
              worshipDto.songs.map((dto) => worshipSongDtoToDbEntity(
                  dto, id, DateUtils.dateOnly(worshipDto.date))));

          // photos
          batch.update(
            database.photo,
            const PhotoCompanion(eventId: Value(null)),
            where: ((photo) => photo.eventId.equals(id)),
          );
          batch.insertAllOnConflictUpdate(
              database.photo,
              worshipDto.photos
                  .map((dto) => worshipPhotoDtoToDbEntity(dto, id)));
        });
      }
    } else {
      throw response.error ?? 'An error occurred: ${response.statusCode}';
    }
  }

  @override
  Stream<WorshipExtended> getWorship(int id) {
    final worshipDataStream = (database.select(database.worship)
          ..where((tbl) => tbl.id.equals(id)))
        .watchSingle();
    final sermonListDataStream = (database.select(database.sermon)
          ..where((tbl) => tbl.eventId.equals(id))
          ..orderBy([(tbl) => OrderingTerm(expression: tbl.title)]))
        .watch();
    final songListDataStream = (database.select(database.song)
          ..where((tbl) => tbl.eventId.equals(id))
          ..orderBy([(tbl) => OrderingTerm(expression: tbl.title)]))
        .watch();
    final photoListDataStream = (database.select(database.photo)
          ..where((tbl) => tbl.eventId.equals(id))
          ..orderBy([(tbl) => OrderingTerm(expression: tbl.id)]))
        .watch();
    return worshipDataStream.cast<dynamic>().combineLatestAll([
      sermonListDataStream,
      songListDataStream,
      photoListDataStream
    ]).map((data) => WorshipExtended(
          worshipData: data[0],
          sermonDataList: data[1],
          songDataList: data[2],
          photoDataList: data[3],
        ));
  }
}

import 'package:drift/drift.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/local/model/sermon.dart';
import 'package:gethsemane/data/remote/model/author_dto.dart';
import 'package:gethsemane/data/remote/model/event_dto.dart';
import 'package:gethsemane/data/remote/model/music_group_dto.dart';
import 'package:gethsemane/data/remote/model/worship_dto.dart';

AuthorCompanion authorDtoToDbEntity(AuthorDTO dto) => AuthorCompanion(
      id: Value(dto.id),
      name: Value(dto.name),
      biography: Value(dto.biography),
      sermonsCount: Value(dto.sermonsCount),
    );

EventCompanion eventDtoToDbEntity(EventDTO dto) => EventCompanion(
      id: Value(dto.id),
      categoryId: Value(dto.categoryId),
      title: Value(dto.title),
      date: Value(dto.date),
      note: Value(dto.note),
      audio: Value(dto.audio),
      shortDesc: Value(dto.shortDesc),
      isDraft: Value(dto.isDraft > 0),
      isArchive: Value(dto.isArchive > 0),
      musicGroupId: Value(dto.musicGroupId),
      video: Value(dto.video),
    );

MusicGroupCompanion musicGroupDtoToDbEntity(MusicGroupDTO dto) =>
    MusicGroupCompanion(
      id: Value(dto.id),
      title: Value(dto.title),
      history: Value(dto.history),
      leader: Value(dto.leader),
      image: Value(dto.image),
      isActive: Value(dto.isActive),
    );

WorshipCompanion worshipDtoToDbEntity(WorshipDTO dto) => WorshipCompanion(
      id: Value(dto.id),
      date: Value(dto.date),
      title: Value(dto.title),
      shortDesc: Value(dto.shortDesc),
      audio: Value(dto.audio),
      video: Value(dto.video),
      poster: Value(dto.poster),
    );

SermonCompanion worshipSermonDtoToDbEntity(
  WorshipSermonDTO dto,
  int eventId,
  SermonType type,
  DateTime date,
) =>
    SermonCompanion(
      id: Value(dto.id),
      title: Value(dto.title),
      type: Value(type),
      authorId: Value(dto.authorId),
      date: Value(date),
      remoteAudio: Value(dto.audio),
      eventId: Value(eventId),
    );

SongCompanion worshipSongDtoToDbEntity(
  WorshipSongDTO dto,
  int eventId,
  DateTime date,
) =>
    SongCompanion(
      id: Value(dto.id),
      musicGroupId: Value(dto.musicGroupId),
      eventId: Value(eventId),
      title: Value(dto.title),
      date: Value(date),
      remoteAudio: Value(dto.audio),
    );

PhotoCompanion worshipPhotoDtoToDbEntity(WorshipPhotoDTO dto, int eventId) =>
    PhotoCompanion(
      id: Value(dto.id),
      eventId: Value(eventId),
      title: Value(dto.title),
      date: Value(dto.date),
      previewUrl: Value(dto.preview),
      remoteUrl: Value(dto.photo),
    );

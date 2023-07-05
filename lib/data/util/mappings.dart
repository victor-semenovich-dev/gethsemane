import 'package:drift/drift.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/model/author_dto.dart';
import 'package:gethsemane/data/remote/model/event_dto.dart';
import 'package:gethsemane/data/remote/model/music_group_dto.dart';

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

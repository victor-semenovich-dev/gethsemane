import 'package:drift/drift.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/model/event.dart';

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

import 'package:json_annotation/json_annotation.dart';

part '../../../generated/data/remote/model/event.g.dart';

@JsonSerializable(fieldRename: FieldRename.snake)
class EventDTO {
  final int id;
  final int categoryId;
  final String title;
  final DateTime date;
  final String? note;
  final String? audio;
  final String? shortDesc;
  final int isDraft;
  final int isArchive;
  final int? musicGroupId;
  final String? video;

  EventDTO({
    required this.id,
    required this.categoryId,
    required this.title,
    required this.date,
    required this.note,
    required this.audio,
    required this.shortDesc,
    required this.isDraft,
    required this.isArchive,
    required this.musicGroupId,
    required this.video,
  });

  static const fromJsonFactory = _$EventDTOFromJson;

  @override
  String toString() {
    return 'EventDTO{id: $id, categoryId: $categoryId, title: $title, date: $date, note: $note, audio: $audio, shortDesc: $shortDesc, isDraft: $isDraft, isArchive: $isArchive, musicGroupId: $musicGroupId, video: $video}';
  }
}

import 'package:json_annotation/json_annotation.dart';

part '../../../generated/data/remote/model/worship_dto.g.dart';

@JsonSerializable(fieldRename: FieldRename.snake, createToJson: false)
class WorshipDTO {
  final int id;
  final DateTime date;
  final String? title;
  final String? shortDesc;
  final String? audio;
  final String? video;
  final String? poster;
  final List<WorshipSermonDTO> sermons;
  final List<WorshipSermonDTO> witnesses;
  final List<WorshipSongDTO> songs;
  final List<WorshipPhotoDTO> photos;

  WorshipDTO({
    required this.id,
    required this.date,
    required this.title,
    required this.shortDesc,
    required this.audio,
    required this.video,
    required this.poster,
    required this.sermons,
    required this.witnesses,
    required this.songs,
    required this.photos,
  });

  static const fromJsonFactory = _$WorshipDTOFromJson;

  @override
  String toString() {
    return 'WorshipDTO{id: $id, date: $date, title: $title, shortDesc: $shortDesc, audio: $audio, video: $video, poster: $poster, sermons: $sermons, witnesses: $witnesses, songs: $songs, photos: $photos}';
  }
}

@JsonSerializable(fieldRename: FieldRename.snake, createToJson: false)
class WorshipSermonDTO {
  final int id;
  final String title;
  final int authorId;
  final String audio;

  WorshipSermonDTO({
    required this.id,
    required this.title,
    required this.authorId,
    required this.audio,
  });

  factory WorshipSermonDTO.fromJson(Map<String, dynamic> json) =>
      _$WorshipSermonDTOFromJson(json);

  @override
  String toString() {
    return 'WorshipSermonDTO{id: $id, title: $title, authorId: $authorId, audio: $audio}';
  }
}

@JsonSerializable(fieldRename: FieldRename.snake, createToJson: false)
class WorshipSongDTO {
  final int id;
  final String title;
  final int musicGroupId;
  final String audio;

  WorshipSongDTO({
    required this.id,
    required this.title,
    required this.musicGroupId,
    required this.audio,
  });

  factory WorshipSongDTO.fromJson(Map<String, dynamic> json) =>
      _$WorshipSongDTOFromJson(json);

  @override
  String toString() {
    return 'WorshipSongDTO{id: $id, title: $title, musicGroupId: $musicGroupId, audio: $audio}';
  }
}

@JsonSerializable(fieldRename: FieldRename.snake, createToJson: false)
class WorshipPhotoDTO {
  final int id;
  final String? title;
  final String preview;
  final String photo;
  final DateTime date;

  WorshipPhotoDTO({
    required this.id,
    required this.title,
    required this.preview,
    required this.photo,
    required this.date,
  });

  factory WorshipPhotoDTO.fromJson(Map<String, dynamic> json) =>
      _$WorshipPhotoDTOFromJson(json);

  @override
  String toString() {
    return 'WorshipPhotoDTO{id: $id, title: $title, preview: $preview, photo: $photo, date: $date}';
  }
}

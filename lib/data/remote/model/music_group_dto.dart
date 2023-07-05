import 'package:json_annotation/json_annotation.dart';

part '../../../generated/data/remote/model/music_group_dto.g.dart';

@JsonSerializable(createToJson: false)
class MusicGroupDTO {
  final int id;
  final String? title;
  final String? history;
  final String? leader;
  final String? image;
  final bool isActive;

  MusicGroupDTO({
    required this.id,
    required this.title,
    required this.history,
    required this.leader,
    required this.image,
    required this.isActive,
  });

  static const fromJsonFactory = _$MusicGroupDTOFromJson;

  @override
  String toString() {
    return 'MusicGroupDTO{id: $id, title: $title, history: $history, leader: $leader, image: $image, isActive: $isActive}';
  }
}
